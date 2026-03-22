package org.samo_lego.dungeons_packer.lovika;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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

    /**
     * Checks whether given {@link BlockPos} is within the bounds, inclusive.
     * @param target target {@link BlockPos}
     * @param min minimum bound {@link BlockPos}
     * @param max maximum bound {@link BlockPos}
     * @return 0 if position is within, -1 if less than min, 1 if greater than max. The dimensions are checked by X, Z, Y.
     */
    public static int isInBounds(BlockPos target, BlockPos min, BlockPos max) {
        if (target.getX() < min.getX() || target.getX() > max.getX()) return target.getX() < min.getX() ? -1 : 1;
        if (target.getZ() < min.getZ() || target.getZ() > max.getZ()) return target.getZ() < min.getZ() ? -1 : 1;
        if (target.getY() < min.getY() || target.getY() > max.getY()) return target.getY() < min.getY() ? -1 : 1;
        return 0;
    }

    public static int compareXZY(Vec3i pos1, Vec3i pos2) {
        if (pos1.getX() == pos2.getX()) {
            return pos1.getZ() != pos2.getZ() ? pos1.getY() - pos2.getY() : pos1.getZ() - pos2.getZ();
        } else {
            return pos1.getX() - pos2.getX();
        }
    }
}
