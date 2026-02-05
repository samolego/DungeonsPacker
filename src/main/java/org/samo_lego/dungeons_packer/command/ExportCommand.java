package org.samo_lego.dungeons_packer.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.lovika.tiles.ITileListener;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ExportCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        return Commands.literal("export")
                .then(Commands.argument("outputFile", StringArgumentType.string())
                        .executes(context -> {
                           var path = StringArgumentType.getString(context, "outputFile");
                           return ExportCommand.execute(context, new File(path)) ? 1 : 0;
                        }))
            .executes(context -> {
                var success = ExportCommand.execute(context, new File("exported.pak"));
                // Command logic goes here
                context.getSource().sendSuccess(
                    () -> Component.literal("Export command executed!"),
                    false
                );
                return success ? 1 : 0;
            });
    }

    private static boolean execute(CommandContext<CommandSourceStack> context, File outputFile) {
        var tileListener = ((ITileListener) context.getSource().getLevel()).dungeons_packer$getTileListener();
        try {
            tileListener.export(context.getSource(), outputFile);
        } catch (IOException e) {
            DungeonsPacker.LOGGER.error("Error while creating pak file", e);
            context.getSource().sendFailure(
                Component.literal("Error while creating pak file: " + e.getMessage())
            );
            return false;
        } catch (NoSuchAlgorithmException e) {
            context.getSource().sendFailure(
                Component.literal("SHA-1 Algorithm not found: " + e.getMessage())
            );
            DungeonsPacker.LOGGER.error("SHA-1 Algorithm not found", e);
            return false;
        }
        return true;
    }
}
