package io.github.lijinhong11.mittellib.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtils {
    public static int asUnsigned(int i) {
        i = i < 0 ? -i : i;
        return i;
    }

    public static long asUnsigned(long l) {
        l = l < 0 ? -l : l;
        return l;
    }

    public static double asUnsigned(double d) {
        d = d < 0 ? -d : d;
        return d;
    }

    public static float asUnsigned(float f) {
        f = f < 0 ? -f : f;
        return f;
    }
}
