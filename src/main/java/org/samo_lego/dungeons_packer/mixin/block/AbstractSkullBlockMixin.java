package org.samo_lego.dungeons_packer.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.SkullBlock.Types;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.lovika.block_conversion.IDungeonsConvertable;
import org.samo_lego.dungeons_packer.lovika.region.Region;
import org.samo_lego.dungeons_packer.lovika.region.Region.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

@Mixin(AbstractSkullBlock.class)
public class AbstractSkullBlockMixin implements IDungeonsConvertable {
    @Unique
    private static final String PLAYER_START = "playerstart";

    @Shadow
    private SkullBlock.Type type;

    @Override
    public short dungeons_packer$convertToDungeons(Level level, BlockPos currentPos, BlockPos relativePos, ArrayList<Door> doors, ArrayList<Region> regions) {
        // We mixined into abstract skull to catch both wall skull and ground
        // placed skull block.
        if (this.type == Types.PLAYER) {
            // Register as a start point
            var start = new Region(relativePos, new Vec3i(1, 1, 1), PLAYER_START, PLAYER_START, Type.TRIGGER);
            regions.add(start);
        }
        return BlockMap.toDungeonBlockId(level.getBlockState(currentPos)).orElse(BlockMap.DUNGEONS_AIR);
    }
}
