package org.samo_lego.dungeons_packer.lovika.paking;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.lovika.DungeonLevel;
import org.samo_lego.dungeons_packer.lovika.ObjectGroup;
import org.samo_lego.dungeons_packer.lovika.paking.cooked.CookedResourceWriter;
import org.samo_lego.dungeons_packer.lovika.resource_pack.ResourcePackGenerator;
import org.samo_lego.dungeons_packer.lovika.resource_pack.TextureBytes;
import org.samo_lego.dungeons_packer.lovika.resource_pack.TextureEntry;
import org.samo_lego.dungeons_packer.lovika.tiles.Tile;
import org.samo_lego.japak.PakBuilder;
import org.samo_lego.japak.PakUnpacker;
import org.samo_lego.japak.structs.PakVersion;

import javax.imageio.ImageIO;
import java.awt.color.*;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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
            Map<BlockState, TextureBytes> textureCache,
            HashMap<String, List<int[]>> prefabs
    ) throws IOException, GeneralSecurityException {
        byte[] objectgroupJson = objectGroup.generateJson(tiles).getBytes();
        byte[] levelJson = dungeonLevel.generateJson(tiles).getBytes();

        var builder = new PakBuilder(outputFile, PakVersion.V3);
        String pakPath = OBJECTGROUP_PAK_PATH.replace("{}", javaWorldName);
        builder.addFile(pakPath, objectgroupJson);

        String levelPakPath = LEVEL_PAK_PATH.replace("{}", dungeonLevel.id);
        builder.addFile(levelPakPath, levelJson);

        ResourcePackGenerator.addTextures(builder, usedTextures, textureCache, executioner.getLevel());

        writeBlueprintsData(builder, prefabs);

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

    private static void writeBlueprintsData(PakBuilder builder, HashMap<String, List<int[]>> prefabs) {
        CookedResourceWriter.writeTiles(builder, "archhaven", prefabs.keySet());

        for (var entry : prefabs.entrySet()) {
            var tileId = entry.getKey();
            var pixelRows = entry.getValue();

            if (!pixelRows.isEmpty()) {
                var image = getImage2(pixelRows);
                // Convert the image to bytes
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(image, "png", baos);
                    byte[] imageBytes = baos.toByteArray();

                    String path = String.format("Dungeons/Content/%s/%s.png", DungeonsPacker.MOD_ID, tileId);
                    builder.addFile(path, imageBytes);

                } catch (IOException | NoSuchAlgorithmException e) {
                    DungeonsPacker.LOGGER.error("Error writing blueprint texture for tile {}: {}", tileId, e.getLocalizedMessage());
                }
            }
        }
    }

    private static BufferedImage getImage(List<int[]> pixelRows) {
        int width = 4;
        int height = pixelRows.size();

        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            int[] row = pixelRows.get(y);
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, row[x]);
            }
        }
        return image;
    }
    private static BufferedImage getImage2(List<int[]> pixelRows) {
        int width = 4;
        int height = pixelRows.size();

        // We use a custom color model to prevent ImageIO from applying sRGB/Gamma shifts
        // This treats the image as a raw data array.
        var colorModel = new DirectColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB),
                32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000,
                false, DataBuffer.TYPE_INT);

        var raster = colorModel.createCompatibleWritableRaster(width, height);
        var image = new BufferedImage(colorModel, raster, false, null);

        for (int y = 0; y < height; y++) {
            int[] row = pixelRows.get(y);
            for (int x = 0; x < width; x++) {
                // Note: Use the raster directly to avoid any color model interference
                image.setRGB(x, y, row[x]);
            }
        }
        return image;
    }
}
