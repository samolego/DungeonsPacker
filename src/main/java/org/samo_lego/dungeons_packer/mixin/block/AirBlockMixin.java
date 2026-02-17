package org.samo_lego.dungeons_packer.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.lovika.block_conversion.IDungeonsConvertable;
import org.samo_lego.dungeons_packer.lovika.region.Region;
import org.samo_lego.dungeons_packer.lovika.region.RegionLike;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;

@Mixin(AirBlock.class)
public class AirBlockMixin implements IDungeonsConvertable {
    @Override
    public short dungeons_packer$convertToDungeons(Level level, BlockPos absolutePos, BlockPos relativePos, ArrayList<Door> doors, ArrayList<RegionLike> regions) {
        return BlockMap.DUNGEONS_AIR;
    }
}
