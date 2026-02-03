package org.samo_lego.dungeons_exporter.lovika;

import org.samo_lego.dungeons_exporter.lovika.tiles.Tile;

import java.util.ArrayList;
import java.util.List;

import static org.samo_lego.dungeons_exporter.DungeonsExporter.GSON;

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
