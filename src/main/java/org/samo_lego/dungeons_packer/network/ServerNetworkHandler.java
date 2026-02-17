package org.samo_lego.dungeons_packer.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;
import net.minecraft.world.level.block.Block;
import org.samo_lego.dungeons_packer.lovika.resource_pack.TextureBytes;
import org.samo_lego.dungeons_packer.lovika.tiles.IDungeonsHandlerProvider;

public class ServerNetworkHandler {
    public static void onClientTextureDataReceived(TextureDataC2SPacket packet, Context context) {
        context.server().execute(() -> {
            var tl = ((IDungeonsHandlerProvider) context.player().level()).dungeons_packer$getDungeonsHandler();
            int blockStateId = packet.stateId();
            var directionMap = packet.sideMappings();
            var textureBytes = packet.uniqueTextures();

            tl.onTextureReceive(Block.stateById(blockStateId), new TextureBytes(directionMap, textureBytes));
        });
    }

    public static void onClientTextureDataFinished(FinishTextureDataC2SPacket ignoredPacket, Context context) {
        context.server().execute(() -> {
            var tl = ((IDungeonsHandlerProvider) context.player().level()).dungeons_packer$getDungeonsHandler();
             tl.onTextureReceiveEnd();
        });
    }
}
