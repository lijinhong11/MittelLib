package me.mmmjjkx.mittellib.item.meta;

import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkDefinition extends ReadWriteObject {

    public static FireworkDefinition empty() {
        return new FireworkDefinition();
    }

    public static FireworkDefinition fromFireworkMeta(FireworkMeta meta) {
        return new FireworkDefinition();
    }

    @Override
    public void write(ConfigurationSection cs) {
    }

    @Override
    public void read(ConfigurationSection cs) {
    }
}
