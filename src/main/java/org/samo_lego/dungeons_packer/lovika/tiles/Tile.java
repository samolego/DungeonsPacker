package org.samo_lego.dungeons_packer.lovika.tiles;

import com.google.gson.annotations.SerializedName;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.Utils;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.lovika.block_conversion.IDungeonsConvertable;
import org.samo_lego.dungeons_packer.lovika.region.Region;
import org.samo_lego.dungeons_packer.lovika.block_conversion.DungeonBlockIdProvider;
import org.samo_lego.dungeons_packer.lovika.region.RegionLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public record Tile(
        String id,
        BlockPos pos,
        Vec3i size,
        String blocks,
        @SerializedName("region-plane") String regionPlane,
        @SerializedName("height-plane") String heightPlane,
        @SerializedName("region-y-plane") String regionYPlane,
        @SerializedName("walkable-plane") String walkablePlane,
        List<Door> doors,
        List<RegionLike> regions
) {
    public Tile {
        if (doors == null) {
            doors = new ArrayList<>();
        }
        if (regions == null) {
            regions = new ArrayList<>();
        }
    }


    public static Optional<Tile> fromTileCornerBlock(CommandSourceStack playerConverting, TileCornerBlockEntity cornerBlockEntity, DungeonBlockIdProvider resourceGen) {
        var tileBox = cornerBlockEntity.getRenderableBox();
        var pos = tileBox.localPos().offset(cornerBlockEntity.getBlockPos());
        var size = tileBox.size();
        var doors = new ArrayList<Door>();
        var regions = new ArrayList<RegionLike>();


        int height = size.getY();
        int width = size.getX();
        int depth = size.getZ();

        final int arraySize = height * depth * width;

        // Dungeon block IDs
        // We pack them either into single bytes or double bytes
        // depending on whether we need 16 bit ids
        short[] blockIds = new short[arraySize];
        // Half the size as we merge odd and even block data into a single byte
        byte[] blockData = new byte[Math.ceilDiv(arraySize, 2)];
        boolean need16bitIds = false;


        int planeSize = width * depth;

        byte[] regionPlane = new byte[planeSize];
        Arrays.fill(regionPlane, (byte) 2);

        byte[] heightPlane = new byte[planeSize];
        byte[] walkablePlane = new byte[planeSize];

        // This is taken from Dokucraft world converter,
        // all credit goes to them
        // They have an awesome guide :)
        var localPos = new BlockPos.MutableBlockPos();
        var missing = new HashSet<Block>();

        // We do +3 here as we process planes in
        // lag-behind style
        for (int y = 0; y < height + 3; ++y) {
            for (int z = 0; z < depth; ++z) {
                for (int x = 0; x < width; ++x) {

                    int blockIdx = (y * depth + z) * width + x;
                    short converted = BlockMap.DUNGEONS_AIR;
                    if (y < height) {
                        localPos.set(x, y, z);
                        var absolutePos = pos.offset(localPos);
                        var blockState = cornerBlockEntity.getLevel().getBlockState(absolutePos);

                        // Do the block conversion
                        if (blockState.getBlock() instanceof IDungeonsConvertable cnv) {
                            converted = cnv.dungeons_packer$convertToDungeons(cornerBlockEntity.getLevel(), absolutePos, localPos.immutable(), doors, regions);
                        } else {
                            //var ids = BlockMap.toDungeonBlockId(blockState);
                            var ids = resourceGen.requestId(blockState);
                            if (ids != -1) {
                                converted = ids;
                            } else {
                                missing.add(blockState.getBlock());
                            }
                        }

                        short convBlockId = (short) (converted >> 4);
                        byte convertedData = (byte) (converted & 0x0F);

                        blockIds[blockIdx] = convBlockId;
                        // Decide whether we need 16 bit block ids
                        need16bitIds |= convBlockId > 0xFF;

                        // Write block data
                        var dataIdx = blockIdx / 2;
                        byte existingData = blockData[dataIdx];
                        existingData <<= ((blockIdx % 2) * 4);
                        existingData |= convertedData;
                        blockData[dataIdx] = existingData;
                    }

                    // Other planes
                    int targetY = y - 3;
                    if (targetY >= 0) {
                        int planeIdx = z * width + x;
                        int targetIdx = blockIdx - 3 * planeSize;
                        short targetBlockId = blockIds[targetIdx];

                        if (targetBlockId != BlockMap.DUNGEONS_AIR) {
                            heightPlane[planeIdx] = (byte) targetY;
                            walkablePlane[planeIdx] = (byte) (targetY + 1);

                            boolean aboveT1 =(targetY + 1 >= height) || blockIds[targetIdx + planeSize] == BlockMap.DUNGEONS_AIR;
                            boolean aboveT2 = (targetY + 2 >= height) || blockIds[targetIdx + 2 * planeSize] == BlockMap.DUNGEONS_AIR;
                            boolean aboveT3 = (targetY + 3 >= height) || blockIds[blockIdx] == BlockMap.DUNGEONS_AIR;

                            // If all 3 blocks are air, the target is a walkable floor
                            // If hasCeiling, then we are in a tunnel
                            byte currentType = regionPlane[planeIdx];
                            if (aboveT1 && aboveT2 && aboveT3) {
                                if (currentType != 0 && currentType != 3) {
                                    // First time finding a floor in this column
                                    regionPlane[planeIdx] = (byte) 0;
                                }
                            } else if (currentType != 0 && currentType != 3) {
                                // Only overwrite if we haven't found a floor (0/3) yet
                                // Block is solid but no headroom -> it's a wall
                                regionPlane[planeIdx] = 4;
                            } else if (currentType == 0) {
                                // We previously thought this was a floor, but now we found a ceiling block at y+3
                                // This means it's actually a tunnel, so we update the region type to 3
                                regionPlane[planeIdx] = 3;
                            }
                        }
                    }
                }
            }
        }

        // 2D Plane Generation (Top-Down Scan)

        // Initialize everything to 0/Void
        Arrays.fill(regionPlane, (byte) 2);
        Arrays.fill(heightPlane, (byte) 0);
        Arrays.fill(walkablePlane, (byte) 0);

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                int planeIdx = z * width + x;

                // Scan from top to bottom
                for (int y = height - 1; y >= 0; y--) {
                    int idx = (y * depth + z) * width + x;
                    if (blockIds[idx] == 0) continue; // Skip air

                    // HEIGHT PLANE: Following Python Tile.py exactly
                    // Only the VERY highest non-air block sets the height-plane
                    if (heightPlane[planeIdx] == 0) {
                        heightPlane[planeIdx] = (byte) (y + 1);
                    }

                    // Check for headroom (Need air at y+1 and y+2)
                    boolean air1 = (y + 1 >= height) || blockIds[idx + planeSize] == 0;
                    boolean air2 = (y + 2 >= height) || blockIds[idx + 2 * planeSize] == 0;

                    if (air1 && air2) {
                        // --- FOUND A WALKABLE SURFACE ---
                        // According to Dungeons, if we find a floor, we STOP searching this column

                        // Walkable Plane: Top solid block + 1
                        walkablePlane[planeIdx] = (byte) (y + 1);

                        // Ceiling Check: Is there a solid block at targetY + 3?
                        boolean hasCeiling = false;
                        for (int cy = y + 3; cy < height; cy++) {
                            if (blockIds[(cy * depth + z) * width + x] != 0) {
                                hasCeiling = true;
                                break;
                            }
                        }
                        regionPlane[planeIdx] = hasCeiling ? (byte) 3 : (byte) 0;

                        break; // STOP column scan here
                    } else {
                        // --- FOUND A WALL ---
                        // If we haven't found a walkable floor yet, mark as wall (4)
                        if (regionPlane[planeIdx] == 2) {
                            regionPlane[planeIdx] = 4;
                            // walkablePlane stays 0
                        }
                    }
                }
            }
        }

        var idsSize = need16bitIds ? arraySize * 2 : arraySize;
        byte[] blocks = new byte[idsSize + blockData.length];
        System.arraycopy(blockData, 0, blocks, idsSize, blockData.length);

        // Transfer the block ids into the blocks
        if (need16bitIds) {
            for (int i = 0; i < blockIds.length; ++i) {
                blocks[2 * i] = (byte) (blockIds[i] >> 8);
                blocks[2 * i + 1] = (byte) blockIds[i];
            }
        } else {
            for (int i = 0; i < blockIds.length; ++i) {
                blocks[i] = (byte) blockIds[i];
            }
        }

        String encodedBlocks = Utils.compressAndEncode(blocks);
        String encodedHeight = Utils.compressAndEncode(heightPlane);
        String encodedRegion = Utils.compressAndEncode(regionPlane);
        // Currently we just use the same
        // todo for one day
        String encodedRegionY = encodedHeight;
        String encodedWalkable = Utils.compressAndEncode(walkablePlane);

        for (var miss: missing) {
            var msg = Component.translatable("message.conversion.block_missing", Component.translatable(miss.getDescriptionId()));
            playerConverting.sendSystemMessage(msg);
        }

        return Optional.of(new Tile(
                String.format("from_%s_to_%s", posToStr(pos), posToStr(pos.offset(size))),
                pos,
                size,
                encodedBlocks,
                encodedRegion,
                encodedHeight,
                encodedRegionY,
                encodedWalkable,
                doors,
                regions
        ));
    }

    private static String posToStr(Vec3i pos) {
        return String.format("x%d_y%d_z%d", pos.getX(), pos.getY(), pos.getZ());
    }
}
