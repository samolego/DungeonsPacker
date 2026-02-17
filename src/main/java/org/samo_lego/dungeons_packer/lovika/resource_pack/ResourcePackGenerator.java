package org.samo_lego.dungeons_packer.lovika.resource_pack;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.lovika.tiles.DungeonsHandler;
import org.samo_lego.japak.PakBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class ResourcePackGenerator {
    private static final String PACK_NAME = "test";
    private static final String BLOCKS_JSON = "Dungeons/Content/data/resourcepacks/%s/blocks.json".formatted(PACK_NAME);
    private static final String RESOURCES_JSON = "Dungeons/Content/data/resourcepacks/%s/resources.json".formatted(PACK_NAME);
    private static final String TERRAIN_TEXTURE_JSON = "Dungeons/Content/data/resourcepacks/%s/images/terrain_texture.json".formatted(PACK_NAME);
    private static final String BLOCKS_LOCATION = "Dungeons/Content/data/resourcepacks/%s/images/blocks/{}".formatted(PACK_NAME);

    public static void addTextures(
            PakBuilder builder,
            Map<BlockState, TextureEntry> usedTextures,
            Map<BlockState, TextureBytes> textures
    ) throws IOException, NoSuchAlgorithmException {

        var terrainTexture = new TerrainTextureJson();
        var resources = new ResourcesJson();

        // This will be for our blocks.json
        var resourceId2texturePath = new TreeMap<String, BlocksJsonTextureEntry>();
        // Add air
        resourceId2texturePath.put("air", BlocksJsonTextureEntry.blockshapeOnly(BlockShape.INVISIBLE));

        for (var entry : usedTextures.entrySet()) {
            var blockState = entry.getKey();
            int blockstateId = Block.getId(blockState);
            var textureEntry = entry.getValue();
            var resourceId = textureEntry.resourceId();

            var blockTextures = textures.get(blockState);
            if (blockTextures != null) {
                for (var identifier : blockTextures.bytes().keySet()) {
                    var bytes = blockTextures.bytes().get(identifier);
                    if (bytes != null) {
                        String fileName = identifier.getPath().substring(identifier.getPath().indexOf("/") + 1).replace('/', '_') + "_" + blockstateId;
                        String path = BLOCKS_LOCATION.replace("{}", fileName + ".png");
                        builder.addFile(path, bytes);

                        // All the directions that use this texture
                        resourceId2texturePath.computeIfAbsent(resourceId, _ -> new BlocksJsonTextureEntry());
                        Arrays.stream(Direction.values())
                                .filter(dir -> identifier.equals(blockTextures.sideMappings().get(dir)))
                                .forEach(dir -> resourceId2texturePath.get(resourceId).put(dir, fileName));

                        // Put into terrain texture
                        var resourceName = "block." + fileName;
                        terrainTexture.addTexture(fileName, resourceName);
                        resources.addTexture(resourceName, "images/blocks/" + fileName + ".png");
                    }
                }
            }
        }
        var blocksJson = DungeonsHandler.GSON.toJson(resourceId2texturePath);
        builder.addFile(BLOCKS_JSON, blocksJson.getBytes());

        var terrainTextureJson = DungeonsHandler.GSON.toJson(terrainTexture);
        builder.addFile(TERRAIN_TEXTURE_JSON, terrainTextureJson.getBytes());

        var resourcesJson = DungeonsHandler.GSON.toJson(resources);
        builder.addFile(RESOURCES_JSON, resourcesJson.getBytes());
    }

    private static class BlocksJsonTextureEntry {
        private Map<Direction, String> textures;
        private BlockShape blockshape;

        public BlocksJsonTextureEntry() {
            this.textures = new TreeMap<>();
            this.blockshape = null;
        }

        public static BlocksJsonTextureEntry blockshapeOnly(BlockShape shape) {
            var entry = new BlocksJsonTextureEntry();
            entry.textures = null;
            entry.blockshape = shape;

            return entry;
        }

        public void put(Direction direction, String path) {
            this.textures.put(direction, path);
        }
    }

}
