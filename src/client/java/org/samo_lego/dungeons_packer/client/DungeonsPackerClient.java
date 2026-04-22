package org.samo_lego.dungeons_packer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityWithBoundingBoxRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.level.block.ConverterBlockEntites;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabBlockEntity;
import org.samo_lego.dungeons_packer.client.network.ClientPacketHandler;
import org.samo_lego.dungeons_packer.client.render.prefab.PrefabBlockEntityRenderer;
import org.samo_lego.dungeons_packer.client.render.prefab.PrefabItemSpecialModel;
import org.samo_lego.dungeons_packer.client.screen.PrefabEditScreen;
import org.samo_lego.dungeons_packer.network.RequestTexturesS2CPacket;

public class DungeonsPackerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ConverterBlockEntites.TILE_CORNER_BLOCK_ENTITY, _context -> new BlockEntityWithBoundingBoxRenderer<>());
		BlockEntityRenderers.register(ConverterBlockEntites.PREFAB_BLOCK_ENTITY, PrefabBlockEntityRenderer::new);

		SpecialModelRenderers.ID_MAPPER.put(
				Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "prefab_renderer"),
				PrefabItemSpecialModel.Unbaked.CODEC
		);

		ClientPlayNetworking.registerGlobalReceiver(RequestTexturesS2CPacket.ID, ClientPacketHandler::handleRequestTextures);


		UseBlockCallback.EVENT.register((player, world, _hand, hitResult) -> {
			if (world.isClientSide() && !player.isShiftKeyDown() && player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
				BlockEntity be = world.getBlockEntity(hitResult.getBlockPos());
				if (be instanceof PrefabBlockEntity prefabe) {
					Minecraft.getInstance().setScreen(new PrefabEditScreen(prefabe));
					return InteractionResult.SUCCESS;
				}
			}
			return InteractionResult.PASS;
		});
	}
}