package org.samo_lego.dungeons_packer.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.storage.LevelResource;

public class DumpCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> _dispatcher, CommandBuildContext _registryAccess) {
        return Commands.literal("dump")
            .executes(context -> {
                var folder = context.getSource().getLevel().getServer().getWorldPath(LevelResource.ROOT);
                var newFile = folder.resolve("dump.pak").toFile();
                ExportCommand.execute(context, newFile, true);
                return 0;
            });
    }
}
