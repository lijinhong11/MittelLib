package me.mmmjjkx.mittellib.item.meta;

import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.MittelLib;
import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.EnumUtils;
import me.mmmjjkx.mittellib.utils.NumberUtils;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.FireworkMeta;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class FireworkDefinition extends ReadWriteObject {
    private final List<FireworkEffect> fireworkEffects;
    private @NonNegative int power;

    public static FireworkDefinition empty() {
        return new FireworkDefinition(new ArrayList<>(), 0);
    }

    public static FireworkDefinition fromFireworkMeta(FireworkMeta meta) {
        return new FireworkDefinition(meta.getEffects(), meta.hasPower() ? meta.getPower() : 0);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("power", power);

        if (fireworkEffects != null && !fireworkEffects.isEmpty()) {
            List<Map<String, Object>> fireworkEffectMaps = new ArrayList<>();
            for (FireworkEffect fe : fireworkEffects) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", fe.getType().toString());
                map.put("color", BukkitUtils.writeColor(fe.getColors()));
                map.put("fadeColors", BukkitUtils.writeColor(fe.getFadeColors()));
                map.put("flicker", fe.hasFlicker());
                map.put("trail", fe.hasTrail());

                fireworkEffectMaps.add(map);
            }

            cs.set("effects", fireworkEffectMaps);
        }
    }

    @Override
    public void read(ConfigurationSection cs) {
        this.power = NumberUtils.asUnsigned(cs.getInt("power"));

        this.fireworkEffects.clear();
        List<Map<?, ?>> maps = cs.getMapList("effects");
        for (Map<?, ?> map : maps) {
            Map<String, Object> fireworkEffectMap = (Map<String, Object>) map;

            String typeStr = (String) fireworkEffectMap.get("type");
            FireworkEffect.Type type = EnumUtils.readEnum(FireworkEffect.Type.class, typeStr);
            if (type == null) {
                MittelLib.getInstance()
                        .getLogger()
                        .severe("Cannot define a firework effect: type with name " + typeStr + " does not exist");
                continue;
            }

            this.fireworkEffects.add(
                    FireworkEffect.builder()
                            .flicker((Boolean) fireworkEffectMap.getOrDefault("flicker", false))
                            .trail((Boolean) fireworkEffectMap.getOrDefault("trail", false))
                            .with(type)
                            .withColor(BukkitUtils.toColors((List<Map<?, ?>>) fireworkEffectMap.get("colors")))
                            .withFade(BukkitUtils.toColors((List<Map<?, ?>>) fireworkEffectMap.get("fadeColors")))
                            .build()
            );
        }
    }

    public void applyTo(FireworkMeta fm) {
        fm.setPower(power);
        fm.clearEffects();
        fm.addEffects(fireworkEffects);
    }

    public static FireworkDefinition readFromSection(ConfigurationSection cs) {
        int power = NumberUtils.asUnsigned(cs.getInt("power"));

        List<FireworkEffect> fireworkEffects = new ArrayList<>();
        List<Map<?, ?>> maps = cs.getMapList("effects");
        for (Map<?, ?> map : maps) {
            Map<String, Object> fireworkEffectMap = (Map<String, Object>) map;

            String typeStr = (String) fireworkEffectMap.get("type");
            FireworkEffect.Type type = EnumUtils.readEnum(FireworkEffect.Type.class, typeStr);
            if (type == null) {
                MittelLib.getInstance()
                        .getLogger()
                        .severe("Cannot define a firework effect: type with name " + typeStr + " does not exist");
                continue;
            }

            fireworkEffects.add(
                    FireworkEffect.builder()
                            .flicker((Boolean) fireworkEffectMap.getOrDefault("flicker", false))
                            .trail((Boolean) fireworkEffectMap.getOrDefault("trail", false))
                            .with(type)
                            .withColor(BukkitUtils.toColors((List<Map<?, ?>>) fireworkEffectMap.get("colors")))
                            .withFade(BukkitUtils.toColors((List<Map<?, ?>>) fireworkEffectMap.get("fadeColors")))
                            .build()
            );
        }

        return new FireworkDefinition(fireworkEffects, power);
    }
}
