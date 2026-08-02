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
package io.github.lijinhong11.mittellib.item.meta;

import io.github.lijinhong11.mittellib.configuration.ReadWriteObject;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.EnumUtils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
public class BannerDefinition implements ReadWriteObject {
    private static final Registry<PatternType> REGISTRY =
            RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);

    private List<Pattern> bannerPatterns;

    public static BannerDefinition empty() {
        return new BannerDefinition(new ArrayList<>());
    }

    public static BannerDefinition fromBannerMeta(BannerMeta bannerMeta) {
        return new BannerDefinition(bannerMeta.getPatterns());
    }

    public BannerDefinition(ConfigurationSection cs) {
        read(cs);
    }

    @NotNull public Pattern getPattern(int index) {
        return bannerPatterns.get(index);
    }

    public void addPattern(Pattern pattern) {
        bannerPatterns.add(pattern);
    }

    public void setPattern(int index, Pattern pattern) {
        bannerPatterns.set(index, pattern);
    }

    public void removePattern(int index) {
        bannerPatterns.remove(index);
    }

    public int numberOfPatterns() {
        return bannerPatterns.size();
    }

    @Override
    public void write(ConfigurationSection cs) {
        List<String> stringPatterns = new ArrayList<>();
        for (Pattern pattern : bannerPatterns) {
            stringPatterns.add(pattern.getColor() + ";" + REGISTRY.getKey(pattern.getPattern()));
        }

        cs.set("patterns", stringPatterns);
    }

    @Override
    public void read(ConfigurationSection cs) {
        List<String> stringPatterns = cs.getStringList("patterns");
        if (stringPatterns.isEmpty()) {
            return;
        }

        bannerPatterns.clear();
        for (String s : stringPatterns) {
            String[] parts = s.split(";", 2);
            if (parts.length != 2) {
                continue;
            }

            DyeColor color = EnumUtils.readEnum(DyeColor.class, parts[0].trim());
            NamespacedKey key = BukkitUtils.getNamespacedKey(parts[1].trim());
            if (color == null || key == null) {
                continue;
            }

            PatternType type = REGISTRY.get(key);
            if (type == null) {
                continue;
            }

            bannerPatterns.add(new Pattern(color, type));
        }
    }

    public void applyTo(BannerMeta meta) {
        meta.setPatterns(bannerPatterns);
    }
}
