package org.samo_lego.dungeons_packer.lovika.block_conversion;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;

public record Rule(Map<Property<?>, Comparable<?>> requirements, short result) {

    public int matches(BlockState state) {
        int count = 0;
        for (var entry : this.requirements.entrySet()) {
            Property<?> key = entry.getKey();

            if (!state.hasProperty(key) || !state.getValue(key).equals(entry.getValue())) {
                return -1;
            }
            count++;
        }
        return count;
    }
}
