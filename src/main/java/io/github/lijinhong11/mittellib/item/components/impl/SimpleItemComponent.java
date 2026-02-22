package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

/**
 * THIS IS A UNIVERSAL COMPONENT FOR PRIMITIVES
 *
 * @param <T> the type of value
 */
public final class SimpleItemComponent<T> extends ReadWriteItemComponent {
    @Getter
    private final String key;
    private final T value;
    private final BiConsumer<ItemStack, T> applier;

    @ParametersAreNonnullByDefault
    public SimpleItemComponent(
            String key,
            T value,
            BiConsumer<ItemStack, T> applier
    ) {
        this.key = key;
        this.value = value;
        this.applier = applier;
    }

    @ParametersAreNonnullByDefault
    public static <T> SimpleItemComponent<T> readFromSection(
            String key,
            ConfigurationSection cs,
            Class<T> type,
            BiConsumer<ItemStack, T> applier
    ) {
        T value = cs.getObject(key, type);
        return new SimpleItemComponent<>(key, value, applier);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set(key, value);
    }

    @Override
    public void applyToItem(ItemStack item) {
        applier.accept(item, value);
    }
}
