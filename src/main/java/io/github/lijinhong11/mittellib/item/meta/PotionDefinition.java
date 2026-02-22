package io.github.lijinhong11.mittellib.item.meta;

import io.github.lijinhong11.mittellib.configuration.ReadWriteObject;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PotionDefinition extends ReadWriteObject {
    private @Nullable PotionType type;
    private @Nullable List<PotionEffect> customEffects;
    private @Nullable Color color;

    public static PotionDefinition empty() {
        return new PotionDefinition(PotionType.WATER, new ArrayList<>(), Color.BLUE);
    }

    public static PotionDefinition fromPotionMeta(PotionMeta potion) {
        return new PotionDefinition(potion.getBasePotionType(), potion.getCustomEffects(), potion.getColor());
    }

    public PotionDefinition(ConfigurationSection cs) {
        super(cs);
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (type != null) {
            cs.set("type", type.key().asString());
        }

        if (customEffects != null && !customEffects.isEmpty()) {
            List<Map<String, Object>> maps = new ArrayList<>();
            for (PotionEffect pe : customEffects) {
                maps.add(BukkitUtils.writePotionEffect(pe));
            }

            cs.set("customEffects", maps);
        }
        
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
        NamespacedKey type = BukkitUtils.getNamespacedKey(cs.getString("type"));
        if (type != null) {
            this.type = Registry.POTION.get(type);
        }

        if (cs.contains("customEffects")) {
            this.customEffects = cs.getMapList("customEffects").stream().map(m -> BukkitUtils.readPotionEffect((Map<String, Object>) m)).toList();
        }

        ConfigurationSection colorSection = cs.getConfigurationSection("color");
        if (colorSection != null) {
            int alpha = colorSection.getInt("alpha", 255);
            int red = colorSection.getInt("red", 0);
            int green = colorSection.getInt("green", 0);
            int blue = colorSection.getInt("blue", 0);
            color = Color.fromARGB(alpha, red, green, blue);
        }
    }

    public void applyTo(PotionMeta pm) {
        pm.setBasePotionType(type);
        pm.setColor(color);
        pm.clearCustomEffects();

        if (customEffects != null && !customEffects.isEmpty()) {
            for (PotionEffect pet : customEffects) {
                pm.addCustomEffect(pet, true);
            }
        }
    }
}
