package org.samo_lego.dungeons_packer.lovika.tiles;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TileProperties {
    public Stretch stretch = null;

    public record Stretch(
            @Expose String id,
            @Expose String tileId,
            @Expose String objectGroupId
    ) {}

    public record Objective(
            String displayMode,
            Gauntlet gauntlet
    ) {
        public record Gauntlet(@SerializedName("end-region") String endRegion) { }
    }
}
