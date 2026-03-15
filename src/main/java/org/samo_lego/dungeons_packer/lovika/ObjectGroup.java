package org.samo_lego.dungeons_packer.lovika;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.level.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.block_conversion.DungeonBlockIdProvider;
import org.samo_lego.dungeons_packer.lovika.tiles.DungeonsHandler;
import org.samo_lego.dungeons_packer.lovika.tiles.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.samo_lego.dungeons_packer.lovika.tiles.DungeonsHandler.GSON;

public class ObjectGroup {
    private final TreeMap<ITilePos, TileCornerBlockEntity> tiles;

    public ObjectGroup() {
        this.tiles = new TreeMap<>();
    }


    public String generateJson(Tile[] tiles) {
        return GSON.toJson(Map.of("objects", tiles));
    }

    public Tile[] getTiles(ServerPlayer player, DungeonBlockIdProvider resourceGen, Map<String, List<int[]>> prefabs) {
        Tile[] tiles = new Tile[this.tiles.size()];
        int i = 0;
        for (var be : this.tiles.values()) {
            var tile = Tile.fromTileCornerBlock(player, be, resourceGen, i, prefabs);
            if (tile.isPresent()) {
                tiles[i] = tile.get();
            }
            ++i;
        }

        return tiles;
    }

    public void addTileCorner(TileCornerBlockEntity cornerBlock) {
        assert cornerBlock.isMainCorner() : "Only main corners should be added to the tile";

        this.tiles.put(new TileCornerArea(cornerBlock), cornerBlock);
    }

    public Optional<TileCornerBlockEntity> getTileCornerContaining(BlockPos pos) {
        var corner = this.tiles.get(new TileBlockPos(pos));
        return Optional.ofNullable(corner);
    }

    public void removeCorner(TileCornerBlockEntity be) {
        this.tiles.remove(new TileBlockPos(be.getBlockPos()));
    }


    private record TileBlockPos(BlockPos pos) implements ITilePos { }

    private class TileCornerArea implements ITilePos {
        public final BlockPos minPos;
        public final BlockPos maxPos;

        public TileCornerArea(TileCornerBlockEntity edge) {
            var pos1 = edge.getBlockPos();
            var pos2 = edge.getMatchingCornerPos()
                    .orElseThrow(() -> new IllegalArgumentException("TileCornerBlockEntity should have a matching corner"));

            this.minPos = BlockPos.min(pos1, pos2);
            this.maxPos = BlockPos.max(pos1, pos2);
        }
    }

    /**
     * A hacky way to get info faster, whether a block is in a tile.
     */
    private interface ITilePos extends Comparable<ITilePos> {

        @Override
        default int compareTo(ObjectGroup.ITilePos othr) {
            if (othr == null) {
                return -1;
            }
            // if other object is tile corner and the blockpos is in
            // conatined in the tile, return 0
            // if it's out +, return >0
            // else return < 0
            TileCornerArea comparingArea;
            BlockPos comparingPos;
            int switched;

            switch (this) {
                case TileBlockPos blockPos when othr instanceof TileCornerArea corner -> {
                    switched = -1;
                    comparingArea = corner;
                    comparingPos = blockPos.pos;
                }
                case TileCornerArea cornerPos when othr instanceof TileBlockPos blockPos -> {
                    comparingArea = cornerPos;
                    comparingPos = blockPos.pos;
                    switched = 1;
                }
                case TileCornerArea corner1 when othr instanceof TileCornerArea corner2 -> {
                    comparingArea = corner1;
                    comparingPos = corner2.minPos;
                    switched = 1;
                }
                default -> {
                    DungeonsPacker.LOGGER.warn("Comparing two tile blocks, which should not happen. This may cause incorrect behavior.");
                    return this.compareTo(othr);
                }
            }


            return switched * Utils.isInBounds(comparingPos, comparingArea.minPos, comparingArea.maxPos);
        }
    }
}
