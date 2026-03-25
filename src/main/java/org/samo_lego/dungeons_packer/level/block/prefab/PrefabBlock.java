package org.samo_lego.dungeons_packer.level.block.prefab;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;
import org.samo_lego.dungeons_packer.level.ModComponents;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.lovika.block_conversion.DungeonBlockIdProvider;
import org.samo_lego.dungeons_packer.lovika.block_conversion.IDungeonsConvertable;
import org.samo_lego.dungeons_packer.lovika.region.RegionLike;

import java.util.ArrayList;
import java.util.List;

public class PrefabBlock extends BaseEntityBlock implements IDungeonsConvertable {
    private static final MapCodec<PrefabBlock> CODEC = simpleCodec(PrefabBlock::new);

    public PrefabBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new PrefabBlockEntity(worldPosition, blockState);
    }

    @Override
    protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
        ItemStack stack = super.getCloneItemStack(level, pos, state, includeData);

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PrefabBlockEntity prefabBe && includeData) {
            stack.set(ModComponents.PREFAB_DATA, prefabBe.getPrefabData());
        }

        return stack;
    }

    @Override
    public short dungeons_packer$convertToDungeons(DungeonBlockIdProvider blockIdProvider, ServerPlayer player, BlockPos absolutePos, BlockPos relativePos, int width, int depth, ArrayList<Door> doors, ArrayList<RegionLike> regions, List<int[]> prefabs) {
        var be = player.level().getBlockEntity(absolutePos);
        if (be instanceof PrefabBlockEntity prefabBe) {
            var prefabData = prefabBe.getPrefabData();
            var pixels = prefabData.encodeToPixels(relativePos);
            if (pixels.isPresent()) {
                prefabs.add(pixels.get());
            } else {
                player.sendSystemMessage(Component.translatable("message.dungeons_packer.prefab_conversion_failed", Component.literal(absolutePos.toShortString()), Component.literal(prefabData.BP_Class())));
            }
        }

        return BlockMap.DUNGEONS_AIR;
    }

    @Override
    protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        return InteractionResult.SUCCESS;
    }
}
