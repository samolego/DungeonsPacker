package org.samo_lego.dungeons_packer.lovika.resource_pack;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.lovika.block_conversion.Block2IdGenerator;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap2;
import org.samo_lego.dungeons_packer.lovika.tiles.DungeonsHandler;
import org.samo_lego.japak.PakBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class ResourcePackGenerator {
    private static final String PACK_NAME = "test";
    private static final String BLOCKS_JSON = "Dungeons/Content/data/resourcepacks/%s/blocks.json".formatted(PACK_NAME);
    private static final String RESOURCES_JSON = "Dungeons/Content/data/resourcepacks/%s/resources.json".formatted(PACK_NAME);
    private static final String TERRAIN_TEXTURE_JSON = "Dungeons/Content/data/resourcepacks/%s/images/terrain_texture.json".formatted(PACK_NAME);
    private static final String BLOCKS_LOCATION = "Dungeons/Content/data/resourcepacks/%s/images/blocks/{}".formatted(PACK_NAME);

    public static void addTextures(
            PakBuilder builder,
            Map<BlockState, TextureEntry> usedTextures,
            Map<BlockState, TextureBytes> textures,
            ServerLevel level) throws IOException, NoSuchAlgorithmException {

        var terrainTexture = new TerrainTextureJson();
        var resources = new ResourcesJson();

        // This will be for our blocks.json
        var resourceId2texturePath = new TreeMap<String, BlocksJsonTextureEntry>();

        // Add air
        resourceId2texturePath.put(
                Block2IdGenerator.getResourceStringId(BlockMap2.DUNGEONS_AIR).orElse("air"),
                BlocksJsonTextureEntry.blockshapeOnly(BlockShape.INVISIBLE)
        );

        var blockstate2texture = usedTextures.entrySet().stream()
                .sorted((Map.Entry.comparingByValue(Comparator.comparingInt(TextureEntry::blockId))))
                .toList();

        var debugBlocks = new ArrayList<String>();

        for (var entry : blockstate2texture) {
            var blockState = entry.getKey();
            int blockstateId = Block.getId(blockState);
            var textureEntry = entry.getValue();
            var resourceId = textureEntry.resourceId();

            var blockTextures = textures.get(blockState);
            if (blockTextures != null) {
                for (var identifier : blockTextures.textureId2bytes().keySet()) {
                    var bytes = blockTextures.textureId2bytes().get(identifier);
                    if (bytes != null) {
                        String fileName = identifier.getPath().substring(identifier.getPath().indexOf("/") + 1).replace('/', '_') + "_" + blockstateId;
                        String path = BLOCKS_LOCATION.replace("{}", fileName + ".png");
                        builder.addFile(path, bytes);

                        // All the directions that use this texture
                        var blockShape = BlockShape.fromBlockState(blockState, level);
                        resourceId2texturePath.computeIfAbsent(resourceId, _ -> new BlocksJsonTextureEntry());

                        if (blockShape != null) {
                            resourceId2texturePath.get(resourceId).blockshape = blockShape;
                        }

                        var directions = Arrays.stream(Direction.values())
                                .filter(dir -> identifier.equals(blockTextures.direction2textureId().get(dir))).toList();

                        if (!directions.isEmpty()) {
                            directions.forEach(dir -> {
                                var name = resourceId + "_" + dir.getName();
                                resourceId2texturePath.get(resourceId).put(dir, name);

                                // Put into terrain texture
                                var resourceName = "block." + fileName;
                                terrainTexture.addTexture(name, resourceName, textureEntry.blockData());
                                resources.addTexture(resourceName, "images/blocks/" + fileName + ".png");
                            });

                            debugBlocks.add("%s (id: %s, data %s) -> %s, directions: %s".formatted(
                                    resourceId,
                                    String.format("0x%04X", textureEntry.blockId()),
                                    Integer.toBinaryString(textureEntry.blockData()),
                                    fileName,
                                    directions.stream().map(Direction::name).toList()
                            ));
                        }
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

        builder.addFile("Dungeons/Content/data/resourcepacks/%s/debug.txt".formatted(PACK_NAME), DungeonsHandler.GSON.toJson(debugBlocks).getBytes());
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
