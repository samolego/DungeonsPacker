package org.samo_lego.dungeons_packer.lovika.region;

import net.minecraft.core.Vec3i;
import org.samo_lego.dungeons_packer.lovika.Door;

public class Region extends Door {
    private final String tags;
    private final Type type;

    public Region(Vec3i pos, Vec3i size, String name, String tags, Type type) {
        super(pos, size, name);
        this.tags = tags;
        this.type = type;
    }

    public enum Type {
        TRIGGER,
        EMPTY;


        public static Type parse(String type) {
            if (type.isEmpty()) return EMPTY;
            return Type.valueOf(type);
        }

        @Override
        public String toString() {
            if (this == EMPTY) return "";
            return this.name().toLowerCase();
        }
    }
}
