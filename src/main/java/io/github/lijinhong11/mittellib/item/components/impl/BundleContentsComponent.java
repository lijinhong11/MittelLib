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
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

@ItemComponentSpec(key = "bundleContents")
public final class BundleContentsComponent extends ReadWriteItemComponent {
    private final List<ItemStack> contents;

    public BundleContentsComponent(List<ItemStack> contents) {
        this.contents = contents;
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.BUNDLE_CONTENTS;
    }

    public static BundleContentsComponent fromMinecraftComponent(BundleContents value) {
        return new BundleContentsComponent(value.contents());
    }

    public static BundleContentsComponent readFromSection(ConfigurationSection cs) {
        return new BundleContentsComponent(ContainerComponent.readItems(cs));
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.BUNDLE_CONTENTS, BundleContents.bundleContents(contents));
    }

    @Override
    public void write(ConfigurationSection cs) {
        ContainerComponent.writeItems(cs, contents);
    }
}
