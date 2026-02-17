package org.samo_lego.dungeons_packer.lovika;

import net.minecraft.world.phys.shapes.VoxelShape;

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

    /**
     * Checks if 2 voxelshapes are the same.
     * Taken from <a href="https://github.com/TheEpicBlock/PolyMc/blob/a3eaae6a56522a830b6e9a244e2bade0431a8c59/src/main/java/io/github/theepicblock/polymc/impl/Util.java#L190">PolyMc</a>
     */
    public static boolean areEqual(VoxelShape a, VoxelShape b) {
        if (a == b || a.isEmpty() && b.isEmpty()) {
            return true;
        }
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        return a.bounds().equals(b.bounds());
    }
}
