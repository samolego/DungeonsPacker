package org.samo_lego.dungeons_packer.mixin.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SlabBlock.class)
public interface SlabBlockAccessor {
    @Accessor("SHAPE_BOTTOM")
    static VoxelShape SHAPE_BOTTOM() {
        throw new UnsupportedOperationException();
    }
}
