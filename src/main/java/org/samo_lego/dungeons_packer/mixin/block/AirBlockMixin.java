package org.samo_lego.dungeons_packer.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.lovika.block_conversion.DungeonBlockIdProvider;
import org.samo_lego.dungeons_packer.lovika.block_conversion.IDungeonsConvertable;
import org.samo_lego.dungeons_packer.lovika.region.RegionLike;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;

@Mixin(AirBlock.class)
public class AirBlockMixin implements IDungeonsConvertable {
    @Override
    public short dungeons_packer$convertToDungeons(DungeonBlockIdProvider blockIdProvider, ServerPlayer player, BlockPos absolutePos, BlockPos relativePos, ArrayList<Door> doors, ArrayList<RegionLike> regions) {
        return BlockMap.DUNGEONS_AIR;
    }
}
