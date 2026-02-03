package org.samo_lego.dungeons_exporter.lovika;

import net.minecraft.core.Vec3i;

public class Door implements Sizeable {
    private final String name;
    private Vec3i pos2;
    private Vec3i pos;

    public Door(Vec3i pos, Vec3i size, String name) {
        this.pos = pos;
        this.pos2 = pos.offset(size);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Vec3i getPos() {
        return this.pos;
    }

    @Override
    public Vec3i getPos2() {
        return this.pos2;
    }

    @Override
    public void setPos(Vec3i pos) {
        this.pos = pos;
    }

    @Override
    public void setSize(Vec3i size) {
        this.pos2 = this.pos.offset(size);
    }

    @Override
    public void setPos2(Vec3i pos2) {
        this.pos2 = pos2;
    }
}
