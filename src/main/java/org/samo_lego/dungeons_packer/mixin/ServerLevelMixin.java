package org.samo_lego.dungeons_packer.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jspecify.annotations.Nullable;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.tiles.ITileListener;
import org.samo_lego.dungeons_packer.lovika.tiles.TileListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements ITileListener {
    @Unique
    private TileListener tileListener;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void onInit(
            MinecraftServer minecraftServer,
            Executor executor,
            LevelStorageAccess levelStorageAccess,
            ServerLevelData serverLevelData,
            ResourceKey<Level> resourceKey,
            LevelStem levelStem,
            boolean bl,
            long l,
            List<CustomSpawner> list,
            boolean bl2,
            @Nullable RandomSequences randomSequences,
            CallbackInfo ci
    ) {
        String levelName = minecraftServer.getWorldData().getLevelName();
        this.tileListener = new TileListener(levelName.replace(" ", "_"));
    }

    @Override
    public TileListener dungeons_packer$getTileListener() {
        return this.tileListener;
    }
}
