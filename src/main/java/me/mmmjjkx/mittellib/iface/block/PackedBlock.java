package me.mmmjjkx.mittellib.iface.block;

import org.bukkit.Location;

public interface PackedBlock {
    void place(Location location);

    String getId();
}
