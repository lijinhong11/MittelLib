package io.github.lijinhong11.mittellib.item.components.impl;


import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "kineticWeapon", requiredVersion = MCVersion.V1_21_11)
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
        ConfigurationSection dismountCondition = cs.createSection("dismount");
        ConfigurationSection knockbackCondition = cs.createSection("knockback");
        ConfigurationSection damageCondition = cs.createSection("damage");

        writeCondition(dismount, dismountCondition);
        writeCondition(knockback, knockbackCondition);
        writeCondition(damage, damageCondition);

        cs.set("delayTicks", delayTicks);
        cs.set("forwardMovement", forwardMovement);
        cs.set("damageMultiplier", damageMultiplier);
        cs.set("contactCooldownTicks", contactCooldownTicks);

        if (sound != null) {
            cs.set("sound", sound.asString());
        }

        if (hitSound != null) {
            cs.set("hitSound", hitSound.asString());
        }
    }

    public static KineticWeaponComponent readFromSection(ConfigurationSection cs) {
        KineticWeapon.Condition dismount = readCondition(cs.getConfigurationSection("dismount"));
        KineticWeapon.Condition knockback = readCondition(cs.getConfigurationSection("knockback"));
        KineticWeapon.Condition damage = readCondition(cs.getConfigurationSection("damage"));

        if (dismount == null || knockback == null || damage == null) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot define a kinetic weapon component: dismount condition & knockback condition & damage condition are required, one of them is missing");
            return null;
        }

        int contactCooldownTicks = cs.getInt("contactCooldownTicks", 10);
        if (contactCooldownTicks < 1) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot define a kinetic weapon component: contactCooldownTicks must be positive");
            return null;
        }

        int delayTicks = NumberUtils.asUnsigned(cs.getInt("delayTicks", 0));
        float forwardMovement = NumberUtils.asUnsigned((float) cs.getDouble("forwardMovement", 0));
        float damageMultiplier = NumberUtils.asUnsigned((float) cs.getDouble("damageMultiplier", 1));

        NamespacedKey sound = cs.contains("sound") ? BukkitUtils.getNamespacedKey(cs.getString("sound")) : null;
        NamespacedKey hitSound = cs.contains("hitSound") ? BukkitUtils.getNamespacedKey(cs.getString("hitSound")) : null;

        return new KineticWeaponComponent(dismount, knockback, damage, delayTicks, forwardMovement, damageMultiplier, sound, hitSound, contactCooldownTicks);
    }

    private static KineticWeapon.Condition readCondition(ConfigurationSection cs) {
        if (cs == null) {
            return null;
        }

        if (!cs.contains("maxDurationTicks")) {
            return null;
        }

        int maxDurationTicks = NumberUtils.asUnsigned(cs.getInt("maxDurationTicks"));
        float minSpeed = NumberUtils.asUnsigned((float) cs.getDouble("minSpeed", 0));
        float minRelativeSpeed = NumberUtils.asUnsigned((float) cs.getDouble("minRelativeSpeed", 0));

        return KineticWeapon.condition(maxDurationTicks, minSpeed, minRelativeSpeed);
    }

    private void writeCondition(KineticWeapon.Condition condition, ConfigurationSection cs) {
        cs.set("maxDuartionTicks", condition.maxDurationTicks());
        cs.set("minSpeed", condition.minSpeed());
        cs.set("minRelativeSpeed", condition.minRelativeSpeed());
    }
}
