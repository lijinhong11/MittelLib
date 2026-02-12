package io.github.lijinhong11.mittellib.item.components.impl;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "useCooldown", requiredVersion = MCVersion.V1_21_2)
@SuppressWarnings("UnstableApiUsage")
@RequiredArgsConstructor
@AllArgsConstructor
public class UseCooldownComponent extends ReadWriteItemComponent {
    private final @Positive float cooldown;
    private @Nullable Key cooldownGroup;

    public static UseCooldownComponent fromMinecraftComponent(UseCooldown useCooldown) {
        return new UseCooldownComponent(useCooldown.seconds(), useCooldown.cooldownGroup());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.USE_COOLDOWN;
    }

    @Nullable
    public static UseCooldownComponent readFromSection(ConfigurationSection cs) {
        float sec = NumberUtils.asUnsigned((float) cs.getDouble("seconds"));

        if (sec <= 0) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot define a use cooldown component: seconds is lower than or equals to 0");
            return null;
        }

        NamespacedKey cooldownGroup = null;
        if (cs.contains("cooldownGroup")) {
            NamespacedKey key = BukkitUtils.getNamespacedKey(cs.getString("cooldownGroup", "null"));
            if (key != null) {
                cooldownGroup = key;
            }
        }

        return new UseCooldownComponent(sec, cooldownGroup);
    }

    @Override
    public void applyToItem(ItemStack item) {
        UseCooldown useCooldown = UseCooldown.useCooldown(cooldown)
                .cooldownGroup(cooldownGroup)
                .build();

        item.setData(DataComponentTypes.USE_COOLDOWN, useCooldown);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("seconds", cooldown);

        if (cooldownGroup != null) {
            cs.set("cooldownGroup", cooldownGroup.asString());
        }
    }
}
