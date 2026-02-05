package org.samo_lego.dungeons_packer.block.corner;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundSource;

public class TileCornerBlockItem extends BlockItem {
    public TileCornerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Raycast to check if we are looking at a block
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (hitResult.getType() == HitResult.Type.MISS) {
            Vec3 lookAngle = player.getLookAngle();
            BlockPos targetPos = BlockPos.containing(player.getEyePosition().add(lookAngle.scale(player.blockInteractionRange())));

            if (level.getBlockState(targetPos).canBeReplaced()) {
                if (!level.isClientSide()) {
                    BlockState state = this.getBlock().defaultBlockState();

                    if (level.setBlock(targetPos, state, 11)) {
                        SoundType soundType = state.getSoundType();
                        level.playSound(null, targetPos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

                        if (!player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }
                    }
                }
                return Success.SUCCESS_SERVER;
            }
        }

        return super.use(level, player, hand);
    }
}
