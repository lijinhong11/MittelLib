package io.github.lijinhong11.mittellib.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public abstract class ReadWriteItemComponent extends ReadWriteObject {
    protected ReadWriteItemComponent() {
        super();
    }

    protected ReadWriteItemComponent(ConfigurationSection cs) {
        throw new UnsupportedOperationException("Please use #readFromSection method");
    }

    public abstract void applyToItem(ItemStack item);

    public final void read(ConfigurationSection cs) {
        //Should move to static readFromSection method
        throw new RuntimeException(new IllegalAccessException("Please use #readFromSection method"));
    }
}
