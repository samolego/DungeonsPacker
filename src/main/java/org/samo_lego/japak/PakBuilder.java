package org.samo_lego.japak;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.samo_lego.japak.io.PakWriter;
import org.samo_lego.japak.structs.CompressionMethod;
import org.samo_lego.japak.structs.PakEntry;
import org.samo_lego.japak.structs.PakFooter;
import org.samo_lego.japak.structs.PakIndex;
import org.samo_lego.japak.structs.PakVersion;

/**
 * A high-level utility class for building Unreal Engine 4 .pak files.
 * This class simplifies the process of adding files and writing the final pak
 * structure.
 */
public class PakBuilder {

    private final PakWriter writer;
    private final Map<String, PakEntry> entries = new HashMap<>();
    private final PakVersion version;

    /**
     * Creates a new PakBuilder.
     * @param outputFile the file to write the .pak to. The file will be deleted if it already exists.
     * @param version the PakVersion to use for the .pak file.
     * @throws IOException if an I/O error occurs.
     */
    public PakBuilder(File outputFile, PakVersion version) throws IOException {
        this.writer = new PakWriter(outputFile);
        this.version = version;
    }

    /**
     * Adds a file to the .pak archive.
     * For now, we will use uncompressed and unencrypted data as recommended.
     *
     * @param internalPath The path of the file inside the archive (e.g., "Dungeons/Content/data/test.json")
     * @param data The raw byte data of the file to add.
     * @throws IOException if an I/O error occurs.
     */
    public void addFile(String internalPath, byte[] data) throws IOException, NoSuchAlgorithmException {
        PakEntry entry = new PakEntry();
        entry.offset = this.writer.getPos();
        entry.size = data.length;
        entry.compressedSize = data.length;
        entry.compressionMethod = CompressionMethod.NONE;
        entry.isEncrypted = false;

        // Calculate SHA-1 hash of the file data
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        entry.hash = md.digest(data);

        // 1. Write the Entry Header
        entry.write(this.writer, this.version);

        // 2. Write the actual file data
        this.writer.write(data);

        this.entries.put(internalPath, entry);
    }


    /**
     * Finalizes the .pak file by writing the index and the footer.
     * @throws IOException if an I/O error occurs.
     * @throws NoSuchAlgorithmException if SHA-1 algorithm is not available.
     */
    public void finish() throws IOException, NoSuchAlgorithmException {
        long indexOffset = this.writer.getPos();

        // 3. Write the Index to a temporary byte array to calculate its hash and size
        ByteArrayOutputStream indexBytesStream = new ByteArrayOutputStream();
        try (PakWriter indexWriter = new PakWriter(indexBytesStream)) {
            PakIndex pakIndex = new PakIndex();
            pakIndex.write(indexWriter, this.version, this.entries);
        }

        byte[] indexBytes = indexBytesStream.toByteArray();
        long indexSize = indexBytes.length;

        // Calculate index hash
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] indexHash = digest.digest(indexBytes);

        // Now write the index to the actual file
        this.writer.write(indexBytes);

        // 4. Write the Footer
        PakFooter footer = new PakFooter();
        footer.version = this.version;
        footer.indexOffset = indexOffset;
        footer.indexSize = indexSize;
        footer.hash = indexHash;
        footer.encrypted = false;

        footer.write(this.writer);

        this.writer.close();
    }
}
