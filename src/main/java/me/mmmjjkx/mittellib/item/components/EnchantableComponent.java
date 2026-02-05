package me.mmmjjkx.mittellib.item.components;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.Positive;

@AllArgsConstructor
public class EnchantableComponent extends ReadWriteItemComponent {
    private final @Positive int value;

    public static EnchantableComponent fromMinecraftComponent(Enchantable enchantable) {
        return new EnchantableComponent(enchantable.value());
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(value));
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("enchantable", value);
    }

    public static EnchantableComponent readFromSection(ConfigurationSection cs) {
        int value = cs.getInt("enchantable");
        if (value < 1) {
            return null;
        }

        return new EnchantableComponent(value);
    }
}
