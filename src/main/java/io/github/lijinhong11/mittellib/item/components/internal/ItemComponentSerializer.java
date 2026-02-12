package io.github.lijinhong11.mittellib.item.components.internal;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
import lombok.experimental.UtilityClass;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.impl.SimpleItemComponent;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
    }

    @SuppressWarnings("unchecked")
    private static void scanAndRegister() {
        Reflections reflections =
                new Reflections("me.mmmjjkx.mittellib.item.components.impl");

        MCVersion current = MCVersion.getCurrent();

        for (Class<?> raw : reflections.getTypesAnnotatedWith(ItemComponentSpec.class)) {

            if (!ReadWriteItemComponent.class.isAssignableFrom(raw)) {
                continue;
            }

            Class<? extends ReadWriteItemComponent> clazz =
                    (Class<? extends ReadWriteItemComponent>) raw;

            ItemComponentSpec spec = clazz.getAnnotation(ItemComponentSpec.class);

            if (!current.isAtLeast(spec.requiredVersion())) {
                continue;
            }

            try {
                Method readMethod =
                        clazz.getDeclaredMethod("readFromSection", ConfigurationSection.class);
                readMethod.setAccessible(true);

                READERS.put(spec.key(), new ReadMethod(readMethod));
                KEYS.put(clazz, spec.key());

                Method typeMethod =
                        clazz.getDeclaredMethod("getDataComponentType");
                typeMethod.setAccessible(true);

                DataComponentType type = (DataComponentType) typeMethod.invoke(null);

                TYPE_KEYS.put(type, spec.key());

                Method fromMethod = Arrays.stream(clazz.getDeclaredMethods()).filter(m -> m.getName().equals("fromMinecraftComponent")).findAny().get();

                fromMethod.setAccessible(true);
                FROM_METHODS.put(type, fromMethod);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        clazz.getName() + " missing required static methods"
                );
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to register component " + clazz.getName(),
                        e
                );
            }
        }
    }

    private static <T> void registerSimple(
            String key,
            Class<T> type,
            DataComponentType dataType,
            BiConsumer<ItemStack, T> applier
    ) {
        READERS.put(key, new ReadMethod(cs ->
                SimpleItemComponent.readFromSection(key, cs, type, applier)
        ));

        TYPE_KEYS.put(dataType, key);
    }

    private static void registerSimples() {
        MCVersion current = MCVersion.getCurrent();
        if (current.isAtLeast(MCVersion.V1_20_5)) {
            registerSimple("damage", Integer.class, DataComponentTypes.DAMAGE, (i, e) -> i.setData(DataComponentTypes.DAMAGE, e));
            registerSimple("max_damage", Integer.class, DataComponentTypes.MAX_DAMAGE, (i, e) -> i.setData(DataComponentTypes.MAX_DAMAGE, e));
            registerSimple("max_stack_size", Integer.class, DataComponentTypes.MAX_STACK_SIZE, (i, e) -> i.setData(DataComponentTypes.MAX_STACK_SIZE, e));
            registerSimple("enchantable", Integer.class, DataComponentTypes.ENCHANTABLE, (i, e) -> i.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(e)));
        }

        if (current.isAtLeast(MCVersion.V1_21_2)) {
            registerSimple("glider", Boolean.class, DataComponentTypes.GLIDER, (i, e) -> {
                e = e == null || e;
                if (e) {
                    i.setData(DataComponentTypes.GLIDER);
                }
            });

            registerSimple("item_model", String.class, DataComponentTypes.ITEM_MODEL, (i, e) -> {
                NamespacedKey key = BukkitUtils.getNamespacedKey(e);
                if (key != null) {
                    i.setData(DataComponentTypes.ITEM_MODEL, key);
                }
            });

            registerSimple("tooltip_style", String.class, DataComponentTypes.TOOLTIP_STYLE, (i, e) -> {
                NamespacedKey key = BukkitUtils.getNamespacedKey(e);
                if (key != null) {
                    i.setData(DataComponentTypes.TOOLTIP_STYLE, key);
                }
            });
        }

        if (current.isAtLeast(MCVersion.V1_21_11)) {
            registerSimple("intangible_projectile", Boolean.class, DataComponentTypes.INTANGIBLE_PROJECTILE, (i, e) -> {
                e = e == null || e;
                if (e) {
                    i.setData(DataComponentTypes.INTANGIBLE_PROJECTILE);
                }
            });
        }
    }

    public static List<ReadWriteItemComponent> readComponentsFromSection(
            ConfigurationSection cs
    ) {
        List<ReadWriteItemComponent> list = new ArrayList<>();

        for (String key : cs.getKeys(false)) {
            ReadMethod reader = READERS.get(key);
            if (reader == null) continue;

            ReadWriteItemComponent component =
                    cs.isConfigurationSection(key)
                            ? reader.invoke(cs.getConfigurationSection(key))
                            : reader.invoke(cs);

            if (component != null) {
                list.add(component);
            }
        }

        return list;
    }

    public static List<ReadWriteItemComponent> readComponentsFromItem(
            ItemStack item
    ) {
        List<ReadWriteItemComponent> list = new ArrayList<>();

        for (DataComponentType type : item.getDataTypes()) {
            Method fromMethod = FROM_METHODS.get(type);
            if (fromMethod == null) continue;

            if (type instanceof DataComponentType.Valued<?> valued) {
                Object value = item.getData(valued);
                if (value == null) continue;

                try {
                    ReadWriteItemComponent component =
                            (ReadWriteItemComponent) fromMethod.invoke(null, value);

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

    public static void writeComponentsToConfiguration(
            List<ReadWriteItemComponent> components,
            ConfigurationSection cs
    ) {
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
            Function<ConfigurationSection, ReadWriteItemComponent> reader
    ) {
        ReadMethod(Method method) {
            this(cs -> {
                try {
                    return (ReadWriteItemComponent) method.invoke(null, cs);
                } catch (Exception e) {
                    return null;
                }
            });
        }

        ReadWriteItemComponent invoke(ConfigurationSection cs) {
            return reader.apply(cs);
        }
    }
}