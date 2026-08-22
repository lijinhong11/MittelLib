/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.mittellib.item.components.internal;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.impl.SimpleItemComponent;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.EnumUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import io.papermc.paper.datacomponent.item.MapId;
import io.papermc.paper.datacomponent.item.OminousBottleAmplifier;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.bukkit.DyeColor;
import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

@UtilityClass
@ApiStatus.Internal
@SuppressWarnings("UnstableApiUsage")
public class ItemComponentSerializer {
    private static final Map<String, ReadMethod> READERS = new HashMap<>();
    private static final Map<Class<?>, String> KEYS = new HashMap<>();
    private static final Map<DataComponentType, String> TYPE_KEYS = new HashMap<>();
    private static final Map<DataComponentType, Method> FROM_METHODS = new HashMap<>();

    static {
        scanAndRegister();
        registerSimples();

        FutureComponentRegistry.register();
    }

    @SuppressWarnings("unchecked")
    private static void scanAndRegister() {
        Reflections reflections = new Reflections("io.github.lijinhong11.mittellib.item.components.impl");

        MCVersion current = MCVersion.getCurrent();

        for (Class<?> raw : reflections.getTypesAnnotatedWith(ItemComponentSpec.class)) {
            if (!ReadWriteItemComponent.class.isAssignableFrom(raw)) {
                continue;
            }

            Class<? extends ReadWriteItemComponent> clazz = (Class<? extends ReadWriteItemComponent>) raw;

            ItemComponentSpec spec = clazz.getAnnotation(ItemComponentSpec.class);

            if (!current.isAtLeast(spec.requiredVersion())) {
                continue;
            }

            try {
                Method readMethod = clazz.getDeclaredMethod("readFromSection", ConfigurationSection.class);
                readMethod.setAccessible(true);

                Method fromMethod = Arrays.stream(clazz.getDeclaredMethods())
                        .filter(m -> m.getName().equals("fromMinecraftComponent"))
                        .findAny()
                        .orElseThrow();

                fromMethod.setAccessible(true);

                Method typeMethod = clazz.getDeclaredMethod("getDataComponentType");
                typeMethod.setAccessible(true);

                DataComponentType type = (DataComponentType) typeMethod.invoke(null);

                READERS.put(spec.key(), new ReadMethod(readMethod, fromMethod));
                KEYS.put(clazz, spec.key());

                TYPE_KEYS.put(type, spec.key());

                FROM_METHODS.put(type, fromMethod);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(clazz.getName() + " missing required static methods");
            } catch (Exception e) {
                throw new RuntimeException("Failed to register component " + clazz.getName(), e);
            }
        }
    }

    static <T> void registerSimple(
            String key, Class<T> type, DataComponentType dataType, BiConsumer<ItemStack, T> applier) {
        READERS.put(
                key,
                new ReadMethod(
                        cs -> SimpleItemComponent.readFromSection(key, cs, type, applier),
                        obj -> SimpleItemComponent.pack(key, obj, type, applier),
                        true));

        TYPE_KEYS.put(dataType, key);
    }

    private static void registerNonValued(String key, DataComponentType.NonValued dataType) {
        READERS.put(
                key,
                new ReadMethod(
                        cs -> cs.getBoolean(key)
                                ? new SimpleItemComponent<>(key, true, (item, ignored) -> item.setData(dataType))
                                : null,
                        ignored -> new SimpleItemComponent<>(key, true, (item, value) -> item.setData(dataType)),
                        true));

        TYPE_KEYS.put(dataType, key);
    }

    private static void registerSimples() {
        registerNonValued("unbreakable", DataComponentTypes.UNBREAKABLE);

        registerSimple(
                "damage", Integer.class, DataComponentTypes.DAMAGE, (i, e) -> i.setData(DataComponentTypes.DAMAGE, e));
        registerSimple(
                "maxDamage",
                Integer.class,
                DataComponentTypes.MAX_DAMAGE,
                (i, e) -> i.setData(DataComponentTypes.MAX_DAMAGE, e));
        registerSimple(
                "maxStackSize",
                Integer.class,
                DataComponentTypes.MAX_STACK_SIZE,
                (i, e) -> i.setData(DataComponentTypes.MAX_STACK_SIZE, e));
        registerSimple(
                "enchantable",
                Integer.class,
                DataComponentTypes.ENCHANTABLE,
                (i, e) -> i.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(e)));

