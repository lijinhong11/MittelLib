package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import java.util.function.BiConsumer;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * THIS IS A UNIVERSAL COMPONENT FOR PRIMITIVES and NAMESPACED KEYS
 *
 * @param <T> the type of value
 */
@SuppressWarnings("unchecked")
public final class SimpleItemComponent<T> extends ReadWriteItemComponent {
    @Getter
    private final String key;

    private final T value;
    private final BiConsumer<ItemStack, T> applier;

    @ParametersAreNonnullByDefault
    public SimpleItemComponent(String key, T value, BiConsumer<ItemStack, T> applier) {
        this.key = key;
        this.value = value;
        this.applier = applier;
    }

    @ParametersAreNonnullByDefault
    public static <T> SimpleItemComponent<T> readFromSection(
            String key, ConfigurationSection cs, Class<T> type, BiConsumer<ItemStack, T> applier) {
        T value = cs.getObject(key, type);
        return new SimpleItemComponent<>(key, value, applier);
    }

    @ParametersAreNonnullByDefault
    public static <T> SimpleItemComponent<T> pack(
            String key, Object o, Class<T> type, BiConsumer<ItemStack, T> applier) {
        if (!type.isInstance(o)) {
            throw new IllegalArgumentException("argument type mismatch");
        }
        return new SimpleItemComponent<>(key, (T) o, applier);
    }


    @Override
    public void write(ConfigurationSection cs) {
        if (value instanceof Key k) {
            cs.set(key, k.asString());
        } else {
            cs.set(key, value);
        }
    }

    @Override
    public void applyToItem(ItemStack item) {
        applier.accept(item, value);
    }
}
