package me.mmmjjkx.mittellib.utils;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumUtils {
    public static <T extends Enum<T>> T readEnum(@NotNull Class<T> clazz, @NotNull String name) {
        return readEnum(clazz, name, null);
    }

    public static <T extends Enum<T>> T readEnum(@NotNull Class<T> clazz, @NotNull String name, @Nullable T def) {
        Preconditions.checkNotNull(name);

        for (T t : clazz.getEnumConstants()) {
            if (t.toString().equalsIgnoreCase(name)) {
                return t;
            }
        }

        return def;
    }
}
