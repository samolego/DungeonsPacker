package org.samo_lego.dungeons_packer.lovika;

import net.minecraft.commands.CommandSourceStack;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.resource_pack.ResourceGenerator;
import org.samo_lego.dungeons_packer.lovika.tiles.Tile;

import java.util.HashSet;
import java.util.Set;

import static org.samo_lego.dungeons_packer.lovika.tiles.DungeonsHandler.GSON;

public record ObjectGroup(Set<TileCornerBlockEntity> objects)  {

    public ObjectGroup {
        if (objects == null) {
            objects = new HashSet<>();
        }
    }


    public String generateJson(Tile[] tiles) {
        return String.format("{ \"objects\": %s }", GSON.toJson(tiles));
    }

    public Tile[] getTiles(CommandSourceStack executioner, ResourceGenerator resourceGen) {
        Tile[] tiles = new Tile[this.objects.size()];
        int i = 0;
        for (var be : this.objects) {
            var tile = Tile.fromTileCornerBlock(executioner, be, resourceGen);
            if (tile.isPresent()) {
                tiles[i] = tile.get();
            }
            ++i;
        }

        return tiles;
    }
}
