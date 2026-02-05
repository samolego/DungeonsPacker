package org.samo_lego.dungeons_packer.item;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.block.ConverterBlocks;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CreativeTabs {
    public static final ResourceKey<CreativeModeTab> DUNGEONS_CREATIVE_TAB_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "creative_tab"));
    public static final CreativeModeTab DUNGEONS_CREATIVE_TAB = CreativeModeTab.builder(Row.TOP, 0)
            .icon(() -> new ItemStack(Items.SPAWNER))
            .title(Component.translatable("itemGroup.dungeons_packer"))
            .displayItems((params, output) -> {
                output.accept(ConverterBlocks.TILE_DOOR_BLOCK);
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

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, DUNGEONS_CREATIVE_TAB_KEY, DUNGEONS_CREATIVE_TAB);
    }
}
