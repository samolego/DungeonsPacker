package org.samo_lego.dungeons_packer.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.lovika.tiles.ITileListener;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class ExportCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        return Commands.literal("export")
                .then(Commands.argument("outputFile", StringArgumentType.greedyString())
                        .executes(context -> {
                           var path = StringArgumentType.getString(context, "outputFile");
                           return ExportCommand.execute(context, new File(path), false) ? 1 : 0;
                        }))
            .executes(context -> {
                var folder = context.getSource().getLevel().getServer().getWorldPath(LevelResource.ROOT);
                var newFile = folder.resolve("exported.pak").toFile();
                var success = ExportCommand.execute(context, newFile, false);
                return success ? 1 : 0;
            });
    }

    public static boolean execute(CommandContext<CommandSourceStack> context, File outputFile, boolean dump) {
        var tileListener = ((ITileListener) context.getSource().getLevel()).dungeons_packer$getTileListener();
        try {
            tileListener.export(context.getSource(), outputFile, dump);

            var message = dump ? "commands.dungeons_packer.dump.success" : "commands.dungeons_packer.export.success";
            context.getSource().sendSuccess(
                () -> Component.translatable(message, Component.literal(String.valueOf(outputFile.toPath().normalize()))).withStyle(ChatFormatting.GREEN),
                false
            );
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
        } catch (GeneralSecurityException e) {
            context.getSource().sendFailure(
                Component.literal("General security exception: " + e.getMessage())
            );
            DungeonsPacker.LOGGER.error("General security exception", e);
            return false;
        }
        return true;
    }
}
