package org.samo_lego.japak.structs;

import java.io.IOException;
import org.samo_lego.japak.io.PakReader;
import org.samo_lego.japak.io.PakWriter;

public class PakFooter {

    public static final int PAK_MAGIC = 0x5A6F12E1;

    public PakVersion version;
    public long indexOffset;
    public long indexSize;
    public byte[] hash; // SHA1 of the Index
    public boolean encrypted;
    public byte[] encryptionKeyGuid;
    public boolean indexIsFrozen;
    public CompressionMethod[] compressionMethods;

    public PakFooter() {
        this.encryptionKeyGuid = new byte[20];
        this.hash = new byte[20];
        this.compressionMethods = new CompressionMethod[0];
    }

    /**
     * Reads the footer by scanning the end of the file for the Magic Number.
     */
    public void read(PakReader reader) throws IOException {
        long size = reader.getSize();
        long footerOffset = -1;

        // Check 204-byte footer (V8B)
        /*if (size >= 204) {
            reader.setPos(size - 204);
            if (reader.readInt() == PAK_MAGIC) {
                footerOffset = size - 204;
                this.version = PakVersion.from(reader.readInt());
            }
        }
        // Check 172-byte footer (V8A)
        if (footerOffset == -1 && size >= 172) {
            reader.setPos(size - 172);
            if (reader.readInt() == PAK_MAGIC) {
                footerOffset = size - 172;
                this.version = PakVersion.V8A;
            }
        }*/
        // Check 44-byte footer (Legacy V1-V7)
        if (size >= 44) {
            reader.setPos(size - 44);
            if (reader.readInt() == PAK_MAGIC) {
                footerOffset = size - 44;
                this.version = PakVersion.fromLegacy(reader.readInt());
            }
        }

        if (footerOffset == -1 || this.version == PakVersion.INVALID) {
            throw new IOException("Invalid pak file or unsupported version. Magic not found.");
        }

        reader.setPos(footerOffset + 8);
        this.indexOffset = reader.readLong();
        this.indexSize = reader.readLong();
        this.hash = reader.readBytes(20);

        // If it's a newer version, we can also go back and grab the GUID and encryption flag
        /*if (this.version.value >= PakVersion.V8A.value) {
            long footerStart = (this.version == PakVersion.V8B) ? size - 204 : size - 172;
            reader.setPos(footerStart);
            this.encryptionKeyGuid = reader.readBytes(20);
            this.encrypted = reader.readByte() == 1;
        }*/
    }

    /**
     * Writes a V8B (Version 9) Footer.
     * Use this for your Minecraft Dungeons Exporter.
     */
    public void write(PakWriter writer) throws IOException {
        //if (this.version.value < PakVersion.V8A.value) {
        // Legacy (44 bytes)
        writer.writeInt(PAK_MAGIC);
        writer.writeInt(this.version.value);
        writer.writeLong(this.indexOffset);
        writer.writeLong(this.indexSize);
        writer.write(this.hash);
        /*} else if (this.version == PakVersion.V8A) {
            // V8A (172 bytes) - Magic at Start
            writer.writeInt(PAK_MAGIC);
            writer.writeInt(8); // Internal version
            writer.writeLong(this.indexOffset);
            writer.writeLong(this.indexSize);
            writer.write(this.hash);
            writer.writeByte((byte) (this.encrypted ? 1 : 0));
            writer.write(this.encryptionKeyGuid);
            writer.write(new byte[107]); // Padding to reach 172
        } else {
            // V8B (204 bytes) - GUID at Start
            writer.write(this.encryptionKeyGuid); // 20
            writer.writeByte((byte) (this.encrypted ? 1 : 0)); // 1
            writer.writeInt(PAK_MAGIC); // 4
            writer.writeInt(8); // Internal version (writes 8 even for V8B)
            writer.writeLong(this.indexOffset);
            writer.writeLong(this.indexSize);
            writer.write(this.hash);
            writer.write(new byte[139]); // Padding to reach 204
        }*/
    }
}
