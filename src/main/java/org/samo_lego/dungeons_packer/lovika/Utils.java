package org.samo_lego.dungeons_packer.lovika;

import java.util.Base64;
import java.util.zip.Deflater;

public class Utils {
    public static String compressAndEncode(byte[] data) {
        try (Deflater deflater = new Deflater()) {
            deflater.setInput(data);
            deflater.finish();

            byte[] buffer = new byte[data.length + 100];
            int compressedLength = deflater.deflate(buffer);
            deflater.end();

            byte[] output = new byte[compressedLength];
            System.arraycopy(buffer, 0, output, 0, compressedLength);

            return Base64.getEncoder().encodeToString(output);
        }
    }
}
