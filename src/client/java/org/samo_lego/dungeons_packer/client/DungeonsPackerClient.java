package org.samo_lego.dungeons_packer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ChunkSectionLayerMap;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityWithBoundingBoxRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import org.samo_lego.dungeons_packer.block.ConverterBlockEntites;
import org.samo_lego.dungeons_packer.block.ConverterBlocks;

public class DungeonsPackerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ConverterBlockEntites.TILE_CORNER_BLOCK_ENTITY, _context -> new BlockEntityWithBoundingBoxRenderer<>());
		ChunkSectionLayerMap.putBlock(ConverterBlocks.END_MISSION_BLOCK, ChunkSectionLayer.TRANSLUCENT);
	}
}