package org.samo_lego.dungeons_packer.lovika.block_conversion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.region.RegionLike;

import java.util.ArrayList;

public interface IDungeonsConvertable {
    short dungeons_packer$convertToDungeons(DungeonBlockIdProvider blockIdProvider, ServerPlayer player, BlockPos absolutePos, BlockPos relativePos, ArrayList<Door> doors, ArrayList<RegionLike> regions, ArrayList<int[]> prefabs);
}
