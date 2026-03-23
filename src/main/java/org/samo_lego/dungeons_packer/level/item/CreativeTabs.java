package org.samo_lego.dungeons_packer.level.item;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.level.ModComponents;
import org.samo_lego.dungeons_packer.level.block.ConverterBlocks;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabData;

import java.util.List;

import static org.samo_lego.dungeons_packer.level.block.prefab.PrefabRegistry.PREFAB_NAME_TO_INDEX;

public class CreativeTabs {
    public static final ResourceKey<CreativeModeTab> DUNGEONS_CREATIVE_TAB_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "creative_tab"));
    public static final CreativeModeTab DUNGEONS_CREATIVE_TAB = CreativeModeTab.builder(Row.TOP, 0)
            .icon(() -> new ItemStack(ConverterItems.TILE_CORNER_BLOCK_ITEM))
            .title(Component.translatable("itemGroup.dungeons_packer"))
            .displayItems((params, output) -> {
                output.accept(ConverterItems.TILE_DOOR_BLOCK_ITEM);
                output.accept(ConverterBlocks.END_MISSION_BLOCK);
                output.accept(ConverterItems.TILE_CORNER_BLOCK_ITEM);

                // And custom ItemStacks
                ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
                stack.set(DataComponents.ITEM_NAME, Component.literal("Player spawn"));
                stack.set(DataComponents.LORE, new ItemLore(List.of(
                        Component.literal("Place this where you"),
                        Component.literal("want the player to spawn.")
                )));
                output.accept(stack);
            })
            .build();

    public static final ResourceKey<CreativeModeTab> DUNGEONS_CREATIVE_TAB_PREFAB_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "creative_prefab_tab"));
    public static final CreativeModeTab DUNGEONS_CREATIVE_PREFAB_TAB = CreativeModeTab.builder(Row.BOTTOM, 0)
                .icon(() -> new ItemStack(Items.SPAWNER))
                .title(Component.translatable("itemGroup.dungeons_packer_generated"))
                .displayItems((params, output) -> {
                    output.acceptAll(PREFAB_NAME_TO_INDEX.keySet().stream().map(name -> {
                        var stack = new ItemStack(ConverterItems.PREFAB_BLOCK_ITEM);

                        // Make the name nicer (from pascal case to normal)
                        StringBuilder displayName = new StringBuilder();
                        for (int i = 3; i < name.length(); i++) {
                            char c = name.charAt(i);
                            if (c == '_') {
                                displayName.append(' ');
                                continue;
                            }

                            if (Character.isUpperCase(c) && Character.isLowerCase(name.charAt(i - 1))) {
                                displayName.append(' ');
                            }
                            displayName.append(c);
                        }

                        stack.set(DataComponents.CUSTOM_NAME, Component.literal(displayName.toString()));
                        stack.set(ModComponents.PREFAB_DATA, PrefabData.getDefault().withBPClass(name));

                        return stack;
                    }).toList());
                })
                .build();

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, DUNGEONS_CREATIVE_TAB_KEY, DUNGEONS_CREATIVE_TAB);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, DUNGEONS_CREATIVE_TAB_PREFAB_KEY, DUNGEONS_CREATIVE_PREFAB_TAB);
    }
}
