package org.samo_lego.dungeons_packer.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import org.samo_lego.dungeons_packer.lovika.Door;
import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockMap;
import org.samo_lego.dungeons_packer.lovika.block_conversion.IDungeonsConvertable;
import org.samo_lego.dungeons_packer.lovika.region.Region;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;

@Mixin(StructureBlock.class)
public class StructureBlockMixin implements IDungeonsConvertable {
    @Override
    public short dungeons_packer$convertToDungeons(Level level, BlockPos absolutePos, MutableBlockPos localPos, ArrayList<Door> doors, ArrayList<Region> regions) {
        var blockEntity = level.getBlockEntity(absolutePos);
        if (blockEntity instanceof StructureBlockEntity sbe) {
            var structName = sbe.getStructureName();
            var type = structName.split(":");
            var name = type.length > 1 ? type[1] : "";

            var pos = sbe.getStructurePos();
            var size = sbe.getStructureSize();
            
            if ("door".equals(type[0])) {
                doors.add(new Door(pos, size, name));
            } else if ("region".equals(type[0])) {
                var json = GsonHelper.parse(sbe.getMetaData());
                var tagsJson = json.get("tags");
                var tags = tagsJson == null ? "" : tagsJson.getAsString();

                var typeJson = json.get("type");
                var typeStr = typeJson == null ? "" : typeJson.getAsString();
                var regionType = Region.Type.parse(typeStr);

                regions.add(new Region(pos, size, name, tags, regionType));
            }
        }

        return BlockMap.DUNGEONS_AIR;
    }
}
