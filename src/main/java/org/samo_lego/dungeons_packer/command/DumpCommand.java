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

public class DumpCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> _dispatcher, CommandBuildContext _registryAccess) {
        return Commands.literal("dump")
            .executes(context -> {
                var folder = context.getSource().getLevel().getServer().getWorldPath(LevelResource.ROOT);
                var newFile = folder.resolve("dump.pak").toFile();
                var success = ExportCommand.execute(context, newFile, true);
                return success ? 1 : 0;
            });
    }
}
