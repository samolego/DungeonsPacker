package org.samo_lego.dungeons_packer.client.render.prefab;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.javagl.obj.FloatTuple;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjFace;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

import java.io.IOException;
import java.io.InputStream;

public class ObjModel {
    private final Obj obj;

    public ObjModel(InputStream is) throws IOException {
        this.obj = ObjUtils.convertToRenderable(ObjReader.read(is));
    }

    public void render(PoseStack poseStack, SubmitNodeCollector queue, RenderType renderType, PrefabRenderState state, int light, int overlay) {
        poseStack.pushPose();

        var rel = state.getPrefabData().relativePos();
        // 1. Translation (rel)
        poseStack.translate(rel.x(), rel.y(), rel.z());

        var scale = state.getPrefabData().scale();
        // 2. Scale (uniform)
        poseStack.scale((float) scale.x(), (float) scale.y(), (float) scale.z());

        // Rotation x, y, z
        var rotation = state.getPrefabData().rotation();

        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotation.x()));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation.y()));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotation.z()));

        queue.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            renderToBuffer(pose, buffer, light, overlay);
        });

        poseStack.popPose();
    }


    public void renderToBuffer(PoseStack.Pose pose, VertexConsumer buffer, int light, int overlay) {
        for (int i = 0; i < obj.getNumFaces(); i++) {
            ObjFace tri = obj.getFace(i);
            // Triangle-to-Quad trick (repeating last vertex)
            int[] faceIdx = { 0, 1, 2, 2 };

            for (int v = 0; v < 4; v++) {
                int idx = faceIdx[v];
                FloatTuple vert = obj.getVertex(tri.getVertexIndex(idx));
                FloatTuple tex  = obj.getTexCoord(tri.getTexCoordIndex(idx));
                FloatTuple norm = obj.getNormal(tri.getNormalIndex(idx));

                // Your UE to Minecraft axis mapping (X, Z, Y)
                float x = vert.getX() / 100;
                float y = vert.getZ() / 100;
                float z = vert.getY() / 100;

                buffer.addVertex(pose, x, y, z)
                        .setColor(255, 255, 255, 255)
                        .setUv(tex.getX(), 1.0f - tex.getY())
                        .setOverlay(overlay)
                        .setLight(light)
                        .setNormal(pose, norm.getX(), -norm.getZ(), norm.getY());
            }
        }
    }
}
