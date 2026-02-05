package org.samo_lego.dungeons_packer.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockItem;

import java.util.function.Function;

import static org.samo_lego.dungeons_packer.block.ConverterBlocks.TILE_CORNER_BLOCK;

public class ConverterItems {

    public static final Item TILE_CORNER_BLOCK_ITEM = register(
            "tile_corner",
            settings -> new TileCornerBlockItem(TILE_CORNER_BLOCK, settings),
            new Item.Properties().useBlockDescriptionPrefix()
    );

    public static <GenericItem extends Item> GenericItem register(String name, Function<Properties, GenericItem> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, name));

        // Create the item instance.
        GenericItem item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() { }
}
