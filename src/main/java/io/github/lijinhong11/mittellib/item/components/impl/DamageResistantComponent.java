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
import lombok.AllArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "damageResistant", requiredVersion = MCVersion.V1_21_2)
@AllArgsConstructor
public class DamageResistantComponent extends ReadWriteItemComponent {
    private TagKey<DamageType> tag;

    public static DamageResistantComponent fromMinecraftComponent(DamageResistant damageResistant) {
        return new DamageResistantComponent(damageResistant.types());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.DAMAGE_RESISTANT;
    }

    @Nullable
    public static DamageResistantComponent readFromSection(ConfigurationSection cs) {
        NamespacedKey key = BukkitUtils.getNamespacedKey(cs.getString("damageResistant", "null"));
        if (key == null) {
            return null;
        }

        return new DamageResistantComponent(TagKey.create(RegistryKey.DAMAGE_TYPE, key));
    }

    @Override
    public void applyToItem(ItemStack item) {
        if (tag == null) {
            return;
        }

        item.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(tag));
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("damageResistant", tag.key().asString());
    }
}
