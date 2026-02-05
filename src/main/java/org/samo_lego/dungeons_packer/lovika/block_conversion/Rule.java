package org.samo_lego.dungeons_packer.lovika.block_conversion;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;

public class Rule {
    private final Map<Property<?>, Comparable<?>> requirements;
    private final short result;
    private boolean wasTriggered = false; // Our tracker

    public Rule(Map<Property<?>, Comparable<?>> requirements, short result) {
        this.requirements = requirements;
        this.result = result;
    }

    public int matches(BlockState state) {
        int count = 0;
        for (var entry : requirements.entrySet()) {
            Property<?> key = entry.getKey();

            if (!state.hasProperty(key) || !state.getValue(key).equals(entry.getValue())) {
                return -1;
            }
            count++;
        }
        return count;
    }

    public void markUsed() { this.wasTriggered = true; }
    public boolean wasTriggered() { return this.wasTriggered; }
    public short getResult() { return result; }
    public Map<Property<?>, Comparable<?>> getRequirements() { return requirements; }
}
