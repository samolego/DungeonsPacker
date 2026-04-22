package org.samo_lego.dungeons_packer.lovika.resource_pack;

import org.samo_lego.dungeons_packer.lovika.block_conversion.BlockConstants;

import java.util.Iterator;

public record TextureEntry(short blockId, byte blockData, String resourceId) {
    public short getFullId() {
        return (short) (this.blockId << BlockConstants.BLOCK_ID_MASK_SHIFT_COUNT | this.blockData);
    }

    public static TextureEntry fromFullId(short fullId, String resourceId) {
        return new TextureEntry((short) (fullId >> BlockConstants.BLOCK_ID_MASK_SHIFT_COUNT), (byte) (fullId & BlockConstants.BLOCK_DATA_MASK), resourceId);
    }
}
