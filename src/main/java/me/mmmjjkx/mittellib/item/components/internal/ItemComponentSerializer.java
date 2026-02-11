package me.mmmjjkx.mittellib.item.components.internal;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
import lombok.experimental.UtilityClass;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.item.components.impl.SimpleItemComponent;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.enums.MCVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

@UtilityClass
@ApiStatus.Internal
public class ItemComponentSerializer {
    private static final Map<String, ReadMethod> READERS = new HashMap<>();
    private static final Map<Class<?>, String> KEYS = new HashMap<>();

    static {
        scanAndRegister();
        registerSimple();
    }

    private static void scanAndRegister() {
        Reflections reflections = new Reflections("me.mmmjjkx.mittellib.item.components.impl");

        MCVersion currentVersion = MCVersion.getCurrent();
        Set<Class<?>> components = reflections.getTypesAnnotatedWith(ItemComponentSpec.class);

        for (Class<?> clazz : components) {
            if (!ReadWriteItemComponent.class.isAssignableFrom(clazz)) {
                continue;
            }

            ItemComponentSpec spec = clazz.getAnnotation(ItemComponentSpec.class);

            if (!spec.requiredVersion().isAtLeast(currentVersion)) {
                continue;
            }

            try {
                Method read = clazz.getDeclaredMethod("readFromSection", ConfigurationSection.class);
                read.setAccessible(true);

                READERS.put(spec.key(), new ReadMethod(read));
                KEYS.put(clazz, spec.key());
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        clazz.getName() + " missing static readFromSection(ConfigurationSection)"
                );
            }
        }
    }

    private static void registerSimple() {
        MCVersion current = MCVersion.getCurrent();

        if (current.isAtLeast(MCVersion.V1_20_5)) {
            READERS.put("damage", new ReadMethod(cs ->
                    SimpleItemComponent.readFromSection("damage", cs, Integer.class, (i, e) -> i.setData(DataComponentTypes.DAMAGE, e))
            ));

            READERS.put("max_damage", new ReadMethod(cs ->
                    SimpleItemComponent.readFromSection("max_damage", cs, Integer.class, (i, e) -> i.setData(DataComponentTypes.MAX_DAMAGE, e))
            ));

            READERS.put("max_stack_size", new ReadMethod(cs ->
                    SimpleItemComponent.readFromSection("max_stack_size", cs, Integer.class, (i, e) -> i.setData(DataComponentTypes.MAX_STACK_SIZE, e))
            ));

            READERS.put("enchantable", new ReadMethod(cs ->
                    SimpleItemComponent.readFromSection("enchantable", cs, Integer.class, (i, e) -> i.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(e)))
            ));
        }

        if (current.isAtLeast(MCVersion.V1_21_2)) {
            READERS.put("glider", new ReadMethod(cs ->
                    SimpleItemComponent.readFromSection("glider", cs, Boolean.class, (i, e) -> {
                        e = e == null || e;
                        if (e) {
                            i.setData(DataComponentTypes.GLIDER);
                        }
                    })
            ));
            READERS.put("item_model", new ReadMethod(cs ->
                    SimpleItemComponent.readFromSection("item_model", cs, String.class, (i, e) -> {
                        NamespacedKey key = BukkitUtils.getNamespacedKey(e);
                        if (key != null) {
                            i.setData(DataComponentTypes.ITEM_MODEL, key);
                        }
                    })
            ));
            READERS.put("tooltip_style", new ReadMethod(cs ->
                    SimpleItemComponent.readFromSection("tooltip_style", cs, String.class, (i, e) -> {
                        NamespacedKey key = BukkitUtils.getNamespacedKey(e);
                        if (key != null) {
                            i.setData(DataComponentTypes.TOOLTIP_STYLE, key);
                        }
                    })
            ));
        }

        if (current.isAtLeast(MCVersion.V1_21_11)) {
            READERS.put("intangible_projectile", new ReadMethod(cs ->
                    SimpleItemComponent.readFromSection("intangible_projectile", cs, Boolean.class, (i, e) -> {
                        e = e == null || e;
                        if (e) {
                            i.setData(DataComponentTypes.INTANGIBLE_PROJECTILE);
                        }
                    })
            ));
        }
    }

    public static List<ReadWriteItemComponent> readComponentsFromSection(ConfigurationSection cs) {
        List<ReadWriteItemComponent> list = new ArrayList<>();

        for (String key : cs.getKeys(false)) {
            ReadMethod reader = READERS.get(key);
            if (reader == null) continue;

            ReadWriteItemComponent result;

            if (cs.isConfigurationSection(key)) {
                ConfigurationSection section = cs.getConfigurationSection(key);
                result = reader.invoke(section);
            } else {
                result = reader.invoke(cs);
            }

            list.add(result);
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
                if (component instanceof SimpleItemComponent<?> simpleItemComponent) {
                    simpleItemComponent.write(cs);
                }

                continue;
            }

            component.write(cs.createSection(key));
        }
    }

    @ApiStatus.Internal
    private record ReadMethod(Function<ConfigurationSection, ReadWriteItemComponent> consumer) {
        public ReadMethod(Method method) {
            this(cs -> {
                try {
                    return (ReadWriteItemComponent) method.invoke(cs);
                } catch (Exception e) {
                    return null;
                }
            });
        }

        public ReadWriteItemComponent invoke(ConfigurationSection cs) {
            return consumer.apply(cs);
        }
    }
}