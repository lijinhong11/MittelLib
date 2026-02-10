package me.mmmjjkx.mittellib.item.components.impl;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.item.components.internal.ItemComponentSpec;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.enums.MCVersion;
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

    @Nullable
    public static DamageResistantComponent readFromSection(ConfigurationSection cs) {
        NamespacedKey key = BukkitUtils.getNamespacedKey(cs.getString("damageResistant", "null"));
        if (key == null) {
            return null;
        }

        return new DamageResistantComponent(TagKey.create(RegistryKey.DAMAGE_TYPE, key));
    }
}
