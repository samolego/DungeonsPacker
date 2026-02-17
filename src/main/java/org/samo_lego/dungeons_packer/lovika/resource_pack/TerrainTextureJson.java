package org.samo_lego.dungeons_packer.lovika.resource_pack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TerrainTextureJson {
    private final String resource_pack_name = "vanilla";
    private final String texture_name = "atlas.terrain";
    private int padding = 0;
    private int num_mip_levels = 4;
    private final Map<String, TextureWrapper> texture_data = new TreeMap<>();

    public void addTexture(String identifier, String identifier2) {
        if (this.texture_data.containsKey(identifier)) {
            var wrapper = this.texture_data.get(identifier);

            if (wrapper instanceof SingletonTexture singleton) {
                var multiTexture = singleton.toMultiTexture();
                multiTexture.add(identifier);
                this.texture_data.put(identifier, multiTexture);
            } else if (wrapper instanceof MultiTexture multi) {
                multi.add(identifier);
            }
        } else {
            this.texture_data.put(identifier, new SingletonTexture(identifier2));
        }
    }

    private interface TextureWrapper { }

    private record SingletonTexture(String textures) implements TextureWrapper {
        public MultiTexture toMultiTexture() {
            var multiTexture = new MultiTexture();
            multiTexture.add(this.textures);
            return multiTexture;
        }
    }

    private static class MultiTexture implements TextureWrapper {
        private final List<String> textures = new ArrayList<>();

        public MultiTexture(String ... textures) {
            this.textures.addAll(List.of(textures));
        }

        public void add(String texture) {
            this.textures.add(texture);
        }
    }
}
