package me.mmmjjkx.mittellib.item.meta;

import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullDefinition extends ReadWriteObject {

    public static SkullDefinition empty() {
        return new SkullDefinition();
    }

    public static SkullDefinition fromSkullMeta(SkullMeta meta) {
        return new SkullDefinition();
    }

    @Override
    public void write(ConfigurationSection cs) {
    }

    @Override
    public void read(ConfigurationSection cs) {
    }
}
