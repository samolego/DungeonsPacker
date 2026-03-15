package org.samo_lego.dungeons_packer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.samo_lego.dungeons_packer.event.EventHandler;
import org.samo_lego.dungeons_packer.level.ModComponents;
import org.samo_lego.dungeons_packer.level.block.ConverterBlockEntites;
import org.samo_lego.dungeons_packer.level.block.ConverterBlocks;
import org.samo_lego.dungeons_packer.command.DungeonsCommand;
import org.samo_lego.dungeons_packer.command.DungeonsPackerCommand;
import org.samo_lego.dungeons_packer.command.PakCommand;
import org.samo_lego.dungeons_packer.level.item.CreativeTabs;
import org.samo_lego.dungeons_packer.level.item.ConverterItems;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.network.FinishTextureDataC2SPacket;
import org.samo_lego.dungeons_packer.network.RequestTexturesS2CPacket;
import org.samo_lego.dungeons_packer.network.ServerNetworkHandler;
import org.samo_lego.dungeons_packer.network.TextureDataC2SPacket;
import org.samo_lego.dungeons_packer.network.UpdatePrefabC2SPacket;

public class DungeonsPacker implements ModInitializer {
	public static final String MOD_ID = "dungeons_packer";
	public static final String MOD_ID_GENERATED = "dungeons_packer_generated";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Gson GSON = new GsonBuilder().create();

	@Override
	public void onInitialize() {
		ConverterBlocks.initialize();
		ConverterItems.initialize();
		ConverterBlockEntites.initialize();
		CreativeTabs.initialize();
		CommandRegistrationCallback.EVENT.register(DungeonsCommand::register);
		CommandRegistrationCallback.EVENT.register(DungeonsPackerCommand::register);
		CommandRegistrationCallback.EVENT.register(PakCommand::register);

		BlockMap.initialize();
		ModComponents.initialize();

		PayloadTypeRegistry.clientboundPlay().register(RequestTexturesS2CPacket.ID, RequestTexturesS2CPacket.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(TextureDataC2SPacket.ID, TextureDataC2SPacket.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(FinishTextureDataC2SPacket.ID, FinishTextureDataC2SPacket.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(UpdatePrefabC2SPacket.ID, UpdatePrefabC2SPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(TextureDataC2SPacket.ID, ServerNetworkHandler::onClientTextureDataReceived);
		ServerPlayNetworking.registerGlobalReceiver(FinishTextureDataC2SPacket.ID, ServerNetworkHandler::onClientTextureDataFinished);
		ServerPlayNetworking.registerGlobalReceiver(UpdatePrefabC2SPacket.ID, ServerNetworkHandler::onUpdatePrefabReceived);

		UseBlockCallback.EVENT.register((player, level, hand, blockHitResult) -> {
			BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
			return EventHandler.onPlayerPlaceBlock(player, level, hand, blockPos);
		});
		UseItemCallback.EVENT.register((player, level, hand) -> {
			HitResult hitResult = player.pick(5, 0, false);

			BlockPos blockPos;
			if (hitResult instanceof BlockHitResult blockHitResult) {
                blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
            } else {
				var loc = hitResult.getLocation();
				blockPos = BlockPos.containing(loc.x, loc.y, loc.z);
			}

			return EventHandler.onPlayerPlaceBlock(player, level, hand, blockPos);

		});
	}
}