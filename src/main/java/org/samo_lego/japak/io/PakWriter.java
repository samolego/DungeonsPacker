package org.samo_lego.japak.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class PakWriter implements Closeable {

    private final RandomAccessFile raf;
    private final OutputStream os;
    private long pos = 0;

    public PakWriter(File file) throws IOException {
        this.raf = new RandomAccessFile(file, "rw");
        this.raf.setLength(0);
        this.os = null;
    }

    public PakWriter(ByteArrayOutputStream os) {
        this.raf = null;
        this.os = os;
    }

    public long getPos() throws IOException {
        return this.raf != null ? this.raf.getFilePointer() : this.pos;
    }

    public void setPos(long pos) throws IOException {
        if (this.raf != null) this.raf.seek(pos);
    }

    public void write(byte[] bytes) throws IOException {
        if (this.raf != null) {
            this.raf.write(bytes);
        } else {
            this.os.write(bytes);
            this.pos += bytes.length;
        }
    }

    public void writeByte(byte b) throws IOException {
        if (this.raf != null) {
            this.raf.write(b);
        } else {
            this.os.write(b);
            this.pos++;
        }
    }

    public void writeShort(short value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2).order(
            ByteOrder.LITTLE_ENDIAN
        );
        buffer.putShort(value);
        this.write(buffer.array());
    }

    public void writeInt(int value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(
            ByteOrder.LITTLE_ENDIAN
        );
        buffer.putInt(value);
        this.write(buffer.array());
    }

    public void writeLong(long value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(
            ByteOrder.LITTLE_ENDIAN
        );
        buffer.putLong(value);
        this.write(buffer.array());
    }

    public void writeString(String value) throws IOException {
        if (value.isEmpty()) {
            this.writeInt(0);
            return;
        }

        // Check if the string contains non-ASCII characters
        boolean isAscii = StandardCharsets.US_ASCII.newEncoder().canEncode(value);

        if (isAscii) {
            byte[] bytes = (value + "\0").getBytes(StandardCharsets.US_ASCII);
            this.writeInt(bytes.length);
            this.write(bytes);
        } else {
            byte[] bytes = (value + "\0").getBytes(StandardCharsets.UTF_16LE);
            this.writeInt(-bytes.length / 2);
            this.write(bytes);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.raf != null) this.raf.close();
        if (this.os != null) this.os.close();
    }
}
