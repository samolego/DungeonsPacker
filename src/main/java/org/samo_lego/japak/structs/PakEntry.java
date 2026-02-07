package org.samo_lego.japak.structs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.samo_lego.japak.io.PakReader;
import org.samo_lego.japak.io.PakWriter;

public class PakEntry {

    public long offset;
    public long size;
    public long compressedSize;
    public CompressionMethod compressionMethod;
    public byte[] hash;
    public PakBlock[] compressionBlocks;
    public boolean isEncrypted;
    public int compressionBlockSize;

    public PakEntry() {
        this.compressionBlocks = new PakBlock[0];
        this.hash = new byte[20];
        this.compressionBlockSize = 0;
        this.isEncrypted = false;
        this.compressionMethod = CompressionMethod.NONE;
        this.offset = 0;
        this.size = 0;
        this.compressedSize = 0;
    }

    public void read(PakReader reader, PakVersion version) throws IOException {
        this.offset = reader.readLong();
        this.compressedSize = reader.readLong();
        this.size = reader.readLong();

        // This might be a bug but haven't deeply investigated yet
        //if (version >= PakVersion.V8A) {
        //    this.compressionMethod = CompressionMethod.fromInt(reader.readByte());
        //} else {
        this.compressionMethod = CompressionMethod.fromInt(reader.readInt());
        //}

        // Version 1 had a timestamp. Newer ones don't.
        if (version == PakVersion.V1) {
            reader.readLong();
        }

        this.hash = reader.readBytes(20);

        if (version.value >= PakVersion.V3.value) {
            if (this.compressionMethod != CompressionMethod.NONE) {
                int blockCount = reader.readInt();
                this.compressionBlocks = new PakBlock[blockCount];
                for (int i = 0; i < blockCount; i++) {
                    this.compressionBlocks[i] = new PakBlock(
                            reader.readLong(),
                            reader.readLong()
                    );
                }
            }
            this.isEncrypted = reader.readByte() == 1;
            this.compressionBlockSize = reader.readInt();
        }


        if (version == PakVersion.V4) {
            reader.readInt(); // "Unknown" field
        }
    }



    /**
     * Writes this entry's metadata to the pak file.
     * This is the "header" that is written right before the file's data.
     * @param writer the writer to use.
     * @param version the pak version to adhere to.
     * @throws IOException if an I/O error occurs.
     */
    public void write(PakWriter writer, PakVersion version) throws IOException {
        writer.writeLong(this.offset);
        writer.writeLong(this.compressedSize);
        writer.writeLong(this.size);

        // MUST match the read logic exactly:
        // Only V8A uses a Byte, others use an Int.
        //if (version == PakVersion.V8A) {
        //    writer.writeByte((byte) this.compressionMethod.getValue());
        //} else {
            writer.writeInt(this.compressionMethod.getValue());
        //}

        if (version == PakVersion.V1) {
            writer.writeLong(System.currentTimeMillis());
        }

        writer.write(this.hash);

        if (version.value >= PakVersion.V3.value) {
            if (this.compressionMethod != CompressionMethod.NONE) {
                writer.writeInt(this.compressionBlocks.length);
                for (PakBlock block : this.compressionBlocks) {
                    writer.writeLong(block.start());
                    writer.writeLong(block.end());
                }
            }
            writer.writeByte((byte) (this.isEncrypted ? 1 : 0));
            writer.writeInt(this.compressionBlockSize);
        }

        if (version == PakVersion.V4) {
            writer.writeInt(0); // Unknown field
        }
    }

    public byte[] readData(PakReader reader, PakVersion version, byte[] key)
        throws IOException, GeneralSecurityException {

        if (this.compressionMethod == CompressionMethod.NONE) {
            reader.setPos(this.offset);
            new PakEntry().read(reader, version); // Skip header
            return reader.readBytes((int) this.size);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int blockSize = this.compressionBlockSize == 0 ? 0x10000 : this.compressionBlockSize;

        for (PakBlock block : this.compressionBlocks) {
            // Handle Relative vs Absolute offsets
            // If block start is smaller than the entry offset, it's relative to the entry start
            long absoluteStart = (block.start() < this.offset) ? (this.offset + block.start()) : block.start();

            reader.setPos(absoluteStart);
            int compressedBlockSize = (int) (block.end() - block.start());
            byte[] blockData = reader.readBytes(compressedBlockSize);

            if (this.isEncrypted) {
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
                blockData = cipher.doFinal(blockData);
            }

            if (this.compressionMethod == CompressionMethod.ZLIB) {
                Inflater inflater = new Inflater();
                inflater.setInput(blockData, 0, compressedBlockSize);

                byte[] buffer = new byte[blockSize];
                try {
                    while (!inflater.finished()) {
                        int bytesRead = inflater.inflate(buffer);
                        if (bytesRead == 0) break;

                        int remaining = (int) this.size - baos.size();
                        if (remaining <= 0) break;
                        baos.write(buffer, 0, Math.min(bytesRead, remaining));
                    }
                } catch (DataFormatException e) {
                    throw new IOException("Decompression failed", e);
                } finally {
                    inflater.end();
                }
            }
        }
        return baos.toByteArray();
    }
}
