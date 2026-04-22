package org.samo_lego.dungeons_packer.level.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.dungeons_packer.level.ModComponents;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabBlockEntity;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabData;

import static org.samo_lego.dungeons_packer.level.block.ConverterBlocks.PREFAB_BLOCK;

public class PrefabBlockItem extends BlockItem {
    public PrefabBlockItem(Properties properties) {
        super(PREFAB_BLOCK, properties);
    }

    /**
     * Applies the prefab data from the item stack to the block entity when the block is placed.
     */
    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        boolean superResult = super.updateCustomBlockEntityTag(pos, level, player, stack, state);

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PrefabBlockEntity prefabBe) {
            PrefabData itemData = stack.getOrDefault(ModComponents.PREFAB_DATA, PrefabData.getDefault());
            prefabBe.setPrefabData(itemData);
        }

        return superResult;
    }
}
