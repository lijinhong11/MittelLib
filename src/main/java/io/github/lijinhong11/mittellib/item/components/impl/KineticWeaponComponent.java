package io.github.lijinhong11.mittellib.item.components.impl;


import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@RequiredArgsConstructor
public class KineticWeaponComponent extends ReadWriteItemComponent {
    private final @NotNull KineticWeapon.Condition dismount;
    private final @NotNull KineticWeapon.Condition knockback;
    private final @NotNull KineticWeapon.Condition damage;

    private @NonNegative int delayTicks = 0;
    private float forwardMovement = 0;
    private float damageMultiplier = 1;
    private @Nullable Key sound;
    private @Nullable Key hitSound;
    private @Positive int contactCooldownTicks = 10;

    public static KineticWeaponComponent fromMinecraftComponent(KineticWeapon kineticWeapon) {
        return new KineticWeaponComponent(kineticWeapon.dismountConditions(), kineticWeapon.knockbackConditions(), kineticWeapon.damageConditions(), kineticWeapon.delayTicks(), kineticWeapon.forwardMovement(), kineticWeapon.damageMultiplier(), kineticWeapon.sound(), kineticWeapon.hitSound(), kineticWeapon.contactCooldownTicks());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.KINETIC_WEAPON;
    }

    @Override
    public void applyToItem(ItemStack item) {
        KineticWeapon kineticWeapon = KineticWeapon.kineticWeapon()
                .dismountConditions(dismount)
                .damageConditions(damage)
                .knockbackConditions(knockback)
                .delayTicks(delayTicks)
                .forwardMovement(forwardMovement)
                .damageMultiplier(damageMultiplier)
                .sound(sound)
                .hitSound(hitSound)
                .contactCooldownTicks(contactCooldownTicks)
                .build();

        item.setData(DataComponentTypes.KINETIC_WEAPON, kineticWeapon);
    }

    @Override
    public void write(ConfigurationSection cs) {

    }
}
