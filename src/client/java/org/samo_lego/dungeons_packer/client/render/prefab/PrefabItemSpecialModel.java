package org.samo_lego.dungeons_packer.client.render.prefab;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.samo_lego.dungeons_packer.level.ModComponents;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabData;

import java.util.function.Consumer;

public class PrefabItemSpecialModel implements SpecialModelRenderer<PrefabRenderState> {

    @Override
    public void getExtents(Consumer<Vector3fc> output) {

    }

    @Override
    public PrefabRenderState extractArgument(ItemStack stack) {
        PrefabRenderState state = new PrefabRenderState();
        PrefabData data = stack.get(ModComponents.PREFAB_DATA);
        if (data != null) {
            state.setPrefabData(data);
        }
        return state;
    }

    @Override
    public void submit(PrefabRenderState state, PoseStack matrices, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, final int outlineColor) {
        if (state == null || state.getPrefabData() == null) return;

        ObjModel model = state.getBPObjModel();
        if (model == null) return;

        matrices.pushPose();

        //var scale = state.getPrefabData().scale();
        //matrices.scale((float) scale.x(), (float) scale.y(), (float) scale.z());

        var renderType = RenderTypes.entityCutout(state.getBaseTexture());
        collector.submitCustomGeometry(matrices, renderType, (pose, buffer) -> {
            model.renderToBuffer(pose, buffer, lightCoords, overlayCoords);
        });
        matrices.popPose();
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked<PrefabRenderState> {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<PrefabRenderState> bake(BakingContext context) {
            return new PrefabItemSpecialModel();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<PrefabRenderState>> type() { return CODEC; }
    }
}
