package org.samo_lego.dungeons_packer.lovika;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.tiles.Tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.samo_lego.dungeons_packer.DungeonsPacker.GSON;

public record ObjectGroup(Set<TileCornerBlockEntity> objects) {
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
