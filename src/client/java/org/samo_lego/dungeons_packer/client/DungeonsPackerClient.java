package org.samo_lego.dungeons_packer.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityWithBoundingBoxRenderer;
import org.samo_lego.dungeons_packer.block.ConverterBlockEntites;

public class DungeonsPackerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ConverterBlockEntites.TILE_CORNER_BLOCK_ENTITY, _context -> new BlockEntityWithBoundingBoxRenderer<>());
	}
}