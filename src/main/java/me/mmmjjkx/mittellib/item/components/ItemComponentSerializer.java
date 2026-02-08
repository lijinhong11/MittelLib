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
    private static final String FOOD = "food";
    private static final String PROFILE = "profile";
    private static final String USEREMAINDER = "use_remainder";
    private static final String EQUIPPABLE = "equippable";
    private static final String DAMAGERESISTANT = "damage_resistant";
    private static final String INSTRUMENT = "instrument";
    private static final String ENCHANTABLE = "enchantable";
    private static final String DEATHPROTECTION = "death_protection";
    private static final String TRIM = "trim";

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

        ConfigurationSection food = cs.getConfigurationSection(FOOD);
        if (food != null) {
            FoodComponent foodComponent = FoodComponent.readFromSection(food);
            if (foodComponent != null) {
                components.add(foodComponent);
            }
        }

        ConfigurationSection instrument = cs.getConfigurationSection(INSTRUMENT);
        if (instrument != null) {
            InstrumentComponent instrumentComponent = InstrumentComponent.readFromSection(instrument);
            if (instrumentComponent != null) {
                components.add(instrumentComponent);
            }
        }

        ConfigurationSection profile = cs.getConfigurationSection(PROFILE);
        if (profile != null) {
            ProfileComponent profileComponent = ProfileComponent.readFromSection(profile);
            if (profileComponent != null) {
                components.add(profileComponent);
            }
        }

        ConfigurationSection useRemainder = cs.getConfigurationSection(USEREMAINDER);
        if (useRemainder != null) {
            UseRemainderComponent useRemainderComponent = UseRemainderComponent.readFromSection(useRemainder);
            if (useRemainderComponent != null) {
                components.add(useRemainderComponent);
            }
        }

        ConfigurationSection equippable = cs.getConfigurationSection(EQUIPPABLE);
        if (equippable != null) {
            EquippableComponent equippableComponent = EquippableComponent.readFromSection(equippable);
            if (equippableComponent != null) {
                components.add(equippableComponent);
            }
        }

        return components;
    }

    public static void writeComponentsToConfiguration(List<ReadWriteItemComponent> components, ConfigurationSection cs) {
        for (ReadWriteItemComponent component : components) {
            if (component instanceof ConsumableComponent consumable) {
                consumable.write(cs.createSection(CONSUMABLE));
            }

            if (component instanceof CustomModelDataComponent cmd) {
                cmd.write(cs.createSection(CUSTOM_MODEL));
            }

            if (component instanceof FoodComponent food) {
                food.write(cs.createSection(FOOD));
            }

            if (component instanceof InstrumentComponent instrument) {
                instrument.write(cs.createSection(INSTRUMENT));
            }

            if (component instanceof ProfileComponent profile) {
                profile.write(cs.createSection(PROFILE));
            }

            if (component instanceof UseRemainderComponent useRemainder) {
                useRemainder.write(cs.createSection(USEREMAINDER));
            }
        }
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
