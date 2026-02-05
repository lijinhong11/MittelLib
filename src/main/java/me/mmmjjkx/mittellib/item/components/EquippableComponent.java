package me.mmmjjkx.mittellib.item.components;

import io.papermc.paper.datacomponent.item.Equippable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.utils.EnumUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@AllArgsConstructor
public class EquippableComponent extends ReadWriteItemComponent {
    private @NotNull final EquipmentSlot slot;
    private @Nullable Key equipSound;

    public static EquippableComponent fromMinecraftComponent(Equippable equippable) {
        return new EquippableComponent(equippable.slot());
    }

    @Override
    public void applyToItem(ItemStack item) {

    }

    @Override
    public void write(ConfigurationSection cs) {

    }

    public static EquippableComponent readFromSection(ConfigurationSection cs) {
        EquipmentSlot slot = EnumUtils.readEnum(EquipmentSlot.class, );
    }
}
