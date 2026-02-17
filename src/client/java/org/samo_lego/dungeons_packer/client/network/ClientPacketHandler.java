package org.samo_lego.dungeons_packer.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import org.samo_lego.dungeons_packer.client.BlockRenderer;
import org.samo_lego.dungeons_packer.network.FinishTextureDataC2SPacket;
import org.samo_lego.dungeons_packer.network.RequestTexturesS2CPacket;
import org.samo_lego.dungeons_packer.network.TextureDataC2SPacket;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;


public class ClientPacketHandler {
    public static void handleRequestTextures(RequestTexturesS2CPacket packet, Context context) {
        var client = context.client();
        client.execute(() -> {
            // Minecraft also uses 42 internally
            // so ... I just copied them :P
            var rs = RandomSource.create(42L);
            for (long lid : packet.blockStates()) {
                int stateId = (int) lid;
                var state = Block.stateById(stateId);
                var model = client.getBlockRenderer().getBlockModel(state);

                Map<Direction, Identifier> sideMappings = new EnumMap<>(Direction.class);
                Map<Identifier, byte[]> uniqueTextures = new HashMap<>();

                for (Direction dir : Direction.values()) {

                    for (BlockModelPart part : model.collectParts(rs)) {

                        for (BakedQuad quad : part.getQuads(dir)) {
                            var spriteInfo = quad.spriteInfo();

                            var sprite = spriteInfo.sprite();
                            var textureId = sprite.contents().name();

                            sideMappings.put(dir, textureId);

                            // If we haven't read this specific texture yet, read it now
                            if (!uniqueTextures.containsKey(textureId)) {
                                byte[] bytes = BlockRenderer.getInstance().getRawTextureBytes(textureId, client);
                                byte[] texture = BlockRenderer.getInstance().captureSideRaw(state, dir, client);
                                if (texture.length > 0) {
                                    uniqueTextures.put(textureId, texture);
                                }
                            }
                        }
                    }
                }

                // Also check 'null' (non-culled) quads so we don't miss
                // the inside of blocks or non-cube shapes
                for (BlockModelPart part : model.collectParts(RandomSource.create(42L))) {
                    for (BakedQuad quad : part.getQuads(null)) {
                        var textureId = quad.spriteInfo().sprite().contents().name();

                        // If a side doesn't have a specific texture yet,
                        // the null quad is a good fallback.
                        for (Direction d : Direction.values()) {
                            sideMappings.putIfAbsent(d, textureId);
                        }

                        if (!uniqueTextures.containsKey(textureId)) {
                            byte[] bytes = BlockRenderer.getInstance().getRawTextureBytes(textureId, client);
                            byte[] texture = BlockRenderer.getInstance().captureSideRaw(state, null, client);
                            if (texture.length > 0) {
                                uniqueTextures.put(textureId, texture);
                            }
                        }
                    }
                }

                // Send the unified packet if we found any info
                if (!sideMappings.isEmpty()) {
                    ClientPlayNetworking.send(new TextureDataC2SPacket(stateId, sideMappings, uniqueTextures));
                }
            }

            ClientPlayNetworking.send(new FinishTextureDataC2SPacket());
        });
    }
}
