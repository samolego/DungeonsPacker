package org.samo_lego.dungeons_packer.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.block.ConverterBlocks;
import org.samo_lego.dungeons_packer.item.ConverterItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractBlockOutline", at = @At("HEAD"), cancellable = true)
    private void extractBlockOutline(final Camera camera, final LevelRenderState levelRenderState, CallbackInfo ci) {
        LocalPlayer player = this.minecraft.player;
        if (player.getMainHandItem().getItem() == ConverterItems.TILE_CORNER_BLOCK_ITEM) {
            // Show the block outline
            Vec3 lookAngle = player.getLookAngle();
            BlockPos targetPos = BlockPos.containing(player.getEyePosition().add(lookAngle.scale(player.blockInteractionRange())));
            if (player.level().getBlockState(targetPos).canBeReplaced()) {
                CollisionContext context = CollisionContext.of(camera.entity());
                VoxelShape shape = ConverterBlocks.TILE_CORNER_BLOCK.defaultBlockState().getShape(player.level(), targetPos, context);
                levelRenderState.blockOutlineRenderState = new BlockOutlineRenderState(targetPos, true, true, shape);
                ci.cancel();
            }
        }
    }
}
