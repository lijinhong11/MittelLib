package io.github.lijinhong11.mittellib.item.meta;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteObject;
import io.github.lijinhong11.mittellib.utils.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class MapDefinition extends ReadWriteObject {
    private @Nullable MapViewDefinition mapView;
    private boolean scaling = false;
    private @Nullable Color color;

    public static MapDefinition empty() {
        return new MapDefinition(null, false, null);
    }

    public static MapDefinition fromMapMeta(MapMeta meta) {
        return new MapDefinition(MapViewDefinition.fromMapView(meta.getMapView()), meta.isScaling(), meta.getColor());
    }

    public MapDefinition(ConfigurationSection cs) {
        super(cs);
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (mapView != null) {
            mapView.write(cs.createSection("view"));
        }

        cs.set("scaling", scaling);

        if (color != null) {
            ConfigurationSection colorSection = cs.createSection("color");
            colorSection.set("alpha", color.getAlpha());
            colorSection.set("red", color.getRed());
            colorSection.set("green", color.getGreen());
            colorSection.set("blue", color.getBlue());
        }
    }

    @Override
    public void read(ConfigurationSection cs) {
        if (cs.contains("view") && cs.isConfigurationSection("view")) {
            mapView = MapViewDefinition.empty();
            mapView.read(cs.getConfigurationSection("view"));
        }

        scaling = cs.getBoolean("scaling", false);

        if (cs.contains("color") && cs.isConfigurationSection("color")) {
            ConfigurationSection colorSection = cs.getConfigurationSection("color");
            int a = colorSection.getInt("alpha", 255);
            int r = colorSection.getInt("red", 0);
            int g = colorSection.getInt("green", 0);
            int b = colorSection.getInt("blue", 0);

            color = Color.fromARGB(a, r, g, b);
        }
    }

    public void applyTo(MapMeta mm) {
        if (mapView != null) {
            if (mapView.world != null) {
                MapView view = Bukkit.createMap(mapView.world);
                mapView.applyTo(view);

                mm.setMapView(view);
            }
        }

        mm.setColor(color);
        mm.setScaling(scaling);
    }

    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class MapViewDefinition extends ReadWriteObject {
        private World world;

        private MapView.Scale scale = MapView.Scale.NORMAL;
        private int centerX;
        private int centerZ;
        private boolean locked = false;
        private boolean trackingPosition;
        private boolean unlimitedTracking;

        public static MapViewDefinition empty() {
            return new MapViewDefinition();
        }

        public static MapViewDefinition fromMapView(MapView mv) {
            if (mv == null) {
                throw new NullPointerException();
            }

            return new MapViewDefinition(mv.getWorld(), mv.getScale(), mv.getCenterX(), mv.getCenterZ(), mv.isLocked(), mv.isTrackingPosition(), mv.isUnlimitedTracking());
        }

        public void applyTo(MapView mv) {
            mv.setWorld(world);
            mv.setCenterX(centerX);
            mv.setCenterZ(centerZ);
            mv.setScale(scale);
            mv.setLocked(locked);
            mv.setTrackingPosition(trackingPosition);
            mv.setUnlimitedTracking(unlimitedTracking);
        }

        @Override
        public void write(ConfigurationSection cs) {
            cs.set("world", world.getName());
            cs.set("centerX", centerX);
            cs.set("centerZ", centerZ);
            cs.set("scale", scale.toString());
            cs.set("locked", locked);
            cs.set("trackingPosition", trackingPosition);
            cs.set("unlimitedTracking", unlimitedTracking);
        }

        @Override
        public void read(ConfigurationSection cs) {
            String w = cs.getString("world", "NULL_");
            int centerX = cs.getInt("centerX");
            int centerZ = cs.getInt("centerZ");
            String scaleStr = cs.getString("scale", "null");
            MapView.Scale scale = EnumUtils.readEnum(MapView.Scale.class, scaleStr);
            boolean locked = cs.getBoolean("locked", false);
            boolean trackingPosition = cs.getBoolean("trackingPosition");
            boolean unlimitedTracking = cs.getBoolean("unlimitedTracking");

            World world = Bukkit.getWorld(w);

            if (scale == null) {
                MittelLib.getInstance()
                        .getLogger()
                        .severe("Cannot find a map view scale with name " + scaleStr + " ! Fallback to NORMAL.");
                scale = MapView.Scale.NORMAL;
            }

            this.world = world;
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.scale = scale;
            this.locked = locked;
            this.trackingPosition = trackingPosition;
            this.unlimitedTracking = unlimitedTracking;
        }
    }
}
