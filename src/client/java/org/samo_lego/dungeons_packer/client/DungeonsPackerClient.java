package org.samo_lego.dungeons_packer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityWithBoundingBoxRenderer;
import org.samo_lego.dungeons_packer.block.ConverterBlockEntites;
import org.samo_lego.dungeons_packer.client.network.ClientPacketHandler;
import org.samo_lego.dungeons_packer.network.RequestTexturesS2CPacket;

public class DungeonsPackerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ConverterBlockEntites.TILE_CORNER_BLOCK_ENTITY, _context -> new BlockEntityWithBoundingBoxRenderer<>());

		ClientPlayNetworking.registerGlobalReceiver(RequestTexturesS2CPacket.ID, ClientPacketHandler::handleRequestTextures);
	}
}