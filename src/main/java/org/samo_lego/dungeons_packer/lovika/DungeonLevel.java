package org.samo_lego.dungeons_packer.lovika;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.samo_lego.dungeons_packer.lovika.serialization.BooleanStringSerializer;
import org.samo_lego.dungeons_packer.lovika.tiles.Tile;
import org.samo_lego.dungeons_packer.lovika.tiles.TileListener;
import org.samo_lego.dungeons_packer.lovika.tiles.TileProperties;
import org.samo_lego.dungeons_packer.lovika.tiles.TileProperties.Objective;
import org.samo_lego.dungeons_packer.lovika.tiles.TileProperties.Stretch;

import java.util.ArrayList;
import java.util.List;


public class DungeonLevel {
    public final String id = "archhaven";

    @SerializedName("resource-packs")
    public List<String> resourcePacks = List.of("squidcoast");

    @SerializedName("ambience-level-id")
    public String ambienceLevelId = "cacticanyon";

    @SerializedName("play-intro")
    @JsonAdapter(BooleanStringSerializer.class)
    public boolean playIntro = true;

    @SerializedName("object-groups")
    public List<String> objectGroups;

    public List<Stretch> stretches = new ArrayList<>();

    public List<Objective> objectives = List.of(TileProperties.END_OBJECTIVE);


    public DungeonLevel(String worldName) {
        this.objectGroups = List.of(worldName + "/objectgroup");
    }

    public String generateJson(Tile[] tiles) {
        // Generate one stretch for each tile
        // todo: create a fancy screen to manage this?
        this.stretches.clear();
        for (var tile : tiles) {
            this.stretches.add(new Stretch(List.of(tile.id()), 1));
        }

        return TileListener.GSON.toJson(this);
    }
}
