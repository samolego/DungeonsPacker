package org.samo_lego.dungeons_packer.level.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.samo_lego.dungeons_packer.level.block.TileDoorBlock;
import org.samo_lego.dungeons_packer.lovika.tiles.IDungeonsHandlerProvider;

import static org.samo_lego.dungeons_packer.level.block.ConverterBlocks.TILE_DOOR_BLOCK;

public class TileDoorBlockItem extends BlockItem {

    public TileDoorBlockItem(Properties properties) {
        super(TILE_DOOR_BLOCK, properties);
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        if (level instanceof ServerLevel sle) {
            // Raycast to check if we are looking at a block
            BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

            if (hitResult.getType() != HitResult.Type.MISS) {
                var targetPos = hitResult.getBlockPos();
                return TileDoorBlock.onTryPlace(((IDungeonsHandlerProvider) sle).dungeons_packer$getDungeonsHandler(), player, targetPos);
            }
        }

        return super.use(level, player, hand);
    }
}
