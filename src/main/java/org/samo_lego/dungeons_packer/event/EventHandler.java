package org.samo_lego.dungeons_packer.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.samo_lego.dungeons_packer.level.block.TileDoorBlock;
import org.samo_lego.dungeons_packer.level.item.ConverterItems;
import org.samo_lego.dungeons_packer.level.item.TileDoorBlockItem;
import org.samo_lego.dungeons_packer.lovika.tiles.IDungeonsHandlerProvider;

public class EventHandler {
    public static InteractionResult onPlayerPlaceBlock(Player player, Level level, InteractionHand hand, BlockPos blockPos) {
        var handItem = player.getItemInHand(hand).getItem();

        if (level instanceof ServerLevel slevel) {
            var handler = ((IDungeonsHandlerProvider) slevel).dungeons_packer$getDungeonsHandler();
            if (handItem == ConverterItems.TILE_CORNER_BLOCK_ITEM) {
                return handler.objectGroup.getTileCornerContaining(blockPos).<InteractionResult>map(_ -> {
                    player.sendOverlayMessage(Component.translatable("message.dungeons_packer.cannot_place_in_tile").withStyle(ChatFormatting.RED));
                    return InteractionResult.FAIL;
                }).orElse(InteractionResult.PASS);
            } else if (handItem == ConverterItems.TILE_DOOR_BLOCK_ITEM) {
                return TileDoorBlock.onTryPlace(handler, player, blockPos);
            }
        }
        return InteractionResult.PASS;

    }
}
