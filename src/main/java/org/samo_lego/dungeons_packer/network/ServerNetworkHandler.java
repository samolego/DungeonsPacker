package org.samo_lego.dungeons_packer.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;

import java.io.File;

public class ServerNetworkHandler {
    public static void onClientTextureDataReceived(TextureDataC2SPacket packet, Context context) {
        context.server().execute(() -> {
            int blockStateId = packet.id();
            var textureId = packet.textureId();
            byte[] textureBytes = packet.bytes();

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
