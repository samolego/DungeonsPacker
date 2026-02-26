package org.samo_lego.dungeons_packer.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.network.chat.Component;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.config.ModConfig;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;

import static net.minecraft.commands.Commands.literal;


public class DungeonsPackerCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, CommandSelection _environment) {
        dispatcher.register(literal(DungeonsPacker.MOD_ID)
            .then(literal("recreate_mappings")
                .executes(DungeonsPackerCommand::redoBlockMappings)
            )
            .then(literal("reload")
                .executes(DungeonsPackerCommand::reload)
            )
        );
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        ModConfig.getInstance().reload();
        context.getSource().sendSuccess(() -> Component.translatable("command.dungeons_packer.reload_config.success").withStyle(ChatFormatting.GREEN), false);
        return 1;
    }

    private static int redoBlockMappings(CommandContext<CommandSourceStack> context) {
        BlockMap.initialize();
        context.getSource().sendSuccess(() -> Component.translatable("command.dungeons_packer.recreate_block_mappings.success").withStyle(ChatFormatting.GREEN), false);
        return 1;
    }
}
