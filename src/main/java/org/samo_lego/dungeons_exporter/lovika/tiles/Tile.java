package org.samo_lego.dungeons_exporter.lovika.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import org.samo_lego.dungeons_exporter.lovika.Door;
import org.samo_lego.dungeons_exporter.lovika.region.Region;

import java.util.ArrayList;
import java.util.List;

public record Tile(
        String id,
        BlockPos pos,
        Vec3i size,
        String blocks, // todo: change this
        String regionPlane,
        String heightPlane,
        String regionYPlane,
        List<Door> doors,
        List<Region> regions
) {
    public Tile {
        if (doors == null) {
            doors = new ArrayList<>();
        }
        if (regions == null) {
            regions = new ArrayList<>();
        }
    }
}
