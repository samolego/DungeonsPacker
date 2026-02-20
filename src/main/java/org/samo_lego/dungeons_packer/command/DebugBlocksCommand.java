package org.samo_lego.dungeons_packer.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.Property;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;

public class DebugBlocksCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        return Commands.literal("dbg")
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var level = player.level();
                    var pos = player.blockPosition();

                    DebugBlocksCommand.dbgBlocks(player, level, pos);
                    return 1;
                });
    }

    private static int dbgBlocks(ServerPlayer player, ServerLevel level, BlockPos pos) throws CommandSyntaxException {
        var blocks = BlockMap.dbg(player.level());
        MutableBlockPos mPos = new MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
        var direction = player.getDirection();

        blocks.forEach((block, rules) -> {
            var newPos = mPos.relative(direction);
            mPos.set(newPos);
            var state = block.defaultBlockState();
            level.setBlock(mPos.relative(Direction.DOWN), Blocks.STONE.defaultBlockState(), 3);
            level.setBlock(mPos, state, 0, 0);
        });
        return 1;
    }

    private static  <T extends Comparable<T>, V extends T> void execute(ServerPlayer player, ServerLevel level, BlockPos pos) {
        var blocks = BlockMap.dbg(player.level());
        MutableBlockPos mPos = new MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
        var direction = player.getDirection();
        blocks.forEach((block, rules) -> {
            for (var rule : rules) {
                var newPos = mPos.relative(direction);
                mPos.set(newPos);
                var state = block.defaultBlockState();

                for (var entry : rule.requirements().entrySet()) {
                    var property = entry.getKey();
                    var value = entry.getValue();
                    state = state.setValue((Property<T>) property, (V) value);
                }
                level.setBlock(mPos.relative(Direction.DOWN), Blocks.STONE.defaultBlockState(), 2);
                level.setBlock(mPos, state, 2);
            }
        });
    }
}
