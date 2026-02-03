package org.samo_lego.japak;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;
import org.samo_lego.japak.io.PakReader;
import org.samo_lego.japak.structs.PakEntry;
import org.samo_lego.japak.structs.PakFooter;
import org.samo_lego.japak.structs.PakIndex;
import org.samo_lego.japak.structs.PakVersion;

public class Japak {

    public Japak(String[] args) throws IOException, GeneralSecurityException {
        if (args.length < 1) {
            System.out.println("Usage: java -jar japak.jar <pack/unpack> <path_to_pak_file> [encryption_key]");
            return;
        }

        String command = args[0];
        String pakFilePath = args[1];
        byte[] key = null;
        if (args.length > 2) {
            key = Base64.getDecoder().decode(args[2]);
        }

        PakReader reader = new PakReader(pakFilePath);
        PakFooter footer = new PakFooter();
        footer.read(reader);

        if (footer.version == PakVersion.INVALID) {
            System.out.println("Invalid pak file or unsupported version.");
            return;
        }

        if (command.equals("pack")) {
            new PakBuilder(new File(pakFilePath), PakVersion.V3).finish();
        } else if (command.equals("unpack")) {
            var unpacker = new PakUnpacker(pakFilePath, key);
            List<String> files = unpacker.listFiles();
            for (String file : files) {
                var f = new File(file);
                f.getParentFile().mkdirs();
                try (var out = new java.io.FileOutputStream(f)) {
                    out.write(unpacker.readFile(file));
                }
            }
        }
    }
}
