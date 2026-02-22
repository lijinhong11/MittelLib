package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@ItemComponentSpec(key = "deathProtection", requiredVersion = MCVersion.V1_21_2)
@AllArgsConstructor
public class DeathProtectionComponent extends ReadWriteItemComponent {
    private final List<ConsumeEffect> effects;

    public static DeathProtectionComponent fromMinecraftComponent(DeathProtection deathProtection) {
        return new DeathProtectionComponent(deathProtection.deathEffects());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.DEATH_PROTECTION;
    }

    public static DeathProtectionComponent readFromSection(ConfigurationSection cs) {
        List<ConsumeEffect> effectList = ComponentCommons.readConsumeEffects(cs);
        return new DeathProtectionComponent(effectList);
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection(effects));
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (effects != null && !effects.isEmpty()) {
            ComponentCommons.writeConsumeEffects(effects, cs);
        }
    }
}
