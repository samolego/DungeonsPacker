package org.samo_lego.dungeons_packer.level.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.level.item.ConverterItems;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.lovika.block_conversion.DungeonBlockIdProvider;
import org.samo_lego.dungeons_packer.lovika.region.RegionLike;
import org.samo_lego.dungeons_packer.lovika.tiles.DungeonsHandler;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public short dungeons_packer$convertToDungeons(DungeonBlockIdProvider blockIdProvider, ServerPlayer player, BlockPos absolutePos, BlockPos relativePos, int width, int depth, ArrayList<Door> doors, ArrayList<RegionLike> regions, List<int[]> prefabs) {
        // Check if we are at side
        int x = relativePos.getX();
        int z = relativePos.getZ();
        if (x != 0 && x != width || z != 0 && z != depth) {
            var msg = Component.translatable("message.dungeons_packer.block_conversion.detected_invalid", Component.translatable(ConverterBlocks.TILE_DOOR_BLOCK.getDescriptionId()), Component.literal(absolutePos.toShortString())).withStyle(ChatFormatting.RED);
            var command = String.format("/tp @s %d %d %d", absolutePos.getX(), absolutePos.getY(), absolutePos.getZ());
            var style = msg.getStyle();
            msg.setStyle(style
                    .withClickEvent(new ClickEvent.SuggestCommand(command))
                    .withHoverEvent(new ShowText(Component.translatable("chat.coordinates.tooltip").withStyle(ChatFormatting.YELLOW)))
            );
            player.sendSystemMessage(msg, false);

            return BlockMap.DUNGEONS_AIR;
        }

        return super.dungeons_packer$convertToDungeons(blockIdProvider, player, absolutePos, relativePos, width, depth, doors, regions, prefabs);
    }
}
