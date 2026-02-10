package me.mmmjjkx.mittellib.item.meta;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
public class MapDefinition extends ReadWriteObject {
    private @Nullable MapView mapView;
    private boolean scaling;
    private @Nullable Color color;

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
