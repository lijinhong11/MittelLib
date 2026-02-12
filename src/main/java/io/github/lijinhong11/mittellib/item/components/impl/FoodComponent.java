package io.github.lijinhong11.mittellib.item.components.impl;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "food", requiredVersion = MCVersion.V1_20_5)
@AllArgsConstructor
@NoArgsConstructor
public class FoodComponent extends ReadWriteItemComponent {
    private @NonNegative int nutrition = 0;

    private float saturation = 0.0f;

    private boolean canAlwaysEat = false;

    public static FoodComponent fromMinecraftComponent(FoodProperties properties) {
        return new FoodComponent(properties.nutrition(), properties.saturation(), properties.canAlwaysEat());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.FOOD;
    }

    @Nullable
    public static FoodComponent readFromSection(ConfigurationSection cs) {
        if (!cs.contains("nutrition")) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot define food component: 'nutrition' is not set");
            return null;
        }

        if (!cs.contains("saturation")) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot define food component: 'saturation' is not set");
            return null;
        }

        int nutrition = NumberUtils.asUnsigned(cs.getInt("nutrition", -1));

        double saturationDouble = cs.getDouble("saturation", -1.0d);
        if (saturationDouble < 0.0d) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot define food component: 'saturation' must be >= 0 (was " + saturationDouble + ")");
            return null;
        }

        float saturation = (float) saturationDouble;
        boolean canAlwaysEat = cs.getBoolean("canAlwaysEat", false);

        return new FoodComponent(nutrition, saturation, canAlwaysEat);
    }

    @Override
    public void applyToItem(ItemStack item) {
        FoodProperties properties = FoodProperties.food()
                .nutrition(nutrition)
                .saturation(saturation)
                .canAlwaysEat(canAlwaysEat)
                .build();

        item.setData(DataComponentTypes.FOOD, properties);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("nutrition", nutrition);
        cs.set("saturation", saturation);
        cs.set("canAlwaysEat", canAlwaysEat);
    }
}

