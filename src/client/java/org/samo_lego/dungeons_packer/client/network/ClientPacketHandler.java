package org.samo_lego.dungeons_packer.client.network;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider.Sprite;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuad.SpriteInfo;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import org.samo_lego.dungeons_packer.network.RequestTexturesS2CPacket;
import org.samo_lego.dungeons_packer.network.TextureDataC2SPacket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClientPacketHandler {
    public static void handleRequestTextures(RequestTexturesS2CPacket packet, Context context) {
        var client = context.client();
        client.execute(() -> {
            // Run on the client thread
            for (long lid : packet.blockStates()) {
                int id = (int) lid;
                var state = Block.stateById(id);
                var model = client.getBlockRenderer().getBlockModel(state);
                Set<SpriteInfo> sprites = new HashSet<>();

                for (Direction dir : Direction.values()) {
                    // Who'd think Minecraft uses 42 too :P (ModelBlockRenderer)
                    for (BlockModelPart part : model.collectParts(RandomSource.create(42L))) {
                        part.getQuads(dir).forEach(quad -> sprites.add(quad.spriteInfo()));
                    }
                }

                sprites.forEach(spriteInfo -> {
                    // Get byte[] texture
                    try (var sprite = spriteInfo.sprite()) {
                        var textureId = sprite.contents().name();
                        byte[] bytes = getRawTextureBytes(textureId, client);
                        ClientPlayNetworking.send(new TextureDataC2SPacket(id, textureId, bytes));
                    }
                });
            }
        });
    }

    public static byte[] getRawTextureBytes(Identifier spriteId, Minecraft client) {
        Identifier resourcePath = spriteId.withPrefix("textures/").withSuffix(".png");

        try (var resource = client.getResourceManager().open(resourcePath)) {
            return resource.readAllBytes();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
