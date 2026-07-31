package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "damageResistant", requiredVersion = MCVersion.V26_1_X)
public class DamageResistantComponent extends ReadWriteItemComponent {
    private final RegistryKeySet<DamageType> types;

    public DamageResistantComponent(RegistryKeySet<DamageType> types) {
        this.types = types;
    }

    public static DamageResistantComponent fromMinecraftComponent(DamageResistant damageResistant) {
        return new DamageResistantComponent(damageResistant.types());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.DAMAGE_RESISTANT;
    }

    public static @Nullable DamageResistantComponent readFromSection(ConfigurationSection cs) {
        List<NamespacedKey> keys = BukkitUtils.getNamespacedKeys(cs.getStringList("damageResistants"));
        if (keys.isEmpty()) {
            return null;
        }

        List<TypedKey<DamageType>> typedKeys = keys.stream()
                .map(key -> TypedKey.create(RegistryKey.DAMAGE_TYPE, key.asString()))
                .toList();
        return new DamageResistantComponent(RegistrySet.keySet(RegistryKey.DAMAGE_TYPE, typedKeys));
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(types));
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set(
                "damageResistants",
                types.values().stream().map(key -> key.key().asString()).toList());
    }
}
