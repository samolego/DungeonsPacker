package org.samo_lego.dungeons_packer.lovika.tiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.DungeonLevel;
import org.samo_lego.dungeons_packer.lovika.ObjectGroup;
import org.samo_lego.dungeons_packer.lovika.serialization.Vec3iSerializer;
import org.samo_lego.japak.PakBuilder;
import org.samo_lego.japak.PakUnpacker;
import org.samo_lego.japak.structs.PakVersion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public class TileListener {
    private static final String OBJECTGROUP_PAK_PATH = "Dungeons/Content/data/lovika/objectgroups/{}/objectgroup.json";
    private static final String LEVEL_PAK_PATH = "Dungeons/Content/data/lovika/levels/{}.json";
    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(Vec3i.class, new Vec3iSerializer())
            .disableHtmlEscaping()
            .create();

    private final String levelName;
    public final ObjectGroup objectGroup;
    private final DungeonLevel levelProperties;

    @Nullable
    private TileCornerBlockEntity cornerEntity;

    public TileListener(String levelName) {
        this.levelName = levelName;
        this.objectGroup = new ObjectGroup(null);
        this.levelProperties = new DungeonLevel(levelName);
    }

    public void export(CommandSourceStack executioner, File outputFile, boolean dump) throws IOException, GeneralSecurityException {
        var tiles = this.objectGroup.getTiles(executioner);
        byte[] objectgroupJson = this.objectGroup.generateJson(tiles).getBytes();
        byte[] levelJson = this.levelProperties.generateJson(tiles).getBytes();

        var builder = new PakBuilder(outputFile, PakVersion.V3);
        String pakPath = OBJECTGROUP_PAK_PATH.replace("{}", this.levelName);
        builder.addFile(pakPath, objectgroupJson);

        String levelPakPath = LEVEL_PAK_PATH.replace("{}", this.levelProperties.id);
        builder.addFile(levelPakPath, levelJson);
        builder.finish();

        if (dump) {
            // Unpack the created pak file to a folder next to it for easier debugging
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
