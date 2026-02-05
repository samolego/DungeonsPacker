package org.samo_lego.dungeons_packer.lovika.tiles;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.ObjectGroup;
import org.samo_lego.japak.PakBuilder;
import org.samo_lego.japak.structs.PakVersion;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeSet;


public class TileListener {
    private static final String OBJECTGROUP_PAK_PATH = "Dungeons/Content/data/lovika/objectgroups/{}/objectgroup.json";
    private static final String LEVEL_PAK_PATH = "Dungeons/Content/data/lovika/levels/{}.json";

    private final String levelName;
    private final Set<BlockPos> startPositions;
    private final ObjectGroup tiles;

    @Nullable
    private TileCornerBlockEntity cornerEntity;

    public TileListener(String levelName) {
        this.levelName = levelName;
        this.startPositions = new TreeSet<>();
        this.tiles = new ObjectGroup(null);
    }

    public void onStartPosAdded(BlockPos pos) {
        this.startPositions.add(pos);
    }

    public void onStartPosRemoved(BlockPos pos) {
        this.startPositions.remove(pos);
    }

    public void export(File outputFile) throws IOException, NoSuchAlgorithmException {
        byte[] objectgroupJson = this.tiles.toString().getBytes();
        byte[] levelJson = "".getBytes();

        var builder = new PakBuilder(outputFile, PakVersion.V3);
        String pakPath = OBJECTGROUP_PAK_PATH.replace("{}", this.levelName);
        builder.addFile(pakPath, objectgroupJson);

        // TODO: add ability to choose level to replace
        // or even support blueprint loader
        String levelPakPath = LEVEL_PAK_PATH.replace("{}", "archhaven");
        builder.addFile(levelPakPath, levelJson);

        builder.finish();
    }

    public void onCornerPlaced(TileCornerBlockEntity blockEntity) {
        if (this.cornerEntity == null) {
            this.cornerEntity = blockEntity;
        } else {
            this.cornerEntity.setMatchingCorner(blockEntity.getBlockPos());
            blockEntity.setMatchingCorner(this.cornerEntity.getBlockPos());
            this.cornerEntity = null;
        }
    }

    public void onCornerRemoved(TileCornerBlockEntity removed) {
        if (this.cornerEntity == removed) {
            this.cornerEntity = null;
        } else if (this.cornerEntity != null) {
            // We have a matching pair, connect it
            var matching = removed.getMatchingCorner();
            if (matching.isEmpty()) {
                DungeonsPacker.LOGGER.warn("Corner removed without matching corner set.");
                return;
            }

            var otherCornerBE = matching.get();
            otherCornerBE.setMatchingCorner(this.cornerEntity.getBlockPos());
            this.cornerEntity.setMatchingCorner(matching.get().getBlockPos());
            this.cornerEntity = null;
        } else {
            // Set other corner of the removed box as current corner
            removed.getMatchingCorner().ifPresent(be -> {
                this.cornerEntity = be;
                be.setMatchingCorner(null);
            });
        }
    }
}
