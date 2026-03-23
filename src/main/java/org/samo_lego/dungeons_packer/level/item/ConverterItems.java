package org.samo_lego.dungeons_packer.level.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.level.ModComponents;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabData;

import java.util.function.Function;

public class ConverterItems {

    public static final Item TILE_CORNER_BLOCK_ITEM = register(
            "tile_corner",
            TileCornerBlockItem::new,
            new Item.Properties().useBlockDescriptionPrefix()
    );

    public static final Item TILE_DOOR_BLOCK_ITEM = register(
            "tile_door",
            TileDoorBlockItem::new,
            new Item.Properties().useBlockDescriptionPrefix()
    );


    public static final Item PREFAB_BLOCK_ITEM = register(
            "prefab",
            PrefabBlockItem::new,
            new Item.Properties()
                    .useBlockDescriptionPrefix()
                    .component(ModComponents.PREFAB_DATA, PrefabData.getDefault())
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
