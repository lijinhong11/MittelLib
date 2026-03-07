package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

@ItemComponentSpec(key = "tooltipDisplay", requiredVersion = MCVersion.V1_21_2)
@NoArgsConstructor
@AllArgsConstructor
public class TooltipDisplayComponent extends ReadWriteItemComponent {
    private boolean hideTooltip = false;

    private Set<DataComponentType> hiddenComponents = new HashSet<>();

    public static TooltipDisplayComponent fromMinecraftComponent(TooltipDisplay tooltipDisplay) {
        return new TooltipDisplayComponent(tooltipDisplay.hideTooltip(), tooltipDisplay.hiddenComponents());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.TOOLTIP_DISPLAY;
    }

    @Override
    public void applyToItem(ItemStack item) {
        TooltipDisplay tooltipDisplay = TooltipDisplay.tooltipDisplay()
                .hideTooltip(hideTooltip)
                .hiddenComponents(hiddenComponents)
                .build();

        item.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplay);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("hideTooltip", hideTooltip);
        cs.set(
                "hiddenComponents",
                hiddenComponents.stream().map(c -> c.key().asString()).toList());
    }

    public static TooltipDisplayComponent readFromSection(ConfigurationSection cs) {
        boolean hide = cs.getBoolean("hideTooltip");

        Set<DataComponentType> types = new HashSet<>();
        for (String s : cs.getStringList("hiddenComponents")) {
            NamespacedKey key = BukkitUtils.getNamespacedKey(s);
            if (key == null) {
                continue;
            }

            DataComponentType pct = Registry.DATA_COMPONENT_TYPE.get(key);
            if (pct != null) {
                types.add(pct);
            }
        }

        return new TooltipDisplayComponent(hide, types);
    }
}
