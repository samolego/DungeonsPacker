package org.samo_lego.dungeons_packer.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.region.Region;

public class EndMissionBlock extends AbstractLocalConvertableBlock {
    public static final String END = "end_trigger_objective";

    public EndMissionBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected Door getDoorOrRegion(BlockPos relativePos, Vec3i size) {
        return new Region(relativePos, size, END, END, Region.Type.TRIGGER);
    }
}
