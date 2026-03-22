package org.samo_lego.dungeons_packer.lovika.paking.cooked;

import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.ue4.UE4AssetModifier;
import org.samo_lego.japak.PakBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class CookedResourceWriter {
    private static final String ASSETS_PREFIX = "/assets/%s";

    private static final String DEFAULT_LEVEL_NAME = "ArchHaven";  // todo- once we figure out custom level loading, switch to "LevelName16chars"

    private static final String TILE_UMAP = "Dungeons/Content/Decor/Maps/%s/SubLevels/tile7ch.umap".formatted(DEFAULT_LEVEL_NAME);
    private static final String TILE_UEXP = "Dungeons/Content/Decor/Maps/%s/SubLevels/tile7ch.uexp".formatted(DEFAULT_LEVEL_NAME);

    private static final String XBLUEPRINT_PATHS = "Dungeons/Content/%s/{}".formatted(DungeonsPacker.MOD_ID);
    private static final String XBLUEPRINT_PATH_UASSET = XBLUEPRINT_PATHS.replace("{}", "BP_XBlueprintLoader.uasset");
    private static final String XBLUEPRINT_PATH_UEXP = XBLUEPRINT_PATHS.replace("{}", "BP_XBlueprintLoader.uexp");
    private static final String XBLUEPRINT_PATH_PREFABS_UASSET = XBLUEPRINT_PATHS.replace("{}", "prefabs.uasset");
    private static final String XBLUEPRINT_PATH_PREFABS_UEXP = XBLUEPRINT_PATHS.replace("{}", "prefabs.uexp");
    private static final String XBLUEPRINT_PATH_ACTORLIB_UASSET = XBLUEPRINT_PATHS.replace("{}", "S_ActorLibrary.uasset");
    private static final String XBLUEPRINT_PATH_ACTORLIB_UEXP = XBLUEPRINT_PATHS.replace("{}", "S_ActorLibrary.uexp");



    public static void writeTiles(PakBuilder builder, String levelName, Iterable<String> tileIds) {
        if (levelName.length() != DEFAULT_LEVEL_NAME.length()) {
            DungeonsPacker.LOGGER.error("Invalid mission name length: {} (length: {}, expected {})", levelName, levelName.length(), DEFAULT_LEVEL_NAME.length());
        }

        try {
            for (var tileId : tileIds) {
                writeTileUmap(builder, levelName, tileId);
                writeTileUexp(builder, levelName, tileId);
            }
            writeXBlueprintLoader(builder);
        } catch (IOException | NoSuchAlgorithmException e) {
            DungeonsPacker.LOGGER.error("Error writing cooked resources: {}", e.getLocalizedMessage());
        }
    }

    private static void writeTileUmap(PakBuilder builder, String missionName, String tileId) throws IOException, NoSuchAlgorithmException {
        if (tileId.length() != 7) {
            DungeonsPacker.LOGGER.error("Invalid tile id length: {} (length: {}, expected 7)", tileId, tileId.length());
        }

        var umap = new UE4AssetModifier(CookedResourceWriter.class.getResourceAsStream(ASSETS_PREFIX.formatted(TILE_UMAP)));
        umap.modifySubstring("tile7ch", tileId);
        umap.modifySubstring(DEFAULT_LEVEL_NAME, missionName);

        var umapPath = TILE_UMAP.replace(DEFAULT_LEVEL_NAME, missionName).replace("tile7ch", tileId);
        builder.addFile(umapPath, umap.getData());
    }

    private static void writeTileUexp(PakBuilder builder, String missionName, String tileName) throws IOException, NoSuchAlgorithmException {
        var uexpPath = TILE_UEXP.replace(DEFAULT_LEVEL_NAME, missionName).replace("tile7ch", tileName);
        var bytes = getBytes(ASSETS_PREFIX.formatted(TILE_UEXP));
        builder.addFile(uexpPath, bytes);
    }

    public static void writeXBlueprintLoader(PakBuilder builder) throws IOException, NoSuchAlgorithmException {
        builder.addFile(XBLUEPRINT_PATH_UASSET, getBytes(ASSETS_PREFIX.formatted(XBLUEPRINT_PATH_UASSET)));
        builder.addFile(XBLUEPRINT_PATH_UEXP, getBytes(ASSETS_PREFIX.formatted(XBLUEPRINT_PATH_UEXP)));

        builder.addFile(XBLUEPRINT_PATH_PREFABS_UASSET, getBytes(ASSETS_PREFIX.formatted(XBLUEPRINT_PATH_PREFABS_UASSET)));
        builder.addFile(XBLUEPRINT_PATH_PREFABS_UEXP, getBytes(ASSETS_PREFIX.formatted(XBLUEPRINT_PATH_PREFABS_UEXP)));

        builder.addFile(XBLUEPRINT_PATH_ACTORLIB_UASSET, getBytes(ASSETS_PREFIX.formatted(XBLUEPRINT_PATH_ACTORLIB_UASSET)));
        builder.addFile(XBLUEPRINT_PATH_ACTORLIB_UEXP, getBytes(ASSETS_PREFIX.formatted(XBLUEPRINT_PATH_ACTORLIB_UEXP)));
    }

    private static byte[] getBytes(String path) throws IOException {
        try (var resource = CookedResourceWriter.class.getResourceAsStream(path)) {
            return resource.readAllBytes();
        }
    }
}
