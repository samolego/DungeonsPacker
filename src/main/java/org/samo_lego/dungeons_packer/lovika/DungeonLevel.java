package org.samo_lego.dungeons_packer.lovika;

import com.google.gson.annotations.SerializedName;
import org.samo_lego.dungeons_packer.lovika.tiles.Tile;
import org.samo_lego.dungeons_packer.lovika.tiles.TileProperties.Stretch;

import java.util.ArrayList;
import java.util.List;

public class DungeonLevel {
    public final String id = "archhaven";

    @SerializedName("resource-packs")
    public List<String> resourcePacks = List.of("CactiCanyon");

    @SerializedName("ambience-level-id")
    public String ambienceLevelId = "archhaven";

    @SerializedName("play-intro")
    public boolean playIntro = true;

    @SerializedName("object-groups")
    public List<String> objectGroups = new ArrayList<>();

    public List<Stretch> stretches = new ArrayList<>();

    public DungeonLevel() {
    }
}
