package me.mmmjjkx.mittellib.item.components;

import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
public class DamageComponent extends ReadWriteItemComponent {
    private @NonNegative int damage = 0;

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.DAMAGE, damage);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("damage", damage);
    }

    @Nullable
    public static DamageComponent readFromSection(ConfigurationSection cs) {
        int damage = cs.getInt("damage");
        if (damage < 0) {
            return null;
        }

        return new DamageComponent(damage);
    }
}
