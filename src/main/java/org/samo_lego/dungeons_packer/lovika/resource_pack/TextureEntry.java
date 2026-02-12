package org.samo_lego.dungeons_packer.lovika.resource_pack;

import java.util.Iterator;

public record TextureEntry(short blockId, byte blockData, String resourceId) implements Iterator<TextureEntry> {
    public short getFullId() {
        return (short) (this.blockId << 4 | this.blockData);
    }

    @Override
    public boolean hasNext() {
        return this.blockData < 0b1111;
    }

    @Override
    public TextureEntry next() {
        return new TextureEntry(this.blockId, (byte) (this.blockData + 1), this.resourceId);
    }
}
