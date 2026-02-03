package org.samo_lego.dungeons_exporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.samo_lego.dungeons_exporter.block.ConverterBlockEntites;
import org.samo_lego.dungeons_exporter.block.ConverterBlocks;
import org.samo_lego.dungeons_exporter.command.DungeonsCommand;
import org.samo_lego.dungeons_exporter.inventory.CreativeTabs;

public class DungeonsExporter implements ModInitializer {
	public static final String MOD_ID = "dungeons-exporter";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Gson GSON = new GsonBuilder().create();

	@Override
	public void onInitialize() {
		ConverterBlocks.initialize();
		ConverterBlockEntites.initialize();
		CreativeTabs.initialize();

		CommandRegistrationCallback.EVENT.register(DungeonsCommand::register);
	}
}