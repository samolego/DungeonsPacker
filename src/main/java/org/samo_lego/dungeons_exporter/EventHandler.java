package org.samo_lego.dungeons_exporter;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.dungeons_exporter.lovika.tiles.TileListener;

public class EventHandler implements ServerPlayConnectionEvents.Init {


    @Override
    public void onPlayInit(ServerGamePacketListenerImpl _serverGamePacketListener, MinecraftServer minecraftServer) {
        var worldName = minecraftServer.getWorldData().getLevelName();
        System.out.println("Player connected to world: " + worldName);
        new TileListener(worldName.replace(" ", "_"));
    }
}
