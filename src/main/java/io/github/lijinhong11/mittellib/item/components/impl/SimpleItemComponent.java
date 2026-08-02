/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.papermc.paper.datacomponent.item.Enchantable;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
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
        switch (value) {
            case Key k -> cs.set(key, k.asString());
            case Enum<?> enumValue -> cs.set(key, enumValue.name());
            case Enchantable en -> cs.set(key, en.value());
            case JukeboxPlayable jp -> cs.set(key, jp.jukeboxSong().key().asString());
            default -> cs.set(key, value);
        }
    }

    @Override
    public void applyToItem(ItemStack item) {
        applier.accept(item, value);
    }
}
