package org.samo_lego.dungeons_packer.lovika.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.region.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record Tile(
        String id,
        BlockPos pos,
        Vec3i size,
        String blocks, // todo: change this
        String regionPlane,
        String heightPlane,
        String regionYPlane,
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

    public static Optional<Tile> fromTileCornerBlock(TileCornerBlockEntity cornerBlockEntity) {
        var tileBox = cornerBlockEntity.getRenderableBox();
        var pos = tileBox.localPos().offset(cornerBlockEntity.getBlockPos());
        var size = tileBox.size();

        // This is taken from Dokucraft world converter,
        // all credit goes to them
        for (int x = 0; x < size.getX(); ++x) {
            for (int z = 0; z < size.getZ(); ++z) {
                for (int y = 0; y < size.getY(); ++y) {
                    var currentPos = pos.offset(x, y, z);
                    var blockState = cornerBlockEntity.getLevel().getBlockState(currentPos);

                    if (blockState.isAir()) {
                        continue;
                    }
                    BlockState state = Blocks.BASALT.defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.X);
                    BlockState state2 = Blocks.BASALT.defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.X);


                }
            }
        }

        return Optional.empty();
    }
}
