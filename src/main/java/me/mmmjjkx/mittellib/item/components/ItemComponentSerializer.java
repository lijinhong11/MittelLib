package me.mmmjjkx.mittellib.item.components;

import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import lombok.experimental.UtilityClass;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UtilityClass
@SuppressWarnings("UnstableApiUsage")
public class ItemComponentSerializer {
    private static final String CONSUMABLE = "consumable";
    private static final String CUSTOM_MODEL = "custom_model";

    public static List<ReadWriteItemComponent> readComponentsFromSection(ConfigurationSection cs) {
        List<ReadWriteItemComponent> components = new ArrayList<>();

        ConfigurationSection consumable = cs.getConfigurationSection(CONSUMABLE);
        if (consumable != null) {
            ConsumableComponent component = ConsumableComponent.readFromSection(consumable);
            if (component != null) {
                components.add(component);
            }
        }

        ConfigurationSection customModel = cs.getConfigurationSection(CUSTOM_MODEL);
        if (customModel != null) {
            CustomModelDataComponent cmd = CustomModelDataComponent.readFromSection(customModel);
            components.add(cmd);
        }

        return components;
    }

    public static void writeComponentsToConfiguration(List<ReadWriteItemComponent> components, ConfigurationSection cs) {

    }

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
}
