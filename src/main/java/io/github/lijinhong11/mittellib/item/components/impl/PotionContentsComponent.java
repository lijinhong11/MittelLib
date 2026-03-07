package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "potionContents", requiredVersion = MCVersion.V1_20_5)
@AllArgsConstructor
@NoArgsConstructor
public class PotionContentsComponent extends ReadWriteItemComponent {
    private static final Registry<PotionType> POTION_TYPES =
            RegistryAccess.registryAccess().getRegistry(RegistryKey.POTION);

    private @Nullable Color color;
    private @Nullable PotionType potionType;
    private @Nullable String customName;
    private List<PotionEffect> potionEffects = new ArrayList<>();

    public static PotionContentsComponent fromMinecraftComponent(PotionContents potionContents) {
        return new PotionContentsComponent(
                potionContents.customColor(),
                potionContents.potion(),
                potionContents.customName(),
                potionContents.customEffects());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.POTION_CONTENTS;
    }

    @Override
    public void applyToItem(ItemStack item) {
        PotionContents potionContents = PotionContents.potionContents()
                .customColor(color)
                .potion(potionType)
                .customName(customName)
                .addCustomEffects(potionEffects)
                .build();

        item.setData(DataComponentTypes.POTION_CONTENTS, potionContents);
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (color != null) {
            ConfigurationSection colorSection = cs.createSection("color");
            colorSection.set("alpha", color.getAlpha());
            colorSection.set("red", color.getRed());
            colorSection.set("green", color.getGreen());
            colorSection.set("blue", color.getBlue());
        }

        if (potionType != null) {
            cs.set("potionType", potionType.key().asString());
        }

        if (customName != null) {
            cs.set("customName", customName);
        }

        if (potionEffects != null && !potionEffects.isEmpty()) {
            List<Map<String, Object>> maps = new ArrayList<>();
            potionEffects.forEach(p -> maps.add(BukkitUtils.writePotionEffect(p)));
            cs.set("potionEffects", maps);
        }
    }

    public static PotionContentsComponent readFromSection(ConfigurationSection cs) {
        Color color = null;
        ConfigurationSection colorSection = cs.getConfigurationSection("colorSection");
        if (colorSection != null) {
            int alpha = colorSection.getInt("alpha", 255);
            int red = colorSection.getInt("red", 0);
            int green = colorSection.getInt("green", 0);
            int blue = colorSection.getInt("blue", 0);
            color = Color.fromARGB(alpha, red, green, blue);
        }

        String customName = cs.getString("customName");

        PotionType pt = null;
        if (cs.contains("potionType")) {
            NamespacedKey key = BukkitUtils.getNamespacedKey(cs.getString("potionType"));
            if (key != null) {
                pt = POTION_TYPES.get(key);
            }
        }

        List<PotionEffect> pe = new ArrayList<>();
        if (cs.contains("potionEffects")) {
            List<Map<?, ?>> maps = cs.getMapList("potionEffects");
            pe = maps.stream()
                    .map(m -> BukkitUtils.readPotionEffect((Map<String, Object>) m))
                    .toList();
        }

        return new PotionContentsComponent(color, pt, customName, pe);
    }
}
