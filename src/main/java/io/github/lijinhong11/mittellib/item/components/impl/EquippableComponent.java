/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.EnumUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "equippable")
@RequiredArgsConstructor
@AllArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class EquippableComponent extends ReadWriteItemComponent {
    private final @NotNull EquipmentSlot slot;
    private @Nullable Key equipSound;
    private @Nullable Key assetId;
    private @Nullable RegistryKeySet<EntityType> allowedEntities;
    private boolean dispensable = true;
    private boolean swappable = true;
    private boolean damageOnHurt = true;
    private @Nullable Key cameraOverlay;
    private boolean equipOnInteract = false;
    private boolean canBeSheared = false;
    private @Nullable Key shearSound;

    public static EquippableComponent fromMinecraftComponent(Equippable equippable) {
        return new EquippableComponent(
                equippable.slot(),
                equippable.equipSound(),
                equippable.assetId(),
                equippable.allowedEntities(),
                equippable.dispensable(),
                equippable.swappable(),
                equippable.damageOnHurt(),
                equippable.cameraOverlay(),
                equippable.equipOnInteract(),
                equippable.canBeSheared(),
                equippable.shearSound());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.EQUIPPABLE;
    }

    public static EquippableComponent readFromSection(ConfigurationSection cs) {
        String slotStr = cs.getString("slot", "null");
        EquipmentSlot slot = EnumUtils.readEnum(EquipmentSlot.class, slotStr);
        if (slot == null) {
            MittelLib.getInstance().getLogger().severe("Failed to find a equipment slot with name " + slotStr);
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
            List<TypedKey<EntityType>> keys = BukkitUtils.getNamespacedKeys(entities).stream()
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

        return new EquippableComponent(
                slot,
                equipSound,
                assetId,
                allowedEntities,
                dispensable,
                swappable,
                damageOnHurt,
                cameraOverlay,
                equipOnInteract,
                canBeSheared,
                shearSound);
    }

    @Override
    public void applyToItem(ItemStack item) {
        Equippable.Builder builder =
                Equippable.equippable(slot).equipSound(equipSound).dispensable(dispensable);

        builder.assetId(assetId);
        builder.cameraOverlay(cameraOverlay).swappable(swappable).damageOnHurt(damageOnHurt);
        builder.equipOnInteract(equipOnInteract);
        builder.canBeSheared(canBeSheared).shearSound(shearSound);

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
            cs.set(
                    "allowedEntities",
                    allowedEntities.values().stream()
                            .map(k -> k.key().asString())
                            .toList());
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
}
