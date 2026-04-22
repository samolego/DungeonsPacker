package org.samo_lego.dungeons_packer.level.block.mobs;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.lovika.block_conversion.DungeonBlockIdProvider;
import org.samo_lego.dungeons_packer.lovika.block_conversion.IDungeonsConvertable;
import org.samo_lego.dungeons_packer.lovika.region.RegionLike;
import org.samo_lego.dungeons_packer.lovika.tiles.IDungeonsHandlerProvider;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.state.BlockBehaviour.simpleCodec;

public class MobsAreaBlock  extends BaseEntityBlock implements IDungeonsConvertable {
    private static final MapCodec<MobsAreaBlock> CODEC = simpleCodec(MobsAreaBlock::new);

    public MobsAreaBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new MobsAreaBlockEntity(worldPosition, blockState);
    }

    @Override
    public short dungeons_packer$convertToDungeons(DungeonBlockIdProvider blockIdProvider, ServerPlayer player, BlockPos currentPos, BlockPos relativePos, int width, int depth, ArrayList<Door> doors, ArrayList<RegionLike> regions, List<int[]> prefabs) {
        return BlockMap.DUNGEONS_AIR;
    }


    @Override
    protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        return InteractionResult.SUCCESS;
    }
}
