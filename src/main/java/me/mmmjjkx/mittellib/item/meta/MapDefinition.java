package me.mmmjjkx.mittellib.item.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapDefinition extends ReadWriteObject {
    private @Nullable MapViewDefinition mapView;
    private boolean scaling;
    private @Nullable Color color;

    public static MapDefinition empty() {
        return new MapDefinition();
    }

    public static MapDefinition fromMapMeta(MapMeta meta) {
        return new MapDefinition(MapViewDefinition.fromMapView(meta.getMapView()), meta.isScaling(), meta.getColor());
    }

    @Override
    public void write(ConfigurationSection cs) {
    }

    @Override
    public void read(ConfigurationSection cs) {
    }

    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class MapViewDefinition extends ReadWriteObject {
        private MapView.Scale scale;
        private int centerX;
        private int centerZ;
        private boolean locked = false;
        private boolean trackingPosition;
        private boolean unlimitedTracking;
        private World world;

        public static MapViewDefinition empty() {
            return new MapViewDefinition();
        }

        public static MapViewDefinition fromMapView(MapView mv) {
            if (mv == null) {
                return new MapViewDefinition();
            }

            return new MapViewDefinition(mv.getScale(), mv.getCenterX(), mv.getCenterZ(), mv.isLocked(), mv.isTrackingPosition(), mv.isUnlimitedTracking(), mv.getWorld());
        }

        @Override
        public void write(ConfigurationSection cs) {

        }

        @Override
        public void read(ConfigurationSection cs) {

        }
    }
}
