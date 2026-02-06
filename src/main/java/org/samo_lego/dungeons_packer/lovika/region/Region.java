package org.samo_lego.dungeons_packer.lovika.region;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.Vec3i;
import org.samo_lego.dungeons_packer.lovika.Door;

public class Region extends Door {
    private final String tags;
    private final Type type;

    public Region(Vec3i relativePos, Vec3i size, String name, String tags, Type type) {
        super(relativePos, size, name);
        this.tags = tags;
        this.type = type;
    }

    public enum Type {
        @SerializedName("trigger")
        TRIGGER,
        @SerializedName("spawn")
        SPAWN,
        @SerializedName("")
        EMPTY;


        public static Type parse(String type) {
            if (type.isEmpty()) return EMPTY;
            return Type.valueOf(type.toUpperCase());
        }

        @Override
        public String toString() {
            if (this == EMPTY) return "";
            return this.name().toLowerCase();
        }
    }
}
