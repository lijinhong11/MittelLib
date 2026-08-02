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

import com.destroystokyo.paper.profile.PlayerProfile;
import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteObject;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public class SkullDefinition implements ReadWriteObject {
    private @Nullable PlayerProfile profile;
    private @Nullable NamespacedKey noteBlockSound;

    public static SkullDefinition empty() {
        return new SkullDefinition(null, null);
    }

    public static SkullDefinition fromSkullMeta(SkullMeta meta) {
        return new SkullDefinition(meta.getPlayerProfile(), meta.getNoteBlockSound());
    }

    public SkullDefinition(ConfigurationSection cs) {
        read(cs);
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (profile != null) {
            PlayerTextures textures = profile.getTextures();
            if (textures.getSkin() != null) {
                cs.set("url", textures.getSkin().toString());
            }
        }

        if (noteBlockSound != null) {
            cs.set("noteBlockSound", noteBlockSound.asString());
        }
    }

    @Override
    public void read(ConfigurationSection cs) {
        String url = cs.getString("url", "null");

        try {
            URI uri = URI.create(url);
            PlayerProfile p = Bukkit.createProfile("MITTEL_LIB_LOL");
            PlayerTextures pt = p.getTextures();
            pt.setSkin(uri.toURL());
            p.setTextures(pt);

            profile = p;

            if (cs.contains("noteBlockSound")) {
                NamespacedKey key = BukkitUtils.getNamespacedKey(cs.getString("noteBlockSound"));
                if (key != null) {
                    noteBlockSound = key;
                }
            }
        } catch (Exception e) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Failed to set a skull's skin: url " + url + " is not a valid url");
        }
    }
}
