package org.samo_lego.dungeons_packer.lovika.tiles;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.DungeonLevel;
import org.samo_lego.dungeons_packer.lovika.ObjectGroup;
import org.samo_lego.japak.PakBuilder;
import org.samo_lego.japak.structs.PakVersion;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class TileListener {
    private static final String OBJECTGROUP_PAK_PATH = "Dungeons/Content/data/lovika/objectgroups/{}/objectgroup.json";
    private static final String LEVEL_PAK_PATH = "Dungeons/Content/data/lovika/levels/{}.json";

    private final String levelName;
    public final ObjectGroup objectGroup;
    private final DungeonLevel levelProperties;

    @Nullable
    private TileCornerBlockEntity cornerEntity;

    public TileListener(String levelName) {
        this.levelName = levelName;
        this.objectGroup = new ObjectGroup(null);
        this.levelProperties = new DungeonLevel();
    }

    public void export(CommandSourceStack executioner, File outputFile) throws IOException, NoSuchAlgorithmException {
        byte[] objectgroupJson = this.objectGroup.generateJson(executioner).getBytes();
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

            if (this.cornerEntity.isMainCorner()) {
                this.objectGroup.objects().add(this.cornerEntity);
            } else {
                this.objectGroup.objects().add(blockEntity);
            }

            this.cornerEntity = null;
        }
    }

    public void onCornerRemoved(TileCornerBlockEntity removed) {
        if (this.cornerEntity == removed) {
            this.cornerEntity = null;
        } else if (this.cornerEntity != null) {
            // We have to create a new matching pair
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
