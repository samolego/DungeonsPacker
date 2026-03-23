package org.samo_lego.dungeons_packer.level.block.corner;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
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


public class TileCornerBlock extends BaseEntityBlock implements IDungeonsConvertable {
    private static final MapCodec<TileCornerBlock> CODEC = simpleCodec(TileCornerBlock::new);

    public TileCornerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new TileCornerBlockEntity(worldPosition, blockState);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        var tileListener = ((IDungeonsHandlerProvider) level).dungeons_packer$getDungeonsHandler();
        var blockEntity = (TileCornerBlockEntity) level.getBlockEntity(pos);

        tileListener.onCornerPlaced(blockEntity);
    }

    @Override
    public short dungeons_packer$convertToDungeons(DungeonBlockIdProvider blockIdProvider, ServerPlayer player, BlockPos currentPos, BlockPos relativePos, ArrayList<Door> doors, ArrayList<RegionLike> regions, List<int[]> prefabs) {
        return BlockMap.DUNGEONS_AIR;
    }
}
