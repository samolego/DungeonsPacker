package org.samo_lego.dungeons_packer.lovika.resource_pack;

public enum BlockShape {
    INVISIBLE,
    CROSS_TEXTURE,
    WATER,
    TREE,
    LEAVES,
    BED,
    RAIL,
    PISTON,
    BLOCK_HALF,
    TORCH,
    STAIRS,
    CHEST,
    RED_DUST,
    ROWS,  // crops
    DOOR,
    LADDER,
    LEVER,
    CACTUS,
    FENCE,
    REPEATER,
    IRON_FENCE,  // Iron bars
    STEM,
    VINE,
    FENCE_GATE,
    LILYPAD,
    BREWING_STAND,
    CAULDRON,
    PORTAL_FRAME,
    COCOA,
    TRIPWIRE_HOOK,
    TRIPWIRE,
    WALL,
    FLOWER_POT,
    ANVIL,
    COMPARATOR,
    HOPPER,
    SLIME_BLOCK,
    DOUBLE_PLANT_POLY,
    FIRE;

    public String getName() {
        return this.name().toLowerCase();
    }
}
