package org.samo_lego.dungeons_exporter.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.samo_lego.dungeons_exporter.DungeonsExporter;
import org.samo_lego.dungeons_exporter.block.corner.TileCornerBlock;

import java.util.function.Function;

// Todo: blockentities
public class ConverterBlocks {

	public static final Block TILE_DOOR_BLOCK = register(
			"tile_door",
			Block::new,
			BlockBehaviour.Properties.of().sound(SoundType.STONE),
			true
	);

	public static final Block END_MISSION_BLOCK = register(
			"end_mission",
			Block::new,
			BlockBehaviour.Properties.of().sound(SoundType.AMETHYST),
			true
	);


	public static final Block TILE_CORNER_BLOCK = register(  // TODO: make as an angel block, ability to place in air directly
			"tile_corner",
			TileCornerBlock::new,
			BlockBehaviour.Properties.of().sound(SoundType.AMETHYST),
			true
	);

	private static Block register(String name, Function<Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
		// Create a registry key for the block
		ResourceKey<Block> blockKey = keyOfBlock(name);
		// Create the block instance
		Block block = blockFactory.apply(settings.setId(blockKey));

		// Sometimes, you may not want to register an item for the block.
		// Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
		if (shouldRegisterItem) {
			// Items need to be registered with a different type of registry key, but the ID
			// can be the same.
			ResourceKey<Item> itemKey = keyOfItem(name);

			BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
			Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
		}

		return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
	}

	private static ResourceKey<Block> keyOfBlock(String name) {
		return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(DungeonsExporter.MOD_ID, name));
	}

	private static ResourceKey<Item> keyOfItem(String name) {
		return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DungeonsExporter.MOD_ID, name));
	}

	public static void initialize() { }
}