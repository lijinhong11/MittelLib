package me.mmmjjkx.mittellib.item;

import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import me.mmmjjkx.mittellib.item.meta.BannerDefinition;
import me.mmmjjkx.mittellib.item.meta.FireworkDefinition;
import me.mmmjjkx.mittellib.item.meta.SkullDefinition;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class MittelItemMeta extends ReadWriteObject {
    private @Nullable BannerDefinition banner;
    private @Nullable SkullDefinition skull;
    private @Nullable FireworkDefinition firework;
    private @Nullable Color leatherArmorColor;

    public static MittelItemMeta empty() {
        return new MittelItemMeta();
    }

    private MittelItemMeta() {
    }

    @Override
    public void write(ConfigurationSection cs) {
    }

    @Override
    public void read(ConfigurationSection cs) {
    }
}
