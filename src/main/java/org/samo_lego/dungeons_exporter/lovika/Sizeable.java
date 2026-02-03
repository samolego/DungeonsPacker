package org.samo_lego.dungeons_exporter.lovika;

import net.minecraft.core.Vec3i;

public interface Sizeable {
    Vec3i getPos();
    Vec3i getPos2();

    default Vec3i getSize() {
        return getPos2().subtract(getPos());
    }

    void setPos(Vec3i pos);
    void setSize(Vec3i size);
    void setPos2(Vec3i pos2);
}
