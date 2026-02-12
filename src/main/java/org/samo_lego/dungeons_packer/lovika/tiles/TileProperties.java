package org.samo_lego.dungeons_packer.lovika.tiles;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static org.samo_lego.dungeons_packer.block.EndMissionBlock.END;


public class TileProperties {
    public static final Objective END_OBJECTIVE = new Objective("MainObjective", new Objective.Gauntlet("*.*.%s".formatted(END)));

    public record Stretch(
            @SerializedName("tiles") List<String> tileIds,
            int length
    ) {}

    public record Objective(
            String displayMode,
            Gauntlet gauntlet
    ) {
        public record Gauntlet(@SerializedName("end-region") String endRegion) { }
    }
}
