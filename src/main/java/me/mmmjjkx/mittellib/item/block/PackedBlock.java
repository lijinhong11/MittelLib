package me.mmmjjkx.mittellib.item.block;

import org.bukkit.Location;

public interface PackedBlock {
    void place(Location location);

    String getId();
}
