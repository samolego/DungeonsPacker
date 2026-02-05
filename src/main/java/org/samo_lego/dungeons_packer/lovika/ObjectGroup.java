package org.samo_lego.dungeons_packer.lovika;

import org.samo_lego.dungeons_packer.lovika.tiles.Tile;

import java.util.ArrayList;
import java.util.List;

import static org.samo_lego.dungeons_packer.DungeonsPacker.GSON;

public record ObjectGroup(List<Tile> objects) {
    public ObjectGroup {
        if (objects == null) {
            objects = new ArrayList<>();
        }
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
