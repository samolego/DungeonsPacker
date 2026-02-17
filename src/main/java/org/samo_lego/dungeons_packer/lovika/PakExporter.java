package org.samo_lego.dungeons_packer.lovika;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.lovika.resource_pack.ResourcePackGenerator;
import org.samo_lego.dungeons_packer.lovika.resource_pack.TextureBytes;
import org.samo_lego.dungeons_packer.lovika.resource_pack.TextureEntry;
import org.samo_lego.dungeons_packer.lovika.tiles.Tile;
import org.samo_lego.japak.PakBuilder;
import org.samo_lego.japak.PakUnpacker;
import org.samo_lego.japak.structs.PakVersion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

public class PakExporter {
    private static final String OBJECTGROUP_PAK_PATH = "Dungeons/Content/data/lovika/objectgroups/{}/objectgroup.json";
    private static final String LEVEL_PAK_PATH = "Dungeons/Content/data/lovika/levels/{}.json";

    public static void writePak(
            CommandSourceStack executioner,
            File outputFile,
            String javaWorldName, DungeonLevel dungeonLevel,
            ObjectGroup objectGroup,
            Tile[] tiles,
            boolean dump,
            Map<BlockState, TextureEntry> usedTextures,
            Map<BlockState, TextureBytes> textureCache
    ) throws IOException, GeneralSecurityException {
        byte[] objectgroupJson = objectGroup.generateJson(tiles).getBytes();
        byte[] levelJson = dungeonLevel.generateJson(tiles).getBytes();

        var builder = new PakBuilder(outputFile, PakVersion.V3);
        String pakPath = OBJECTGROUP_PAK_PATH.replace("{}", javaWorldName);
        builder.addFile(pakPath, objectgroupJson);

        String levelPakPath = LEVEL_PAK_PATH.replace("{}", dungeonLevel.id);
        builder.addFile(levelPakPath, levelJson);

        ResourcePackGenerator.addTextures(builder, usedTextures, textureCache, executioner.getLevel());

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
    }
}
