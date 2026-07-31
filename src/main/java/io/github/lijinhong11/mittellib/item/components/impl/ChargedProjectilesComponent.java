package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.MittelItem;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

@ItemComponentSpec(key = "chargedProjectiles")
@NoArgsConstructor
@AllArgsConstructor
public class ChargedProjectilesComponent extends ReadWriteItemComponent {
    private List<ItemStack> projectiles;

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.CHARGED_PROJECTILES;
    }

    public static ChargedProjectilesComponent fromMinecraftComponent(ChargedProjectiles chargedProjectiles) {
        return new ChargedProjectilesComponent(chargedProjectiles.projectiles());
    }

    public static ChargedProjectilesComponent readFromSection(ConfigurationSection cs) {
        List<?> list = cs.getList("projectiles", new ArrayList<>());
        List<ItemStack> items = new ArrayList<>();
        for (Object o : list) {
            ConfigurationSection itemSection = null;
            if (o instanceof ConfigurationSection section) {
                itemSection = section;
            } else if (o instanceof Map<?, ?> map) {
                YamlConfiguration temporary = new YamlConfiguration();
                itemSection = temporary.createSection("item", map);
            }

            if (itemSection != null) {
                items.add(MittelItem.readFromSection(itemSection).get());
            }
        }

        return new ChargedProjectilesComponent(items);
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectiles.chargedProjectiles(projectiles));
    }

    @Override
    public void write(ConfigurationSection cs) {
        List<ConfigurationSection> configurationSections = projectiles.stream()
                .map(i -> {
                    ConfigurationSection r = new YamlConfiguration();
                    new MittelItem(i).write(r);
                    return r;
                })
                .toList();

        cs.set("projectiles", configurationSections);
    }
}
