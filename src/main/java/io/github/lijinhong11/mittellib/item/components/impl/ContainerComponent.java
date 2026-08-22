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
import io.github.lijinhong11.mittellib.item.MittelItem;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

@ItemComponentSpec(key = "container")
public final class ContainerComponent extends ReadWriteItemComponent {
    private final List<ItemStack> contents;

    public ContainerComponent(List<ItemStack> contents) {
        this.contents = contents;
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.CONTAINER;
    }

    public static ContainerComponent fromMinecraftComponent(ItemContainerContents value) {
        return new ContainerComponent(value.contents());
    }

    public static ContainerComponent readFromSection(ConfigurationSection cs) {
        return new ContainerComponent(readItems(cs));
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents));
    }

    @Override
    public void write(ConfigurationSection cs) {
        writeItems(cs, contents);
    }

    static List<ItemStack> readItems(ConfigurationSection cs) {
        List<ItemStack> items = new ArrayList<>();
        for (Object value : cs.getList("items", List.of())) {
            if (value instanceof ConfigurationSection section) {
                items.add(MittelItem.readFromSection(section).get());
            } else if (value instanceof java.util.Map<?, ?> map) {
                YamlConfiguration yaml = new YamlConfiguration();
                items.add(MittelItem.readFromSection(yaml.createSection("item", map)).get());
            }
        }
        return items;
    }

    static void writeItems(ConfigurationSection cs, List<ItemStack> items) {
        cs.set("items", items.stream().map(item -> {
                    YamlConfiguration yaml = new YamlConfiguration();
                    new MittelItem(item).write(yaml);
                    return yaml.getValues(false);
                }).toList());
    }
}
