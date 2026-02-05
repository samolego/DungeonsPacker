package org.samo_lego.dungeons_packer.block.corner;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.lovika.tiles.ITileListener;


public class TileCornerBlock extends BaseEntityBlock {
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
        var tileListener = ((ITileListener) level).dungeons_exporter$getTileListener();
        var blockEntity = (TileCornerBlockEntity) level.getBlockEntity(pos);

        tileListener.onCornerPlaced(blockEntity);
    }
}
