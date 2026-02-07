package org.samo_lego.dungeons_packer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.samo_lego.dungeons_packer.block.ConverterBlockEntites;
import org.samo_lego.dungeons_packer.block.ConverterBlocks;
import org.samo_lego.dungeons_packer.command.DungeonsCommand;
import org.samo_lego.dungeons_packer.command.PakCommand;
import org.samo_lego.dungeons_packer.item.CreativeTabs;
import org.samo_lego.dungeons_packer.item.ConverterItems;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;

public class DungeonsPacker implements ModInitializer {
	public static final String MOD_ID = "dungeons_packer";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Gson GSON = new GsonBuilder().create();

	@Override
	public void onInitialize() {
		ConverterBlocks.initialize();
		ConverterItems.initialize();
		ConverterBlockEntites.initialize();
		CreativeTabs.initialize();
		CommandRegistrationCallback.EVENT.register(DungeonsCommand::register);
		CommandRegistrationCallback.EVENT.register(PakCommand::register);

		BlockMap.initialize();
	}
}