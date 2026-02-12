package org.samo_lego.dungeons_packer.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;
import net.minecraft.world.level.block.Block;
import org.samo_lego.dungeons_packer.lovika.resource_pack.TextureBytes;
import org.samo_lego.dungeons_packer.lovika.tiles.IDungeonsHandlerProvider;

import java.io.File;

public class ServerNetworkHandler {
    public static void onClientTextureDataReceived(TextureDataC2SPacket packet, Context context) {
        context.server().execute(() -> {
            var tl = ((IDungeonsHandlerProvider) context.player().level()).dungeons_packer$getDungeonsHandler();
            int blockStateId = packet.id();
            var textureId = packet.textureId();
            byte[] textureBytes = packet.bytes();

            tl.textureCache.put(Block.stateById(blockStateId), new TextureBytes(textureId, textureBytes));

            // Write file to disk
            var file = new File("packed_textures/" + textureId.getNamespace() + "/" + textureId.getPath() + ".png");
            file.getParentFile().mkdirs(); // Ensure parent directories exist

            try (var outputStream = new java.io.FileOutputStream(file)) {
                outputStream.write(textureBytes);
                System.out.println("Saved texture " + textureId + " for block state ID " + blockStateId + " to " + file.getAbsolutePath());
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        });
    }
}
