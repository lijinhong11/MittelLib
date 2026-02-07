package me.mmmjjkx.mittellib.item.components;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.item.MittelItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class UseRemainderComponent extends ReadWriteItemComponent {
    private final @NotNull ItemStack transfromInto;

    public static UseRemainderComponent fromMinecraftComponent(UseRemainder useRemainder) {
        return new UseRemainderComponent(useRemainder.transformInto());
    }

    @Override
    public void applyToItem(ItemStack item) {
        UseRemainder useRemainder = UseRemainder.useRemainder(transfromInto);
        item.setData(DataComponentTypes.USE_REMAINDER, useRemainder);
    }

    @Override
    public void write(ConfigurationSection cs) {
        MittelItem mittelItem = new MittelItem(transfromInto);
        ConfigurationSection transformInto = cs.createSection("transformInto");
        mittelItem.write(transformInto);
    }

    public static UseRemainderComponent readFromSection(ConfigurationSection cs) {
        ConfigurationSection itemSection = cs.getConfigurationSection("transformInto");
        if (itemSection == null) {
            return null;
        }

        MittelItem mittelItem = MittelItem.readFromSection(itemSection);
        return new UseRemainderComponent(mittelItem.get());
    }
}
