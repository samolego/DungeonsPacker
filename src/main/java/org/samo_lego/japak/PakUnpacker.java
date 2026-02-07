package org.samo_lego.japak;

import org.samo_lego.japak.structs.PakEntry;
import org.samo_lego.japak.structs.PakFooter;
import org.samo_lego.japak.structs.PakIndex;
import org.samo_lego.japak.structs.PakVersion;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class PakUnpacker {

    private final org.samo_lego.japak.io.PakReader reader;
    private final PakFooter footer;
    private final PakIndex index;
    private final byte[] key;

    public PakUnpacker(String path) throws GeneralSecurityException, IOException {
        this(path, null);
    }

    public PakUnpacker(String path, byte[] key)
        throws IOException, GeneralSecurityException {
        this.reader = new org.samo_lego.japak.io.PakReader(path);
        this.key = key;

        this.footer = new PakFooter();
        this.footer.read(this.reader);

        this.index = new PakIndex();
        this.index.read(this.reader, this.footer, key);
    }

    public List<String> listFiles() {
        return this.index.entries.keySet().stream().toList();
    }

    public PakVersion getVersion() {
        return this.footer.version;
    }

    public byte[] readFile(String path)
        throws IOException, GeneralSecurityException {
        PakEntry entry = this.index.entries.get(path);
        if (entry == null) {
            return null;
        }
        return entry.readData(this.reader, this.footer.version, this.key);
    }
}
