package me.mmmjjkx.mittellib.item.components.impl;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.item.components.internal.ItemComponentSpec;
import me.mmmjjkx.mittellib.utils.enums.MCVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@ItemComponentSpec(key = "deathProtection", requiredVersion = MCVersion.V1_21_2)
@AllArgsConstructor
public class DeathProtectionComponent extends ReadWriteItemComponent {
    private final List<ConsumeEffect> effects;

    public static DeathProtectionComponent fromMinecraftComponent(DeathProtection deathProtection) {
        return new DeathProtectionComponent(deathProtection.deathEffects());
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

    public static DeathProtectionComponent readFromSection(ConfigurationSection cs) {
        List<ConsumeEffect> effectList = ComponentCommons.readConsumeEffects(cs);
        return new DeathProtectionComponent(effectList);
    }
}
