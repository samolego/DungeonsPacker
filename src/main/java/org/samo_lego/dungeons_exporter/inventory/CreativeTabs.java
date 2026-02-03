package org.samo_lego.dungeons_exporter.inventory;

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
import org.samo_lego.dungeons_exporter.DungeonsExporter;
import org.samo_lego.dungeons_exporter.block.ConverterBlocks;

import java.util.List;

public class CreativeTabs {
    public static final ResourceKey<CreativeModeTab> DUNEGONS_CREATIVE_TAB_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(DungeonsExporter.MOD_ID, "creative_tab"));
    public static final CreativeModeTab DUNEGONS_CREATIVE_TAB = CreativeModeTab.builder(Row.TOP, 0)
            .icon(() -> new ItemStack(Items.SPAWNER))
            .title(Component.translatable("itemGroup.dungeons_exporter"))
            .displayItems((params, output) -> {
                output.accept(ConverterBlocks.TILE_DOOR_BLOCK);
                output.accept(ConverterBlocks.END_MISSION_BLOCK);

                // And custom ItemStacks
                ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
                stack.set(DataComponents.ITEM_NAME, Component.literal("Player spawn"));
                stack.set(DataComponents.LORE, new ItemLore(List.of(Component.literal("Place this where you want the player to spawn."))));
                output.accept(stack);
            })
            .build();

    public static void initialize() { }
}
