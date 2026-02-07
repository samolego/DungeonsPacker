package org.samo_lego.dungeons_packer.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;
import org.samo_lego.japak.PakBuilder;
import org.samo_lego.japak.PakUnpacker;
import org.samo_lego.japak.structs.PakVersion;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import static net.minecraft.commands.Commands.literal;

public class PakCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx, CommandSelection selection) {

        dispatcher.register(literal("pak")
            .then(literal("pack")
                    .then(Commands.argument("input directory", StringArgumentType.greedyString())
                            .executes(context -> {
                                var input = StringArgumentType.getString(context, "input directory");
                                var file = new File(input);
                                if (!file.exists() || !file.isDirectory()) {
                                    context.getSource().sendFailure(
                                        Component.translatable("commands.pak.pack.invalid_directory", Component.literal(input)).withStyle(ChatFormatting.RED)
                                    );
                                    return 0;
                                }
                                var success = PakCommand.pack(context, input);
                                return success ? 1 : 0;
                            })
                    )
                    .executes(context -> {
                        var folder = context.getSource().getLevel().getServer().getWorldPath(LevelResource.ROOT).resolve("Dungeons").normalize().toAbsolutePath();
                        var success = PakCommand.pack(context, String.valueOf(folder));
                        return success ? 1 : 0;
                    })
            )
            .then(literal("unpack")
                    .then(Commands.argument("input file", StringArgumentType.greedyString())
                            .executes(context -> {
                                var input = StringArgumentType.getString(context, "input file");
                                var file = new File(input);
                                if (!file.exists() || !file.isFile()) {
                                    context.getSource().sendFailure(
                                        Component.translatable("commands.pak.unpack.invalid_file", Component.literal(input)).withStyle(ChatFormatting.RED)
                                    );
                                    return 0;
                                }
                                var success = PakCommand.unpack(context, input);
                                return success ? 1 : 0;
                            })
                    )
            )
        );
    }

    private static boolean pack(CommandContext<CommandSourceStack> context, String input) {
        var folder = new File(input);

        var outputFolder = context.getSource().getLevel().getServer().getWorldPath(LevelResource.ROOT);
        var outputFile = outputFolder.resolve(folder.getName() + ".pak").normalize().toFile();
        Path basePath = folder.toPath();

        try (var walker = Files.walk(basePath)) {
            var pak = new PakBuilder(outputFile, PakVersion.V3);
            walker.filter(Files::isRegularFile)
                .forEach(path -> {
                    String relativePath = basePath.getParent().relativize(path)
                            .toString()
                            .replace("\\", "/");
                    try {
                        byte[] content = Files.readAllBytes(path);
                        pak.addFile(relativePath, content);
                    } catch (IOException | NoSuchAlgorithmException e) {
                        context.getSource().sendFailure(
                            Component.translatable("commands.pak.pack.fail", Component.literal(String.valueOf(path)).withStyle(ChatFormatting.RED))
                        );
                    }
                });

            pak.finish();

            context.getSource().sendSuccess(
                () -> Component.translatable("commands.pak.pack.success", Component.literal(outputFile.getAbsolutePath())).withStyle(ChatFormatting.GREEN),
                false
            );

        } catch (IOException | NoSuchAlgorithmException e) {
            context.getSource().sendFailure(
                    Component.translatable("commands.pak.pack.fail", Component.literal(e.getMessage()).withStyle(ChatFormatting.RED))
            );
            return false;
        }

        return true;
    }


    private static boolean unpack(CommandContext<CommandSourceStack> context, String input) {
        var outputFolder = context.getSource().getLevel().getServer().getWorldPath(LevelResource.ROOT);
        try {
            var unpacker = new PakUnpacker(input);
            for (String filePath : unpacker.listFiles()) {
                byte[] data = unpacker.readFile(filePath);
                var outputFile = outputFolder.resolve(filePath).toFile();
                outputFile.getParentFile().mkdirs();
                Files.write(outputFile.toPath(), data);
            }

            context.getSource().sendSuccess(
                () -> Component.translatable("commands.pak.unpack.success", Component.literal(outputFolder.toString()).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GREEN),
                false
            );

        } catch (GeneralSecurityException | IOException e) {
            context.getSource().sendFailure(
                Component.translatable("commands.pak.unpack.fail", Component.literal(e.getMessage()).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.RED)
            );
             return false;
        }
        return true;
    }
}
