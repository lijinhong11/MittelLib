package io.github.lijinhong11.mittellib.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    @Nullable
    public static <U extends T, T> T findAnyNonNull(Iterable<U> objects) {
        for (U u : objects) {
            if (u != null) {
                return u;
            }
        }

        return null;
    }

    @SafeVarargs
    @Nullable
    public static <U extends T, T> List<T> findAllNonNull(U... objects) {
        List<T> list = new ArrayList<>();

        for (U u : objects) {
            if (u != null) {
                list.add(u);
            }
        }

        return list.isEmpty() ? null : list;
    }

    @Nullable
    public static <U extends T, T> List<T> findAllNonNull(Iterable<U> objects) {
        List<T> list = new ArrayList<>();

        for (U u : objects) {
            if (u != null) {
                list.add(u);
            }
        }

        return list.isEmpty() ? null : list;
    }
}
