package me.mmmjjkx.mittellib.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public abstract class ReadWriteItemComponent extends ReadWriteObject {
    protected ReadWriteItemComponent() {
        super();
    }

    protected ReadWriteItemComponent(ConfigurationSection cs) {
        throw new UnsupportedOperationException();
    }

    public abstract void applyToItem(ItemStack item);

    public final void read(ConfigurationSection cs) {
        //Should move to static readFromSection method
        throw new RuntimeException(new IllegalAccessException("Please use #readFromSection method"));
    }
}
