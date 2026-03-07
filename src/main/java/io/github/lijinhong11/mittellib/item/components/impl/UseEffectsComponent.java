package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseEffects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Range;

@ItemComponentSpec(key = "useEffects", requiredVersion = MCVersion.V1_21_11)
@NoArgsConstructor
@AllArgsConstructor
public class UseEffectsComponent extends ReadWriteItemComponent {
    private boolean canSprint = false;
    private boolean interactVibrations = true;
    private @Range(from = 0, to = 1) @NonNegative float speedMultiplier = 0.2f;

    public static UseEffectsComponent fromMinecraftComponent(UseEffects useEffects) {
        return new UseEffectsComponent(
                useEffects.canSprint(), useEffects.interactVibrations(), useEffects.speedMultiplier());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.USE_EFFECTS;
    }

    @Override
    public void applyToItem(ItemStack item) {
        UseEffects useEffects = UseEffects.useEffects()
                .canSprint(canSprint)
                .interactVibrations(interactVibrations)
                .speedMultiplier(speedMultiplier)
                .build();

        item.setData(DataComponentTypes.USE_EFFECTS, useEffects);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("canSprint", canSprint);
        cs.set("interactVibrations", interactVibrations);
        cs.set("speedMultiplier", speedMultiplier);
    }

    public static UseEffectsComponent readFromSection(ConfigurationSection cs) {
        boolean canSprint = cs.getBoolean("canSprint", false);
        boolean interactVibrations = cs.getBoolean("interactVibrations", true);
        float speedMultiplier = NumberUtils.asUnsigned((float) cs.getDouble("speedMultiplier", 0.2));

        return new UseEffectsComponent(canSprint, interactVibrations, speedMultiplier);
    }
}
