package me.mmmjjkx.mittellib.utils;

import com.google.common.base.Preconditions;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumUtils {
    public static @NotNull TriState readTriState(@NotNull String name) {
        return readEnum(TriState.class, name, TriState.NOT_SET);
    }

    public static @Nullable <T extends Enum<T>> T readEnum(@NotNull Class<T> clazz, @NotNull String name) {
        return readEnum(clazz, name, null);
    }

    @Contract("_, _, !null -> !null")
    public static @Nullable <T extends Enum<T>> T readEnum(@NotNull Class<T> clazz, @NotNull String name, @Nullable T def) {
        Preconditions.checkNotNull(name);

        for (T t : clazz.getEnumConstants()) {
            if (t.toString().equalsIgnoreCase(name)) {
                return t;
            }
        }

        return def;
    }
}
