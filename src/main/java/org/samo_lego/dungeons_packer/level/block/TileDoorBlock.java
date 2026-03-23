package org.samo_lego.dungeons_packer.level.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.level.item.ConverterItems;
import org.samo_lego.dungeons_packer.level.item.TileCornerBlockItem;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.tiles.DungeonsHandler;

public class TileDoorBlock extends AbstractLocalConvertableBlock {

    public TileDoorBlock(Properties properties) {
        super(properties);
    }

    public static InteractionResult onTryPlace(DungeonsHandler handler, Player player, BlockPos blockPos) {
        return handler.objectGroup.getTileCornerContaining(blockPos).map(tileCorner -> {
            // Can only be placed on the side, e.g. x or z must eq to corner x/z
            // or other corner x/z
            var pos1 = tileCorner.getBlockPos();
            var pos2 = tileCorner.getMatchingCornerPos().get();
            if (pos1.getX() == blockPos.getX() || pos2.getX() == blockPos.getX() || pos1.getZ() == blockPos.getZ() || pos2.getZ() == blockPos.getZ()) {
                return InteractionResult.PASS;
            }

            player.sendOverlayMessage(Component.translatable("message.dungeons_packer.door_place.on_side").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }).orElseGet(() -> {
            player.sendOverlayMessage(Component.translatable("message.dungeons_packer.door_place.in_tile", Component.translatable(ConverterItems.TILE_CORNER_BLOCK_ITEM.getDescriptionId())).withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        });
    }

    @Override
    protected Door getDoorOrRegion(BlockPos relativePos, Vec3i size) {
        return new Door(relativePos, size, "");
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }
}
