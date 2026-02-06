package org.samo_lego.dungeons_packer.lovika;

import net.minecraft.core.Vec3i;

public class Door {
    private final String name;
    private final Vec3i size;
    private final Vec3i pos;

    public Door(Vec3i relativePos, Vec3i size, String name) {
        this.pos = relativePos;
        this.size = size;
        this.name = name;
    }
}
