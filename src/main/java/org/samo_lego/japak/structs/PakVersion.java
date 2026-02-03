package org.samo_lego.japak.structs;

public enum PakVersion {
    INVALID(0),
    V1(1),
    V2(2),
    V3(3),
    V4(4),
    V5(5),
    V6(6),
    V7(7);
//    V8A(8),
//    V8B(8);


    public final int value;

    PakVersion(int value) {
        this.value = value;
    }

    public static PakVersion fromLegacy(int value) {
        for (PakVersion version : values()) {
            if (version.value == value) {
                return version;
            }
        }
        return INVALID;
    }

//    public static PakVersion from(int value) {
//        if (value == 8) {
//            return V8B;
//        }
//        return fromLegacy(value);
//    }
}
