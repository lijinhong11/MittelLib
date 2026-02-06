package me.mmmjjkx.mittellib.item.components;

import io.papermc.paper.datacomponent.item.DeathProtection;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@RequiredArgsConstructor
public class DeathProtectionComponent extends ReadWriteItemComponent {

    public static DeathProtectionComponent fromMinecraftComponent(DeathProtection deathProtection) {

    }

    @Override
    public void applyToItem(ItemStack item) {

    }

    @Override
    public void write(ConfigurationSection cs) {

    }
}
