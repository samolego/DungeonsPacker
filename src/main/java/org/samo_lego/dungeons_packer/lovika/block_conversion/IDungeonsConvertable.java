package org.samo_lego.dungeons_packer.lovika.block_conversion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.Level;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.region.Region;

import java.util.ArrayList;

public interface IDungeonsConvertable {
    short dungeons_packer$convertToDungeons(Level level, BlockPos absolutePos, BlockPos relativePos, ArrayList<Door> doors, ArrayList<Region> regions);
}
