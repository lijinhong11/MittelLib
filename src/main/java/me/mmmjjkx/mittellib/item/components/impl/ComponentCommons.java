package me.mmmjjkx.mittellib.item.components.impl;

import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiStatus.Internal
class ComponentCommons {
    static List<ConsumeEffect> readConsumeEffects(ConfigurationSection cs) {
        ConfigurationSection effects = cs.getConfigurationSection("effects");
        List<ConsumeEffect> effectList = new ArrayList<>();
        if (effects != null) {
            if (effects.contains("teleportRandom")) {
                effectList.add(ConsumeEffect.teleportRandomlyEffect((float) effects.getDouble("teleportRandom")));
            }

            if (effects.contains("sound")) {
                NamespacedKey key = BukkitUtils.getNamespacedKey(effects.getString("sound", "minecraft:entity.generic.eat"));
                if (key != null) {
                    effectList.add(ConsumeEffect.playSoundConsumeEffect(key));
                }
            }

            if (effects.getBoolean("clearAllEffects", true)) {
                effectList.add(ConsumeEffect.clearAllStatusEffects());
            }

            List<TypedKey<PotionEffectType>> removeEffects = effects.getStringList("removeEffects").stream().map(e -> {
                NamespacedKey key = BukkitUtils.getNamespacedKey(e);
                if (key == null) {
                    return null;
                }

                return TypedKey.create(RegistryKey.MOB_EFFECT, key);
            }).filter(Objects::nonNull).toList();

            effectList.add(ConsumeEffect.removeEffects(RegistrySet.keySet(RegistryKey.MOB_EFFECT, removeEffects)));

            ConfigurationSection applyEffects = cs.getConfigurationSection("applyEffects");
            if (applyEffects != null) {
                float probability = (float) applyEffects.getDouble("probability");
                ConfigurationSection potionEffects = applyEffects.getConfigurationSection("effects");
                if (potionEffects != null) {
                    List<PotionEffect> pe = potionEffects.getKeys(false).stream().map(s -> {
                        ConfigurationSection effect = potionEffects.getConfigurationSection(s);
                        if (effect == null) {
                            return null;
                        }

                        return BukkitUtils.readPotionEffect(effect);
                    }).filter(Objects::nonNull).toList();

                    effectList.add(ConsumeEffect.applyStatusEffects(pe, probability));
                }
            }
        }

        return effectList;
    }

    static void writeConsumeEffects(List<ConsumeEffect> effects, ConfigurationSection cs) {
        ConfigurationSection effectsSection = cs.createSection("effects");
        for (ConsumeEffect ce : effects) {
            if (ce instanceof ConsumeEffect.TeleportRandomly tr) {
                effectsSection.set("teleportRandom", tr.diameter());
            }

            if (ce instanceof ConsumeEffect.PlaySound ps) {
                effectsSection.set("sound", ps.sound().asString());
            }

            if (ce instanceof ConsumeEffect.ClearAllStatusEffects) {
                effectsSection.set("clearAllEffects", true);
            }

            if (ce instanceof ConsumeEffect.ApplyStatusEffects ase) {
                ConfigurationSection applyEffectsSection = effectsSection.createSection("applyEffects");
                applyEffectsSection.set("probability", ase.probability());
                applyEffectsSection.set("effects", ase.effects());
            }

            if (ce instanceof ConsumeEffect.RemoveStatusEffects rse) {
                List<String> keys = rse.removeEffects().values().stream().map(k -> k.key().asString()).toList();
                effectsSection.set("removeEffects", keys);
            }
        }
    }
}
