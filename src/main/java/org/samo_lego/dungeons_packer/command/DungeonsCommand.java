package org.samo_lego.dungeons_packer.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;

public class DungeonsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, CommandSelection _environment) {
        dispatcher.register(Commands.literal("dungeons")
            .then(ExportCommand.register(dispatcher, registryAccess))
            .then(DumpCommand.register(dispatcher, registryAccess))
        );

    }
}
