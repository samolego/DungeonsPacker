package org.samo_lego.dungeons_packer.client.render.prefab;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabBlockEntity;

public class PrefabBlockEntityRenderer implements BlockEntityRenderer<PrefabBlockEntity, PrefabRenderState> {
	public PrefabBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public PrefabRenderState createRenderState() {
		return new PrefabRenderState();
	}

	@Override
	public void extractRenderState(PrefabBlockEntity blockEntity, PrefabRenderState state, float tickProgress, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
		state.setPrefabData(blockEntity.getPrefabData());
	}

	@Override
	public void submit(PrefabRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
		 ObjModel objModel = state.getBPObjModel();
		 if (objModel != null) {
			 var baseTexture = state.getBaseTexture();
			 var renderType = RenderTypes.entityCutout(baseTexture);
			 objModel.render(matrices, queue, renderType, state, 15728880, OverlayTexture.NO_OVERLAY);
		 }
	}
}