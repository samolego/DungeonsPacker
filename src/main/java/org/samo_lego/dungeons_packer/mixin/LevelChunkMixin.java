package org.samo_lego.dungeons_packer.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.samo_lego.dungeons_packer.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.lovika.tiles.ITileListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.image.*;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @Shadow
    @Final
    private Level level;

    @Inject(
            method = "setBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
            at = @At("RETURN")
    )
    public void setBlockEntity(final BlockEntity blockEntity, CallbackInfo _ci) {
        if (this.level instanceof ServerLevel && blockEntity instanceof TileCornerBlockEntity tbe) {
            //((ITileListener) this.level).dungeons_packer$getTileListener().onCornerPlaced(tbe);
        }
    }
}
