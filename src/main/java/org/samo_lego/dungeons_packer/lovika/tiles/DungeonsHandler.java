package org.samo_lego.dungeons_packer.lovika.tiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.DungeonLevel;
import org.samo_lego.dungeons_packer.lovika.ObjectGroup;
import org.samo_lego.dungeons_packer.lovika.resource_pack.ResourceGenerator;
import org.samo_lego.dungeons_packer.lovika.resource_pack.TextureBytes;
import org.samo_lego.dungeons_packer.lovika.serialization.Vec3iSerializer;
import org.samo_lego.japak.PakBuilder;
import org.samo_lego.japak.PakUnpacker;
import org.samo_lego.japak.structs.PakVersion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class DungeonsHandler {
    private static final String OBJECTGROUP_PAK_PATH = "Dungeons/Content/data/lovika/objectgroups/{}/objectgroup.json";
    private static final String LEVEL_PAK_PATH = "Dungeons/Content/data/lovika/levels/{}.json";
    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(Vec3i.class, new Vec3iSerializer())
            .disableHtmlEscaping()
            .create();

    private final String levelName;
    public final ObjectGroup objectGroup;
    private final DungeonLevel levelProperties;
    public Map<BlockState, TextureBytes> textureCache;

    @Nullable
    private TileCornerBlockEntity cornerEntity;
    private Runnable finishExport;

    public DungeonsHandler(String levelName) {
        this.levelName = levelName;
        this.objectGroup = new ObjectGroup(new HashSet<>());
        this.levelProperties = new DungeonLevel(levelName);
        this.textureCache = new Reference2ObjectOpenHashMap<>();
    }

    public void export(CommandSourceStack executioner, File outputFile, boolean dump) {
        var resourceGen = new ResourceGenerator(this.textureCache.keySet());

        var tiles = this.objectGroup.getTiles(executioner, resourceGen);
        if (tiles.length < 2) {
            executioner.sendSystemMessage(Component.translatable("commands.dungeons_packer.export.not_enough_tiles").withStyle(ChatFormatting.RED));
            return;
        }

        resourceGen.fetchClientTextures(executioner.getPlayer());
        this.finishExport = () -> {
            byte[] objectgroupJson = this.objectGroup.generateJson(tiles).getBytes();
            byte[] levelJson = this.levelProperties.generateJson(tiles).getBytes();

            try {
                var builder = new PakBuilder(outputFile, PakVersion.V3);
                String pakPath = OBJECTGROUP_PAK_PATH.replace("{}", this.levelName);
                builder.addFile(pakPath, objectgroupJson);

                String levelPakPath = LEVEL_PAK_PATH.replace("{}", this.levelProperties.id);
                builder.addFile(levelPakPath, levelJson);
                builder.finish();

                if (dump) {
                    // Unpack the created pak file to a folder for easier debugging
                    var unpaker = new PakUnpacker(String.valueOf(outputFile));
                    var parent = outputFile.getParentFile();
                    List<String> files = unpaker.listFiles();
                    for (String file : files) {
                        var f = new File(parent, file);
                        f.getParentFile().mkdirs();
                        try (var out = new FileOutputStream(f)) {
                            out.write(unpaker.readFile(file));
                        }
                    }
                }


                var message = dump ? "commands.dungeons_packer.dump.success" : "commands.dungeons_packer.export.success";
                executioner.sendSuccess(
                        () -> Component.translatable(message, Component.literal(String.valueOf(outputFile.toPath().normalize())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GREEN),
                        false
                );
            } catch (FileNotFoundException e) {
                DungeonsPacker.LOGGER.error("Error while creating pak file", e);
                executioner.sendFailure(Component.literal("Error while creating pak file: " + e.getMessage()));
            } catch (IOException | NoSuchAlgorithmException e) {
                executioner.sendFailure(Component.literal("SHA-1 Algorithm not found: " + e.getMessage()));
                DungeonsPacker.LOGGER.error("SHA-1 Algorithm not found", e);
            } catch (GeneralSecurityException e) {
                executioner.sendFailure(Component.literal("General security exception: " + e.getMessage()));
                DungeonsPacker.LOGGER.error("General security exception", e);
            }
        };
    }

    public void onCornerPlaced(TileCornerBlockEntity blockEntity) {
        if (this.cornerEntity == null) {
            this.cornerEntity = blockEntity;
        } else {
            this.cornerEntity.setMatchingCorner(blockEntity.getBlockPos());
            blockEntity.setMatchingCorner(this.cornerEntity.getBlockPos());

            if (this.cornerEntity.isMainCorner()) {
                this.objectGroup.objects().add(this.cornerEntity);
            } else {
                this.objectGroup.objects().add(blockEntity);
            }

            this.cornerEntity = null;
        }
    }

    public void onCornerRemoved(TileCornerBlockEntity removed) {
        if (this.cornerEntity == removed) {
            this.cornerEntity = null;
        } else if (this.cornerEntity != null) {
            // We have to create a new matching pair
            var matching = removed.getMatchingCorner();
            if (matching.isEmpty()) {
                DungeonsPacker.LOGGER.warn("Corner removed without matching corner set.");
                return;
            }

            var otherCornerBE = matching.get();
            otherCornerBE.setMatchingCorner(this.cornerEntity.getBlockPos());
            this.cornerEntity.setMatchingCorner(matching.get().getBlockPos());
            this.cornerEntity = null;
        } else {
            // Set other corner of the removed box as current corner
            removed.getMatchingCorner().ifPresent(be -> {
                this.cornerEntity = be;
                be.setMatchingCorner(null);
            });
        }
    }
}
