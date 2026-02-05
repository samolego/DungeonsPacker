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

import java.util.ArrayList;
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
        List<Door> doors,
        List<Region> regions
) {
    public Tile {
        if (doors == null) {
            doors = new ArrayList<>();
        }
        if (regions == null) {
            regions = new ArrayList<>();
        }
    }


    public static Optional<Tile> fromTileCornerBlock(CommandSourceStack playerConverting, TileCornerBlockEntity cornerBlockEntity) {
        var tileBox = cornerBlockEntity.getRenderableBox();
        var pos = tileBox.localPos().offset(cornerBlockEntity.getBlockPos());
        var size = tileBox.size();
        var doors = new ArrayList<Door>();
        var regions = new ArrayList<Region>();

        final int arraySize = size.getY() * size.getZ() * size.getX();
        short[] blockIds = new short[arraySize];
        // Half the size as we merge odd and even block data into a single byte
        byte[] blockData = new byte[Math.ceilDiv(arraySize, 2)];

        boolean need16bitIds = false;
        // This is taken from Dokucraft world converter,
        // all credit goes to them
        var localPos = new BlockPos.MutableBlockPos();
        var missing = new HashSet<Block>();
        for (int y = 0; y < size.getY(); ++y) {
            for (int z = 0; z < size.getZ(); ++z) {
                for (int x = 0; x < size.getX(); ++x) {
                    localPos.set(x, y, z);
                    var absolutePos = pos.offset(localPos);
                    var blockState = cornerBlockEntity.getLevel().getBlockState(absolutePos);

                    short converted = BlockMap.DUNGEONS_AIR;

                    if (blockState.getBlock() instanceof IDungeonsConvertable cnv) {
                        converted = cnv.dungeons_packer$convertToDungeons(cornerBlockEntity.getLevel(), absolutePos, localPos, doors, regions);
                    } else {
                        var ids = BlockMap.toDungeonBlockId(blockState);
                        if (ids.isPresent()) {
                            converted = ids.get();
                        } else {
                            missing.add(blockState.getBlock());
                        }
                    }

                    int blockIdx = (y * size.getZ() + z) * size.getX() + x;
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

        for (var miss: missing) {
            var msg = Component.translatable("message.conversion.block_missing", miss);
            playerConverting.sendSystemMessage(msg);
        }

        return Optional.of(new Tile(
                String.format("from_%s_to_%s", posToStr(pos), posToStr(pos.offset(size))),
                pos,
                size,
                encodedBlocks,
                "",
                "",
                "",
                doors,
                regions
        ));
    }

    private static String posToStr(Vec3i pos) {
        return String.format("x%d_y%d_z%d", pos.getX(), pos.getY(), pos.getZ());
    }
}
