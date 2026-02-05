package org.samo_lego.dungeons_packer.lovika;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Vec3i;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.serialization.Vec3iSerializer;
import org.samo_lego.dungeons_packer.lovika.tiles.Tile;

import java.util.HashSet;
import java.util.Set;

public record ObjectGroup(Set<TileCornerBlockEntity> objects) {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeHierarchyAdapter(Vec3i.class, new Vec3iSerializer())
        .setPrettyPrinting()
        .create();

    public ObjectGroup {
        if (objects == null) {
            objects = new HashSet<>();
        }
    }

    public String generateJson(CommandSourceStack executioner) {
        // Convert to JSON
        Tile[] tiles = new Tile[this.objects.size()];
        int i = 0;
        for (var be : this.objects) {
            var tile = Tile.fromTileCornerBlock(executioner, be);
            if (tile.isPresent()) {
                tiles[i] = tile.get();
            }
            ++i;
        }
        return GSON.toJson(tiles);
    }
}
