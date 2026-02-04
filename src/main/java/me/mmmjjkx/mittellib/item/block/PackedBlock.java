package me.mmmjjkx.mittellib.item.block;

import org.bukkit.Location;

public abstract class PackedBlock {
    public abstract void place(Location location);

    public abstract String getId();
}
