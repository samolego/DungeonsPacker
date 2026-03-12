package org.samo_lego.dungeons_packer.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.SkullBlock.Types;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.lovika.block_conversion.DungeonBlockIdProvider;
import org.samo_lego.dungeons_packer.lovika.block_conversion.IDungeonsConvertable;
import org.samo_lego.dungeons_packer.lovika.region.Region;
import org.samo_lego.dungeons_packer.lovika.region.Region.Type;
import org.samo_lego.dungeons_packer.lovika.region.RegionLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

@Mixin(AbstractSkullBlock.class)
public class AbstractSkullBlockMixin implements IDungeonsConvertable {
    @Unique
    private static final String PLAYER_START = "playerstart";

    @Shadow
    private SkullBlock.Type type;

    @Override
    public short dungeons_packer$convertToDungeons(DungeonBlockIdProvider blockIdProvider, ServerPlayer player, BlockPos currentPos, BlockPos relativePos, ArrayList<Door> doors, ArrayList<RegionLike> regions, ArrayList<int[]> prefabs) {
        // We mixined into abstract skull to catch both wall skull and ground
        // placed skull block.
        if (this.type == Types.PLAYER) {
            // Register as a start point
            var start = new Region(relativePos, new Vec3i(1, 1, 1), PLAYER_START, PLAYER_START, Type.TRIGGER);
            regions.add(start);

            return BlockMap.DUNGEONS_AIR;
        }
        // Todo - support skeleton skulls
        return blockIdProvider.requestId(player.level().getBlockState(currentPos), player);
    }
}
