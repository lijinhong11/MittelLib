package io.github.lijinhong11.mittellib.item.meta;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteObject;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.EnumUtils;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.FireworkMeta;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class FireworkDefinition extends ReadWriteObject {
    private @NotNull List<FireworkEffect> fireworkEffects = new ArrayList<>();
    private @NonNegative int power;

    public static FireworkDefinition empty() {
        return new FireworkDefinition(new ArrayList<>(), 0);
    }

    public static FireworkDefinition fromFireworkMeta(FireworkMeta meta) {
        return new FireworkDefinition(meta.getEffects(), meta.hasPower() ? meta.getPower() : 0);
    }

    public FireworkDefinition(ConfigurationSection cs) {
        super(cs);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("power", power);

        if (!fireworkEffects.isEmpty()) {
            List<Map<String, Object>> fireworkEffectMaps = new ArrayList<>();
            for (FireworkEffect fe : fireworkEffects) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", fe.getType().toString());
                map.put("color", BukkitUtils.writeColors(fe.getColors()));
                map.put("fadeColors", BukkitUtils.writeColors(fe.getFadeColors()));
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
}
