package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
public class PiercingWeaponComponent extends ReadWriteItemComponent {
    private @Nullable Key sound;
    private @Nullable Key hitSound;
    private boolean dealsKnockback = true;
    private boolean dismounts = false;

    public static PiercingWeaponComponent fromMinecraftComponent(PiercingWeapon piercingWeapon) {
        return new PiercingWeaponComponent(piercingWeapon.sound(), piercingWeapon.hitSound(), piercingWeapon.dealsKnockback(), piercingWeapon.dismounts());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.PIERCING_WEAPON;
    }

    @Override
    public void applyToItem(ItemStack item) {
        PiercingWeapon piercingWeapon = PiercingWeapon.piercingWeapon()
                .sound(sound)
                .hitSound(hitSound)
                .dealsKnockback(dealsKnockback)
                .dismounts(dismounts)
                .build();

        item.setData(DataComponentTypes.PIERCING_WEAPON, piercingWeapon);
    }

    @Override
    public void write(ConfigurationSection cs) {

    }
}
