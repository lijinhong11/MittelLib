package me.mmmjjkx.mittellib.item.components;

import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class DeathProtectionComponent extends ReadWriteItemComponent {
    private final List<ConsumeEffect> effects;

    public static DeathProtectionComponent fromMinecraftComponent(DeathProtection deathProtection) {
        return new DeathProtectionComponent(deathProtection.deathEffects());
    }

    @Override
    public void applyToItem(ItemStack item) {

    }

    @Override
    public void write(ConfigurationSection cs) {

    }

    public static DeathProtectionComponent readFromSection(ConfigurationSection cs) {
        List<ConsumeEffect> effectList = ItemComponentSerializer.readConsumeEffects(cs);
        return new DeathProtectionComponent(effectList);
    }
}
