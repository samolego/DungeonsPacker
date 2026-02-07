package org.samo_lego.dungeons_packer.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;

import static net.minecraft.commands.Commands.literal;

public class PakCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx, CommandSelection selection) {
        dispatcher.register(literal("pak")
            .then(literal("pack"))
            .then(literal("unpack"))
        );
    }

    // todo
}
