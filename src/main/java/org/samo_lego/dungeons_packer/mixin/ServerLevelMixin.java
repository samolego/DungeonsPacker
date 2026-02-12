package org.samo_lego.dungeons_packer.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.samo_lego.dungeons_packer.lovika.tiles.DungeonsHandler;
import org.samo_lego.dungeons_packer.lovika.tiles.IDungeonsHandlerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements IDungeonsHandlerProvider {
    @Unique
    private DungeonsHandler dungeonsHandler;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void onInit(
            final MinecraftServer server,
            final Executor executor,
            final LevelStorageSource.LevelStorageAccess levelStorage,
            final ServerLevelData levelData,
            final ResourceKey<Level> dimension,
            final LevelStem levelStem,
            final boolean isDebug,
            final long biomeZoomSeed,
            final List<CustomSpawner> customSpawners,
            final boolean tickTime,
            CallbackInfo ci
    ) {
        String levelName = server.getWorldData().getLevelName();
        this.dungeonsHandler = new DungeonsHandler(levelName.replace(" ", "_"));
    }

    @Override
    public DungeonsHandler dungeons_packer$getDungeonsHandler() {
        return this.dungeonsHandler;
    }
}
