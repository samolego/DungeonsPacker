package org.samo_lego.dungeons_packer.lovika.resource_pack;

import org.samo_lego.dungeons_packer.lovika.serialization.ICustomJsonSerializable;
import org.samo_lego.dungeons_packer.lovika.tiles.DungeonsHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TerrainTextureJson {
    private static final String TEXTURES = "textures";
    private final String resource_pack_name = "vanilla";
    private final String texture_name = "atlas.terrain";
    private int padding = 0;
    private int num_mip_levels = 4;
    private final Map<String, ICustomJsonSerializable> texture_data = new TreeMap<>();


    public void addTexture(String identifier, String identifier2, byte index) {
        if (this.texture_data.containsKey(identifier)) {
            var wrapper = this.texture_data.get(identifier);
            if (wrapper instanceof SingletonTexture singleton) {
                var multiTexture = singleton.toMultiTexture();
                multiTexture.add(identifier2, index);
                this.texture_data.put(identifier, multiTexture);
            } else if (wrapper instanceof MultiTexture multi) {
                multi.add(identifier2, index);
            }
        } else {
            this.texture_data.put(identifier, new SingletonTexture(identifier2, index));
        }
    }

    private record SingletonTexture(String textures, byte index) implements ICustomJsonSerializable {
        public MultiTexture toMultiTexture() {
            var multiTexture = new MultiTexture();
            multiTexture.add(this.textures, this.index);
            return multiTexture;
        }

        @Override
        public Object getSerializationObject() {
            return Map.of(TEXTURES, this.textures);
        }
    }

    private static class MultiTexture implements ICustomJsonSerializable {
        private final Map<Byte, String> textures = new TreeMap<>();

        public void add(String texture, byte index) {
            this.textures.put(index, texture);
        }

        @Override
        public Object getSerializationObject() {
            return Map.of(TEXTURES, this.textures.values());
        }
    }
}
