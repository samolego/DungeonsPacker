package org.samo_lego.dungeons_packer.ue4;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class UE4AssetModifier {

    private static final ValueLayout.OfInt INT_LE = ValueLayout.JAVA_INT_UNALIGNED
            .withOrder(ByteOrder.LITTLE_ENDIAN);
    private static final ValueLayout.OfShort SHORT_LE = ValueLayout.JAVA_SHORT_UNALIGNED
            .withOrder(ByteOrder.LITTLE_ENDIAN);

    private static final int[] CRC_TABLE_DEPRECATED = new int[256];
    private static final int[] CRC_TABLE_SB8_0 = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            int c1 = i << 24;
            int c2 = i;
            for (int j = 0; j < 8; j++) {
                c1 = ((c1 & 0x80000000) != 0) ? ((c1 << 1) ^ 0x04C11DB7) : (c1 << 1);
                c2 = ((c2 & 1) != 0) ? ((c2 >>> 1) ^ 0xEDB88320) : (c2 >>> 1);
            }
            CRC_TABLE_DEPRECATED[i] = c1;
            CRC_TABLE_SB8_0[i] = c2;
        }
    }

    private final byte[] data;
    private final MemorySegment segment;
    public boolean verbose = false;

    public UE4AssetModifier(InputStream inputStream) throws IOException {
        if (inputStream == null) throw new IOException("Resource stream is null");
        this.data = inputStream.readAllBytes();
        this.segment = MemorySegment.ofArray(data);
    }

    public byte[] getData() {
        return this.data;
    }

    public int modifySubstring(String oldSub, String newSub) {
        if (oldSub.length() != newSub.length()) {
            throw new IllegalArgumentException("Length mismatch: " + oldSub.length() + " vs " + newSub.length());
        }

        int replacedCount = 0;
        byte[] searchPattern = oldSub.getBytes(StandardCharsets.US_ASCII);
        int pos = 0;

        while (true) {
            int idx = findIndex(searchPattern, pos);
            if (idx < 0) break;

            boolean foundEntry = false;
            // Back-scan for length header
            for (int lookback = idx - 4; lookback >= Math.max(0, idx - 1024); lookback--) {
                // Use our custom Little Endian layout
                int slen = segment.get(INT_LE, lookback);

                if (slen > 1 && slen < 2048) {
                    int stringStart = lookback + 4;
                    int stringEnd = stringStart + slen - 1;

                    if (stringStart <= idx && (idx + searchPattern.length) <= stringEnd) {
                        if (stringEnd < data.length && data[stringEnd] == 0) {
                            String fullName = new String(data, stringStart, slen - 1, StandardCharsets.US_ASCII);

                            if (fullName.contains(oldSub)) {
                                String newName = fullName.replace(oldSub, newSub);
                                System.arraycopy(newName.getBytes(StandardCharsets.US_ASCII), 0, data, stringStart, newName.length());

                                // Recalculate UE4 NameTable hashes
                                short h1 = (short) (strihashDeprecated(newName) & 0xFFFF);
                                short h2 = (short) (strCrc32(newName) & 0xFFFF);

                                int hPos = stringEnd + 1;
                                if (hPos + 4 <= data.length) {
                                    // Write using Little Endian layout
                                    segment.set(SHORT_LE, hPos, h1);
                                    segment.set(SHORT_LE, hPos + 2, h2);

                                    if (verbose) System.out.println("Modified: " + fullName + " -> " + newName);
                                    replacedCount++;
                                    pos = hPos + 4;
                                    foundEntry = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (!foundEntry) pos = idx + 1;
        }
        return replacedCount;
    }

    private int findIndex(byte[] tgt, int start) {
        outer: for (int i = start; i <= data.length - tgt.length; i++) {
            for (int j = 0; j < tgt.length; j++) if (data[i + j] != tgt[j]) continue outer;
            return i;
        }
        return -1;
    }

    private static int strihashDeprecated(String s) {
        int h = 0;
        for (char c : s.toCharArray()) {
            h = ((h >>> 8) & 0x00FFFFFF) ^ CRC_TABLE_DEPRECATED[(h ^ (Character.toUpperCase(c) & 0xFF)) & 0x000000FF];
        }
        return h;
    }

    private static int strCrc32(String s) {
        int crc = 0xFFFFFFFF;
        for (char ch : s.toCharArray()) {
            crc = (crc >>> 8) ^ CRC_TABLE_SB8_0[(crc ^ ch) & 0xFF];
            for (int i = 0; i < 3; i++) {
                crc = (crc >>> 8) ^ CRC_TABLE_SB8_0[crc & 0xFF];
            }
        }
        return ~crc;
    }
}
