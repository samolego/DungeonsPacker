package org.samo_lego.japak.structs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.samo_lego.japak.io.PakReader;
import org.samo_lego.japak.io.PakWriter;

public class PakIndex {

    public String mountPoint;
    public final Map<String, PakEntry> entries = new HashMap<>();

    public void read(PakReader reader, PakFooter footer, byte[] key)
        throws IOException, GeneralSecurityException {
        reader.setPos(footer.indexOffset);
        byte[] indexBytes = reader.readBytes((int) footer.indexSize);

        if (footer.encrypted) {
            if (key == null || key.length != 32) {
                throw new IllegalArgumentException(
                    "A 32-byte AES key is required for encrypted paks."
                );
            }
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            indexBytes = cipher.doFinal(indexBytes);
        }

        try (
            PakReader indexReader = new PakReader(
                new ByteArrayInputStream(indexBytes)
            )
        ) {
            this.mountPoint = indexReader.readString(indexReader.readInt());
            int entryCount = indexReader.readInt();

            for (int i = 0; i < entryCount; i++) {
                int pathLen = indexReader.readInt();
                String path = indexReader.readString(pathLen);

                PakEntry entry = new PakEntry();
                entry.read(indexReader, footer.version);
                this.entries.put(path, entry);
            }
        }
    }

    /**
     * Writes the pak index to the file.
     * @param writer the writer to use.
     * @param version the pak version to adhere to.
     * @param entries the entries to write.
     * @throws IOException if an I/O error occurs.
     */
    public void write(
        PakWriter writer,
        PakVersion version,
        Map<String, PakEntry> entries
    ) throws IOException {
        writer.writeString("../../../"); // Mount point
        writer.writeInt(entries.size());

        for (Map.Entry<String, PakEntry> mapEntry : entries.entrySet()) {
            writer.writeString(mapEntry.getKey());
            mapEntry.getValue().write(writer, version);
        }
    }
}
