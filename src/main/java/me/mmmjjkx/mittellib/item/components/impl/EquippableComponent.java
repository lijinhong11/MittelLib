package me.mmmjjkx.mittellib.item.components.impl;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.mmmjjkx.mittellib.MittelLib;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.item.components.internal.ItemComponentSpec;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.EnumUtils;
import me.mmmjjkx.mittellib.utils.MCVersion;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ItemComponentSpec(key = "equippable", requiredVersion = MCVersion.V1_21_2)
@RequiredArgsConstructor
@AllArgsConstructor
public class EquippableComponent extends ReadWriteItemComponent {
    private @NotNull final EquipmentSlot slot;
    private @Nullable Key equipSound;
    private @Nullable Key assetId;
    private @Nullable Key cameraOverlay;
    private @Nullable RegistryKeySet<EntityType> allowedEntities;
    private boolean dispensable = true;
    private boolean swappable = true;
    private boolean damageOnHurt = true;
    private boolean equipOnInteract = false;
    private boolean canBeSheared = false;
    private @Nullable Key shearSound;

    public static EquippableComponent fromMinecraftComponent(Equippable equippable) {
        return new EquippableComponent(equippable.slot(), equippable.equipSound(), equippable.assetId(),
                equippable.cameraOverlay(), equippable.allowedEntities(), equippable.dispensable(),
                equippable.swappable(), equippable.damageOnHurt(), equippable.equipOnInteract(),
                equippable.canBeSheared(), equippable.shearSound());
    }

    @Override
    public void applyToItem(ItemStack item) {
        Equippable.Builder builder = Equippable.equippable(slot)
                .equipSound(equipSound)
                .assetId(assetId)
                .cameraOverlay(cameraOverlay)
                .dispensable(dispensable)
                .swappable(swappable)
                .damageOnHurt(damageOnHurt)
                .equipOnInteract(equipOnInteract)
                .canBeSheared(canBeSheared)
                .shearSound(shearSound);

        if (allowedEntities != null) {
            builder.allowedEntities(allowedEntities);
        }

        item.setData(DataComponentTypes.EQUIPPABLE, builder.build());
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("slot", slot.toString());
        if (equipSound != null) {
            cs.set("equipSound", equipSound.asString());
        }
        if (assetId != null) {
            cs.set("assetId", assetId.asString());
        }
        if (cameraOverlay != null) {
            cs.set("cameraOverlay", cameraOverlay.asString());
        }

        if (allowedEntities != null) {
            cs.set("allowedEntities", allowedEntities.values().stream().map(k -> k.key().asString()).toList());
        }

        cs.set("dispensable", dispensable);
        cs.set("swappable", swappable);
        cs.set("damageOnHurt", damageOnHurt);
        cs.set("equipOnInteract", equipOnInteract);
        cs.set("canBeSheared", canBeSheared);

        if (shearSound != null) {
            cs.set("shearSound", shearSound.asString());
        }
    }

    public static EquippableComponent readFromSection(ConfigurationSection cs) {
        String slotStr = cs.getString("slot", "null");
        EquipmentSlot slot = EnumUtils.readEnum(EquipmentSlot.class, slotStr);
        if (slot == null) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot find a equipment slot with name " + slotStr);
            return null;
        }

        Key equipSound = null;
        String equipSoundStr = cs.getString("equipSound");
        if (equipSoundStr != null) {
            NamespacedKey key = BukkitUtils.getNamespacedKey(equipSoundStr);
            if (key != null) {
                equipSound = key;
            }
        }

        Key assetId = null;
        String assetIdStr = cs.getString("assetId");
        if (assetIdStr != null) {
            NamespacedKey key = BukkitUtils.getNamespacedKey(assetIdStr);
            if (key != null) {
                assetId = key;
            }
        }

        Key cameraOverlay = null;
        String cameraOverlayStr = cs.getString("cameraOverlay");
        if (cameraOverlayStr != null) {
            NamespacedKey key = BukkitUtils.getNamespacedKey(cameraOverlayStr);
            if (key != null) {
                cameraOverlay = key;
            }
        }

        RegistryKeySet<EntityType> allowedEntities = null;
        List<String> entities = cs.getStringList("allowedEntities");
        if (!entities.isEmpty()) {
            List<TypedKey<EntityType>> keys = BukkitUtils.getNamespacedKeys(entities)
                    .stream()
                    .map(RegistryKey.ENTITY_TYPE::typedKey)
                    .toList();

            if (!keys.isEmpty()) {
                allowedEntities = RegistrySet.keySet(RegistryKey.ENTITY_TYPE, keys);
            }
        }

        boolean dispensable = cs.getBoolean("dispensable", true);
        boolean swappable = cs.getBoolean("swappable", true);
        boolean damageOnHurt = cs.getBoolean("damageOnHurt", true);
        boolean equipOnInteract = cs.getBoolean("equipOnInteract", false);
        boolean canBeSheared = cs.getBoolean("canBeSheared", false);

        Key shearSound = null;
        String shearSoundStr = cs.getString("shearSound");
        if (shearSoundStr != null) {
            NamespacedKey key = BukkitUtils.getNamespacedKey(shearSoundStr);
            if (key != null) {
                shearSound = key;
            }
        }

        return new EquippableComponent(slot, equipSound, assetId, cameraOverlay, allowedEntities,
                dispensable, swappable, damageOnHurt, equipOnInteract, canBeSheared, shearSound);
    }
}
