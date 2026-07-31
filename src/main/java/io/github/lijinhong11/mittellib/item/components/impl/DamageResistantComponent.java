package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "damageResistant", requiredVersion = MCVersion.V1_21_2)
public class DamageResistantComponent extends ReadWriteItemComponent {
    private static final Method FACTORY = findMethod(DamageResistant.class, "damageResistant", 1);
    private static final Method TYPES = findMethod(DamageResistant.class, "types", 0);
    private static final boolean SUPPORTS_KEY_SET =
            FACTORY.getParameterTypes()[0].getName().equals("io.papermc.paper.registry.set.RegistryKeySet");

    private Object types;

    private DamageResistantComponent(Object types) {
        this.types = types;
    }

    public static DamageResistantComponent fromMinecraftComponent(DamageResistant damageResistant) {
        try {
            return new DamageResistantComponent(TYPES.invoke(damageResistant));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to read damage resistant types", e);
        }
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.DAMAGE_RESISTANT;
    }

    @Nullable public static DamageResistantComponent readFromSection(ConfigurationSection cs) {
        Object types;
        if (SUPPORTS_KEY_SET) {
            List<NamespacedKey> keys = BukkitUtils.getNamespacedKeys(cs.getStringList("damageResistants"));
            if (!keys.isEmpty()) {
                types = createKeySet(keys);
            } else {
                String legacyKey = cs.getString("damageResistant");
                NamespacedKey key = BukkitUtils.getNamespacedKey(legacyKey);
                if (key == null) {
                    return null;
                }
                types = resolveTag(TagKey.create(RegistryKey.DAMAGE_TYPE, key.asString()));
            }
        } else {
            String legacyKey = cs.getString("damageResistant");
            NamespacedKey key = BukkitUtils.getNamespacedKey(legacyKey);
            if (key == null) {
                return null;
            }
            types = TagKey.create(RegistryKey.DAMAGE_TYPE, key.asString());
        }

        if (types == null) {
            return null;
        }
        return new DamageResistantComponent(types);
    }

    @Override
    public void applyToItem(ItemStack item) {
        if (types == null) {
            return;
        }

        try {
            DamageResistant component = (DamageResistant) FACTORY.invoke(null, types);
            item.setData(DataComponentTypes.DAMAGE_RESISTANT, component);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create damage resistant component", e);
        }
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (types == null) {
            return;
        }

        if (types instanceof TagKey<?> tag) {
            cs.set("damageResistant", tag.key().asString());
            return;
        }

        cs.set("damageResistants", getKeyStrings(types));
    }

    private static Object createKeySet(List<NamespacedKey> keys) {
        try {
            Class<?> typedKeyClass = Class.forName("io.papermc.paper.registry.TypedKey");
            Method create = typedKeyClass.getMethod("create", RegistryKey.class, String.class);
            List<Object> typedKeys = new ArrayList<>(keys.size());
            for (NamespacedKey key : keys) {
                typedKeys.add(create.invoke(null, RegistryKey.DAMAGE_TYPE, key.asString()));
            }

            Class<?> registrySetClass = Class.forName("io.papermc.paper.registry.set.RegistrySet");
            Method keySet = registrySetClass.getMethod("keySet", RegistryKey.class, Iterable.class);
            return keySet.invoke(null, RegistryKey.DAMAGE_TYPE, typedKeys);
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create damage type key set", e);
        }
    }

    private static Object resolveTag(TagKey<?> tagKey) {
        try {
            Class<?> registryAccessClass = Class.forName("io.papermc.paper.registry.RegistryAccess");
            Object access = registryAccessClass.getMethod("registryAccess").invoke(null);
            Object registry = registryAccessClass
                    .getMethod("getRegistry", RegistryKey.class)
                    .invoke(access, RegistryKey.DAMAGE_TYPE);
            Class<?> registryClass = Class.forName("org.bukkit.Registry");
            return registryClass.getMethod("getTag", TagKey.class).invoke(registry, tagKey);
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new IllegalStateException("Failed to resolve damage type tag", e);
        }
    }

    private static List<String> getKeyStrings(Object keySet) {
        try {
            Class<?> keySetClass = Class.forName("io.papermc.paper.registry.set.RegistryKeySet");
            Class<?> typedKeyClass = Class.forName("io.papermc.paper.registry.TypedKey");
            Method values = keySetClass.getMethod("values");
            Method keyMethod = typedKeyClass.getMethod("key");
            Iterable<?> typedKeys = (Iterable<?>) values.invoke(keySet);
            List<String> keys = new ArrayList<>();
            for (Object typedKey : typedKeys) {
                Object key = keyMethod.invoke(typedKey);
                keys.add(key.toString());
            }
            return keys;
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new IllegalStateException("Failed to serialize damage type key set", e);
        }
    }

    private static Method findMethod(Class<?> owner, String name, int parameterCount) {
        for (Method method : owner.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == parameterCount) {
                return method;
            }
        }
        throw new IllegalStateException(owner.getName() + " is missing method " + name);
    }
}
