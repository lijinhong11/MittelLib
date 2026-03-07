package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.AllArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

@ItemComponentSpec(key = "trim", requiredVersion = MCVersion.V1_21_2)
@AllArgsConstructor
public class ArmorTrimComponent extends ReadWriteItemComponent {
    private static final Registry<TrimMaterial> TRIM_MATERIAL_REGISTRY =
            RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);
    private static final Registry<TrimPattern> TRIM_PATTERN_REGISTRY =
            RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN);

    private final ArmorTrim armorTrim;

    public ArmorTrimComponent(TrimMaterial trimMaterial, TrimPattern trimPattern) {
        armorTrim = new ArmorTrim(trimMaterial, trimPattern);
    }

    public static ArmorTrimComponent fromMinecraftComponent(ItemArmorTrim itemArmorTrim) {
        return new ArmorTrimComponent(itemArmorTrim.armorTrim());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.TRIM;
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(
                DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(armorTrim).build());
    }

    @Override
    public void write(ConfigurationSection cs) {
        TrimMaterial trimMaterial = armorTrim.getMaterial();
        TrimPattern trimPattern = armorTrim.getPattern();

        cs.set("material", TRIM_MATERIAL_REGISTRY.getKey(trimMaterial).asString());
        cs.set("pattern", TRIM_PATTERN_REGISTRY.getKey(trimPattern).asString());
    }

    public static ArmorTrimComponent readFromSection(ConfigurationSection cs) {
        NamespacedKey material = BukkitUtils.getNamespacedKey(cs.getString("material"));
        NamespacedKey pattern = BukkitUtils.getNamespacedKey(cs.getString("pattern"));

        if (material == null || pattern == null) {
            return null;
        }

        TrimMaterial mat = TRIM_MATERIAL_REGISTRY.get(material);
        TrimPattern pat = TRIM_PATTERN_REGISTRY.get(pattern);

        if (mat == null || pat == null) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Failed to define a armor trim component: material or pattern not found");
            return null;
        }

        return new ArmorTrimComponent(new ArmorTrim(mat, pat));
    }
}
