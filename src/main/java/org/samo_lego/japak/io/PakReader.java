package org.samo_lego.japak.io;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class PakReader implements Closeable {

    private final RandomAccessFile raf;
    private final ByteArrayInputStream bais;
    private final long size;

    public PakReader(String path) throws IOException {
        this.raf = new RandomAccessFile(path, "r");
        this.bais = null;
        this.size = this.raf.length();
    }

    public PakReader(InputStream stream) throws IOException {
        this.raf = null;
        this.bais = new ByteArrayInputStream(stream.readAllBytes());
        this.size = this.bais.available();
    }

    public long getPos() throws IOException {
        if (this.raf != null) {
            return this.raf.getFilePointer();
        } else {
            return this.size - this.bais.available();
        }
    }

    public void setPos(long pos) throws IOException {
        if (this.raf != null) {
            this.raf.seek(pos);
        } else {
            this.bais.reset();
            this.bais.skip(pos);
        }
    }

    public long getSize() {
        return this.size;
    }

    public byte[] read(int size) throws IOException {
        byte[] bytes = new byte[size];
        if (this.raf != null) {
            this.raf.readFully(bytes);
        } else {
            int read = this.bais.read(bytes);
            if (read < size) {
                throw new IOException(
                    "Cannot read " +
                        size +
                        " textureId2bytes, only " +
                        read +
                        " available."
                );
            }
        }
        return bytes;
    }

    public byte readByte() throws IOException {
        if (this.raf != null) {
            return this.raf.readByte();
        } else {
            return (byte) this.bais.read();
        }
    }

    public short readShort() throws IOException {
        byte[] bytes = this.read(2);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public int readInt() throws IOException {
        byte[] bytes = this.read(4);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public long readLong() throws IOException {
        byte[] bytes = this.read(8);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] bytes = new byte[length];
        if (this.raf != null) {
            this.raf.readFully(bytes);
        } else {
            int read = this.bais.read(bytes);
            if (read < length) {
                throw new IOException(
                    "Cannot read " +
                        length +
                        " textureId2bytes, only " +
                        read +
                        " available."
                );
            }
        }
        return bytes;
    }

    public String readString(int length) throws IOException {
        if (length == 0) return "";

        boolean isUtf16 = length < 0;
        int size = isUtf16 ? -length * 2 : length;
        byte[] bytes = this.readBytes(size);

        String str = isUtf16
                ? new String(bytes, StandardCharsets.UTF_16LE)
                : new String(bytes, StandardCharsets.US_ASCII);

        int nullIdx = str.indexOf('\0');
        return (nullIdx == -1) ? str : str.substring(0, nullIdx);
    }


    @Override
    public void close() throws IOException {
        if (this.raf != null) this.raf.close();
        if (this.bais != null) this.bais.close();
    }
}
