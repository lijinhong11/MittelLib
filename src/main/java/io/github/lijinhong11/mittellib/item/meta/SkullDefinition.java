package io.github.lijinhong11.mittellib.item.meta;

import com.destroystokyo.paper.profile.PlayerProfile;
import lombok.AllArgsConstructor;
import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteObject;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

@AllArgsConstructor
public class SkullDefinition extends ReadWriteObject {
    private @Nullable PlayerProfile profile;
    private @Nullable NamespacedKey noteBlockSound;

    public static SkullDefinition empty() {
        return new SkullDefinition(null, null);
    }

    public static SkullDefinition fromSkullMeta(SkullMeta meta) {
        return new SkullDefinition(meta.getPlayerProfile(), meta.getNoteBlockSound());
    }

    public SkullDefinition(ConfigurationSection cs) {
        super(cs);
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
                    .severe("Cannot set a skull's skin: url " + url + " is not a valid url");
        }
    }
}
