package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "piercingWeapon", requiredVersion = MCVersion.V1_21_11)
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
        if (sound != null) {
            cs.set("sound", sound.asString());
        }

        if (hitSound != null) {
            cs.set("hitSound", hitSound.asString());
        }

        cs.set("dealsKnockback", dealsKnockback);
        cs.set("dismounts", dismounts);
    }

    public static PiercingWeaponComponent readFromSection(ConfigurationSection cs) {
        NamespacedKey sound = cs.contains("sound") ? BukkitUtils.getNamespacedKey(cs.getString("sound")) : null;
        NamespacedKey hitSound = cs.contains("hitSound") ? BukkitUtils.getNamespacedKey(cs.getString("hitSound")) : null;

        boolean dealsKnockback = cs.getBoolean("dealsKnockback", true);
        boolean dismounts = cs.getBoolean("dismounts", false);

        return new PiercingWeaponComponent(sound, hitSound, dealsKnockback, dismounts);
    }
}
