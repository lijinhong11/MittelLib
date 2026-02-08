package me.mmmjjkx.mittellib.item.meta;

import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.MapMeta;

public class MapDefinition extends ReadWriteObject {

    public static MapDefinition empty() {
        return new MapDefinition();
    }

    public static MapDefinition fromMapMeta(MapMeta meta) {
        return new MapDefinition();
    }

    @Override
    public void write(ConfigurationSection cs) {
    }

    @Override
    public void read(ConfigurationSection cs) {
    }
}
