package org.samo_lego.dungeons_packer.level.block.mobs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.level.block.ConverterBlockEntites;

public class MobsAreaBlockEntity extends BaseContainerBlockEntity {

    private NonNullList<ItemStack> items;

    public MobsAreaBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ConverterBlockEntites.MOBS_AREA_BLOCK_ENTITY, worldPosition, blockState);
        this.items = NonNullList.create();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.dungeons_packer.mobs_area");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return ChestMenu.sixRows(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 9*6;
//        return this.items.size() + 1;
    }
}
