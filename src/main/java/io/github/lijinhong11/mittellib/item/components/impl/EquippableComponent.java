package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.EnumUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

@ItemComponentSpec(key = "equippable", requiredVersion = MCVersion.V1_21_2)
@RequiredArgsConstructor
@AllArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class EquippableComponent extends ReadWriteItemComponent {
    private static MethodHandle ASSET_SET;

    static {
        MethodType mt = MethodType.methodType(Equippable.Builder.class, Key.class);
        try {
            ASSET_SET = MethodHandles.publicLookup().findVirtual(Equippable.Builder.class, "assetId", mt);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            try {
                ASSET_SET = MethodHandles.publicLookup().findVirtual(Equippable.Builder.class, "model", mt);
            } catch (NoSuchMethodException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private final @NotNull EquipmentSlot slot;
    private @Nullable Key equipSound;
    private @Nullable Key assetId;
    private @Nullable RegistryKeySet<EntityType> allowedEntities;
    private boolean dispensable = true;

    //1.21.2
    private boolean swappable = true;
    private boolean damageOnHurt = true;
    private @Nullable Key cameraOverlay;

    //1.21.5
    private boolean equipOnInteract = false;

    //1.21.6
    private boolean canBeSheared = false;
    private @Nullable Key shearSound;

    //for 1.21.2 lower
    public EquippableComponent(
            @NotNull EquipmentSlot slot,
            @Nullable Key equipSound,
            @Nullable Key assetId,
            @Nullable RegistryKeySet<EntityType> allowedEntities,
            boolean dispensable
    ) {
        this(slot, equipSound, assetId, allowedEntities, dispensable, true, true, null, false, false, null);
    }

    //for 1.21.2 - 1.21.4
    public EquippableComponent(
            @NotNull EquipmentSlot slot,
            @Nullable Key equipSound,
            @Nullable Key assetId,
            @Nullable Key cameraOverlay,
            @Nullable RegistryKeySet<EntityType> allowedEntities,
            boolean dispensable,
            boolean swappable,
            boolean damageOnHurt
    ) {
        this(slot, equipSound, assetId, allowedEntities, dispensable, swappable, damageOnHurt, cameraOverlay, false, false, null);
    }

    public static EquippableComponent fromMinecraftComponent(Equippable equippable) {
        MCVersion current = MCVersion.getCurrent();

        if (current.isLowerThan(MCVersion.V1_21_2)) {
            return new EquippableComponent(equippable.slot(), equippable.equipSound(), equippable.assetId(), equippable.allowedEntities(), equippable.dispensable());
        }

        if (current.isAtLeast(MCVersion.V1_21_2) && current.isLowerThan(MCVersion.V1_21_5)) {
            return new EquippableComponent(equippable.slot(), equippable.equipSound(), equippable.assetId(), equippable.cameraOverlay(), equippable.allowedEntities(), equippable.dispensable(), equippable.swappable(), equippable.damageOnHurt());
        }

        return new EquippableComponent(equippable.slot(), equippable.equipSound(), equippable.assetId(), equippable.allowedEntities(), equippable.dispensable(), equippable.swappable(), equippable.damageOnHurt(), equippable.cameraOverlay(), equippable.equipOnInteract(), equippable.canBeSheared(), equippable.shearSound());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.EQUIPPABLE;
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

        return new EquippableComponent(slot, equipSound, assetId, allowedEntities, dispensable,
                swappable, damageOnHurt, cameraOverlay, equipOnInteract, canBeSheared, shearSound);
    }

    @Override
    public void applyToItem(ItemStack item) {
        Equippable.Builder builder = Equippable.equippable(slot)
                .equipSound(equipSound)
                .dispensable(dispensable);

        try {
            ASSET_SET.invoke(assetId);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        if (MCVersion.getCurrent().isAtLeast(MCVersion.V1_21_2)) {
            builder.cameraOverlay(cameraOverlay)
                    .swappable(swappable)
                    .damageOnHurt(damageOnHurt);
        }

        if (MCVersion.getCurrent().isAtLeast(MCVersion.V1_21_5)) {
            builder.equipOnInteract(equipOnInteract);
        }

        if (MCVersion.getCurrent().isAtLeast(MCVersion.V1_21_6)) {
            builder.canBeSheared(canBeSheared)
                    .shearSound(shearSound);
        }

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

        if (MCVersion.getCurrent().isAtLeast(MCVersion.V1_21_6)) {
            cs.set("canBeSheared", canBeSheared);

            if (shearSound != null) {
                cs.set("shearSound", shearSound.asString());
            }
        }
    }
}
