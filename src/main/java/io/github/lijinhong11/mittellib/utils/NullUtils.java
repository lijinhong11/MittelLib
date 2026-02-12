package io.github.lijinhong11.mittellib.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class NullUtils {
    @SafeVarargs
    @Nullable
    public static <U extends T, T> T findAnyNonNull(U... objects) {
        for (U u : objects) {
            if (u != null) {
                return u;
            }
        }

        return null;
    }
}
