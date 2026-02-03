package org.samo_lego.japak.structs;

public enum CompressionMethod {
    NONE,   // 0
    ZLIB,   // 1
    GZIP,   // 2
    LZ4,    // 3
    OODLE,  // 4
    UNKNOWN;


    public int getValue() {
        return this.ordinal();
    }

   public static CompressionMethod fromInt(int i) {
        if (i >= 0 && i < values().length) {
            return values()[i];
        }
        return UNKNOWN;
    }
}
