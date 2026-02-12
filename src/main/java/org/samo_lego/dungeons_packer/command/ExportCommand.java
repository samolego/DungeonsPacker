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
import org.samo_lego.dungeons_packer.lovika.tiles.IDungeonsHandlerProvider;

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
                           ExportCommand.execute(context, new File(path), false);
                           return 0;
                        }))
            .executes(context -> {
                var folder = context.getSource().getLevel().getServer().getWorldPath(LevelResource.ROOT);
                var newFile = folder.resolve("exported.pak").toFile();
                ExportCommand.execute(context, newFile, false);
                return 0;
            });
    }

    public static void execute(CommandContext<CommandSourceStack> context, File outputFile, boolean dump) {
        var tileListener = ((IDungeonsHandlerProvider) context.getSource().getLevel()).dungeons_packer$getDungeonsHandler();
        tileListener.export(context.getSource(), outputFile, dump);
    }
}
