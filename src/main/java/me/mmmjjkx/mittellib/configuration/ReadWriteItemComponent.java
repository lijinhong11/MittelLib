package me.mmmjjkx.mittellib.configuration;

import org.bukkit.inventory.ItemStack;

public abstract class ReadWriteItemComponent extends ReadWriteObject {
    public abstract void applyToItem(ItemStack item);
}