        registerSimple(
                "repairCost", Integer.class, DataComponentTypes.REPAIR_COST, (i, e) -> i.setData(DataComponentTypes.REPAIR_COST, e));
        registerSimple(
                "enchantmentGlintOverride",
                Boolean.class,
                DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE,
                (i, e) -> i.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, e));
        registerSimple(
                "minimumAttackCharge",
                Float.class,
                DataComponentTypes.MINIMUM_ATTACK_CHARGE,
                (i, e) -> i.setData(DataComponentTypes.MINIMUM_ATTACK_CHARGE, e));
        registerSimple(
                "potionDurationScale",
                Float.class,
                DataComponentTypes.POTION_DURATION_SCALE,
                (i, e) -> i.setData(DataComponentTypes.POTION_DURATION_SCALE, e));
        registerSimple(
                "mapId", Integer.class, DataComponentTypes.MAP_ID, (i, e) -> i.setData(DataComponentTypes.MAP_ID, MapId.mapId(e)));
        registerSimple(
                "ominousBottleAmplifier",
                Integer.class,
                DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER,
                (i, e) -> i.setData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, OminousBottleAmplifier.amplifier(e)));

        registerSimple("baseColor", String.class, DataComponentTypes.BASE_COLOR, (i, e) -> {
            DyeColor dyeColor = EnumUtils.readEnum(DyeColor.class, e);
            if (dyeColor == null) {
                MittelLib.getInstance().getLogger().severe("Failed to find a dye color with name " + e);
                return;
            }

            i.setData(DataComponentTypes.BASE_COLOR, dyeColor);
        });

        registerSimple("rarity", String.class, DataComponentTypes.RARITY, (i, e) -> {
            ItemRarity rarity = EnumUtils.readEnum(ItemRarity.class, e);
            if (rarity == null) {
                MittelLib.getInstance().getLogger().severe("Failed to find a item rarity with name " + e);
                return;
            }

            i.setData(DataComponentTypes.RARITY, rarity);
        });

        registerSimple("jukeboxPlayable", String.class, DataComponentTypes.JUKEBOX_PLAYABLE, (i, e) -> {
            NamespacedKey key = BukkitUtils.getNamespacedKey(e);
            if (key != null) {
                JukeboxSong song = RegistryAccess.registryAccess()
                        .getRegistry(RegistryKey.JUKEBOX_SONG)
                        .get(key);
                if (song == null) {
                    MittelLib.getInstance()
                            .getLogger()
                            .severe("Failed to find a jukebox song with key " + key.asString());
                    return;
                }

                i.setData(
                        DataComponentTypes.JUKEBOX_PLAYABLE,
                        JukeboxPlayable.jukeboxPlayable(song).build());
            }
        });

        registerNonValued("glider", DataComponentTypes.GLIDER);

        registerSimple("itemModel", String.class, DataComponentTypes.ITEM_MODEL, (i, e) -> {
            NamespacedKey key = BukkitUtils.getNamespacedKey(e);
            if (key != null) {
                i.setData(DataComponentTypes.ITEM_MODEL, key);
            }
        });

        registerSimple("tooltipStyle", String.class, DataComponentTypes.TOOLTIP_STYLE, (i, e) -> {
            NamespacedKey key = BukkitUtils.getNamespacedKey(e);
            if (key != null) {
                i.setData(DataComponentTypes.TOOLTIP_STYLE, key);
            }
        });

        registerSimple("damageType", String.class, DataComponentTypes.DAMAGE_TYPE, (i, e) -> {
            Registry<DamageType> reg = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);
            NamespacedKey key = BukkitUtils.getNamespacedKey(e);
            if (key == null) {
                return;
            }

            DamageType dt = reg.get(key);
            if (dt == null) {
                MittelLib.getInstance().getLogger().severe("Failed to find a damage type with key " + key.asString());
                return;
            }

            i.setData(DataComponentTypes.DAMAGE_TYPE, dt);
        });

        registerNonValued("intangibleProjectile", DataComponentTypes.INTANGIBLE_PROJECTILE);

        registerSimple("dye", String.class, DataComponentTypes.DYE, (i, s) -> {
            try {
                DyeColor dyeColor = DyeColor.valueOf(s.toUpperCase());
                i.setData(DataComponentTypes.DYE, dyeColor);
            } catch (IllegalArgumentException e) {
                MittelLib.getInstance().getLogger().severe("Failed to find a dye color with name " + s);
            }
        });

        registerSimple("noteBlockSound", String.class, DataComponentTypes.NOTE_BLOCK_SOUND, (i, s) -> {
            NamespacedKey key = BukkitUtils.getNamespacedKey(s);
            if (key != null) {
                i.setData(DataComponentTypes.NOTE_BLOCK_SOUND, key);
            }
        });

        registerSimple("breakSound", String.class, DataComponentTypes.BREAK_SOUND, (i, s) -> {
            NamespacedKey key = BukkitUtils.getNamespacedKey(s);
            if (key != null) {
                i.setData(DataComponentTypes.BREAK_SOUND, key);
            }
        });

        registerEnum("wolfCollar", DyeColor.class, DataComponentTypes.WOLF_COLLAR, (i, e) -> i.setData(DataComponentTypes.WOLF_COLLAR, e));
        registerEnum("catCollar", DyeColor.class, DataComponentTypes.CAT_COLLAR, (i, e) -> i.setData(DataComponentTypes.CAT_COLLAR, e));
        registerEnum("sheepColor", DyeColor.class, DataComponentTypes.SHEEP_COLOR, (i, e) -> i.setData(DataComponentTypes.SHEEP_COLOR, e));
        registerEnum("shulkerColor", DyeColor.class, DataComponentTypes.SHULKER_COLOR, (i, e) -> i.setData(DataComponentTypes.SHULKER_COLOR, e));
        registerEnum("tropicalFishBaseColor", DyeColor.class, DataComponentTypes.TROPICAL_FISH_BASE_COLOR, (i, e) -> i.setData(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, e));
        registerEnum("tropicalFishPatternColor", DyeColor.class, DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, (i, e) -> i.setData(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, e));
    }

    private static <T extends Enum<T>> void registerEnum(
            String key, Class<T> type, DataComponentType dataType, BiConsumer<ItemStack, T> applier) {
        registerSimple(key, String.class, dataType, (item, value) -> {
            T enumValue = EnumUtils.readEnum(type, value);
            if (enumValue != null) {
                applier.accept(item, enumValue);
            } else {
                MittelLib.getInstance().getLogger().severe("Invalid value for "+ type.getSimpleName() + ":" + value);
            }
        });
    }

    public static List<ReadWriteItemComponent> readComponentsFromSection(ConfigurationSection cs) {
        List<ReadWriteItemComponent> list = new ArrayList<>();

        for (String key : cs.getKeys(false)) {
            ReadMethod reader = READERS.get(key);
            if (reader == null) continue;

            ReadWriteItemComponent component =
                    cs.isConfigurationSection(key) ? reader.invoke(cs.getConfigurationSection(key)) : reader.invoke(cs);

            if (component != null) {
                list.add(component);
            }
        }

        return list;
    }

    public static List<ReadWriteItemComponent> readComponentsFromItem(ItemStack item) {
        List<ReadWriteItemComponent> list = new ArrayList<>();

        for (DataComponentType type : item.getDataTypes()) {
            if (TYPE_KEYS.get(type) == null) {
                continue;
            } else {
                String key = TYPE_KEYS.get(type);
                ReadMethod rm = READERS.get(key);
                if (rm.simple) {
                    if (type instanceof DataComponentType.NonValued v) {
                        list.add(new SimpleItemComponent<>(key, true, (i, b) -> i.setData(v)));
                    }

                    if (type instanceof DataComponentType.Valued<?> va) {
                        SimpleItemComponent<?> sic = getSimpleValuedComponent(key, va, item);
                        if (sic != null) {
                            list.add(sic);
                        }
                    }

                    continue;
                }
            }

            Method fromMethod = FROM_METHODS.get(type);
            if (fromMethod == null) continue;

            if (type instanceof DataComponentType.Valued<?> valued) {
                Object value = item.getData(valued);
                if (value == null) continue;

                try {
                    ReadWriteItemComponent component = (ReadWriteItemComponent) fromMethod.invoke(null, value);

                    if (component != null) {
                        list.add(component);
                    }
                } catch (Exception ignored) {
                }
            }

            if (type instanceof DataComponentType.NonValued nonValued) {
                list.add(new SimpleItemComponent<>(TYPE_KEYS.get(type), true, (i, x) -> i.setData(nonValued)));
            }
        }

        return list;
    }

    public static @Nullable ReadWriteItemComponent getFromMinecraftComponent(
            DataComponentType type, @Nullable Object context) {
        String key = TYPE_KEYS.get(type);
        if (key == null) {
            return null;
        }

        ReadMethod rm = READERS.get(key);
        if (rm == null) {
            return null;
        }

        return rm.fromMinecraftComponent(context);
    }

    private static <T> SimpleItemComponent<T> getSimpleValuedComponent(
            String key, DataComponentType.Valued<T> va, ItemStack item) {
        T value = item.getData(va);
        if (value != null) {
            return new SimpleItemComponent<>(key, value, (i, b) -> i.setData(va, b));
        }

        return null;
    }

    public static void writeComponentsToConfiguration(
            List<ReadWriteItemComponent> components, ConfigurationSection cs) {
        for (ReadWriteItemComponent component : components) {

            String key = KEYS.get(component.getClass());

            if (key == null) {
                if (component instanceof SimpleItemComponent<?> simple) {
                    simple.write(cs);
                }
                continue;
            }

            component.write(cs.createSection(key));
        }
    }

    @ApiStatus.Internal
    private record ReadMethod(
            Function<ConfigurationSection, ReadWriteItemComponent> reader,
            Function<Object, ReadWriteItemComponent> fromMC,
            boolean simple) {
        ReadMethod(Method method1, Method method2) {
            this(
                    cs -> {
                        try {
                            return (ReadWriteItemComponent) method1.invoke(null, cs);
                        } catch (Exception e) {
                            return null;
                        }
                    },
                    obj -> {
                        try {
                            return (ReadWriteItemComponent) method2.invoke(null, obj);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    false);
        }

        ReadWriteItemComponent invoke(ConfigurationSection cs) {
            return reader.apply(cs);
        }

        ReadWriteItemComponent fromMinecraftComponent(Object object) {
            return fromMC.apply(object);
        }
    }
}
