package me.mmmjjkx.mittellib.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public abstract class ReadWriteItemComponent extends ReadWriteObject {
    public abstract void applyToItem(ItemStack item);

    public final void read(ConfigurationSection cs) {
        //Should move to static read method
    }
}
