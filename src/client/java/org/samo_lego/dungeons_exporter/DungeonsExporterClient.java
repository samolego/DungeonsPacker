package org.samo_lego.dungeons_exporter;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityWithBoundingBoxRenderer;
import org.samo_lego.dungeons_exporter.block.ConverterBlockEntites;

public class DungeonsExporterClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ConverterBlockEntites.TILE_CORNER_BLOCK_ENTITY, _context -> new BlockEntityWithBoundingBoxRenderer());
	}
}