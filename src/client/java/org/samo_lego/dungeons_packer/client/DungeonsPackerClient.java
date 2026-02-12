package org.samo_lego.dungeons_packer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ChunkSectionLayerMap;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityWithBoundingBoxRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.block.ConverterBlockEntites;
import org.samo_lego.dungeons_packer.block.ConverterBlocks;
import org.samo_lego.dungeons_packer.client.network.ClientPacketHandler;
import org.samo_lego.dungeons_packer.network.RequestTexturesS2CPacket;

import java.util.ArrayList;

public class DungeonsPackerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ConverterBlockEntites.TILE_CORNER_BLOCK_ENTITY, _context -> new BlockEntityWithBoundingBoxRenderer<>());
		//todo ChunkSectionLayerMap.putBlock(ConverterBlocks.END_MISSION_BLOCK, ChunkSectionLayer.TRANSLUCENT);

		ClientPlayNetworking.registerGlobalReceiver(RequestTexturesS2CPacket.ID, ClientPacketHandler::handleRequestTextures);
	}
}