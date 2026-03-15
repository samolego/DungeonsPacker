package org.samo_lego.dungeons_packer.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.lovika.block_conversion.DungeonBlockIdProvider;
import org.samo_lego.dungeons_packer.lovika.block_conversion.IDungeonsConvertable;
import org.samo_lego.dungeons_packer.lovika.region.Region;
import org.samo_lego.dungeons_packer.lovika.region.RegionLike;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLocalConvertableBlock extends Block implements IDungeonsConvertable {
    public AbstractLocalConvertableBlock(Properties properties) {
        super(properties);
    }


    @Override
    public short dungeons_packer$convertToDungeons(DungeonBlockIdProvider blockIdProvider, ServerPlayer player, BlockPos absolutePos, BlockPos relativePos, ArrayList<Door> doors, ArrayList<RegionLike> regions, List<int[]> prefabs) {
        // Check neighbour blocks
        var level = player.level();
        for (var direction : Direction.values()) {
            BlockPos neighborPos = absolutePos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborPos.compareTo(absolutePos) < 0 && neighborState.getBlock() == this.asBlock()) {
                // Other guy will handle it
                return BlockMap.DUNGEONS_AIR;
            }
        }
        // We're at the bottom most block, so we can define the section
        int y = 0;
        int z = 0;
        int x = 0;

        while (level.getBlockState(absolutePos.offset(x, y, z)).is(this)) {
            y++;
        }
        while (level.getBlockState(absolutePos.offset(x, y - 1, z)).is(this)) {
            z++;
        }
        while (level.getBlockState(absolutePos.offset(x, y - 1, z - 1)).is(this)) {
            x++;
        }

        var size = new Vec3i(x, y, z);
        regions.add(new Region(relativePos, size, this.regionName(), this.regionTags(), Region.Type.TRIGGER));

        return BlockMap.DUNGEONS_AIR;
    }

    protected abstract String regionTags();

    protected abstract String regionName();
}
