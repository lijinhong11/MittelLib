package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "dyedColor")
@NoArgsConstructor
@AllArgsConstructor
public class DyedColorComponent extends ReadWriteItemComponent {
    private Color color;

    public static DyedColorComponent fromMinecraftComponent(DyedItemColor dyedItemColor) {
        return new DyedColorComponent(dyedItemColor.color());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.DYED_COLOR;
    }

    public static @Nullable DyedColorComponent readFromSection(ConfigurationSection cs) {
        if (!cs.isInt("red") || !cs.isInt("green") || !cs.isInt("blue")) {
            return null;
        }

        try {
            Color color =
                    Color.fromARGB(cs.getInt("alpha", 255), cs.getInt("red"), cs.getInt("green"), cs.getInt("blue"));
            return new DyedColorComponent(color);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public void applyToItem(ItemStack item) {
        if (color != null) {
            item.setData(
                    DataComponentTypes.DYED_COLOR,
                    DyedItemColor.dyedItemColor().color(color).build());
        }
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (color == null) {
            return;
        }

        cs.set("alpha", color.getAlpha());
        cs.set("red", color.getRed());
        cs.set("green", color.getGreen());
        cs.set("blue", color.getBlue());
    }
}
