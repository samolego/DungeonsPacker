package org.samo_lego.dungeons_packer.lovika.resource_pack;

import java.util.Map;
import java.util.TreeMap;

public class ResourcesJson {
    private final String pack_id = "MooncoreCaverns";
    private final String name = "resourcePack.mooncorecaverns.name";
    private final String description = "resourcePack.mooncorecaverns.description";
    private final TextureWrapper resources = new TextureWrapper(new TreeMap<>(), Map.of("atlas.items.meta", "images/item_texture.json",
                "atlas.terrain.meta", "images/terrain_texture.json",
                "block.graphics.meta", "blocks.json",
                "item.server.meta", "items.json"
    ));

    public void addTexture(String identifier, String path) {
        this.resources.textures().put(identifier, path);
    }

    private record TextureWrapper(Map<String, String> textures, Map<String, String> metas) { }
}
