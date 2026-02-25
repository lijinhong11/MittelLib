package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Weapon;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;

@ItemComponentSpec(key = "weapon", requiredVersion = MCVersion.V1_21_5)
@NoArgsConstructor
@AllArgsConstructor
public class WeaponComponent extends ReadWriteItemComponent {
    private @NonNegative int itemDamagePerAttack = 1;
    private @NonNegative float disableBlockingForSeconds = 0;

    public static WeaponComponent fromMinecraftComponent(Weapon weapon) {
        return new WeaponComponent(weapon.itemDamagePerAttack(), weapon.disableBlockingForSeconds());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.WEAPON;
    }

    @Override
    public void applyToItem(ItemStack item) {
        Weapon weapon = Weapon.weapon()
                .itemDamagePerAttack(itemDamagePerAttack)
                .disableBlockingForSeconds(disableBlockingForSeconds)
                .build();

        item.setData(DataComponentTypes.WEAPON, weapon);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("damagePerAttack", itemDamagePerAttack);
        cs.set("disableBlockingForSeconds", disableBlockingForSeconds);
    }

    public static WeaponComponent readFromSection(ConfigurationSection cs) {
        int itemDamagePerAttack = NumberUtils.asUnsigned(cs.getInt("damagePerAttack", 1));
        float disableBlockingForSeconds = NumberUtils.asUnsigned((float) cs.getDouble("disableBlockingForSeconds", 0));

        return new WeaponComponent(itemDamagePerAttack, disableBlockingForSeconds);
    }
}
