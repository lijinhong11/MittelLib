package io.github.lijinhong11.mittellib.item.components.impl;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import lombok.AllArgsConstructor;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.MittelItem;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@ItemComponentSpec(key = "useRemainder", requiredVersion = MCVersion.V1_21_2)
@AllArgsConstructor
public class UseRemainderComponent extends ReadWriteItemComponent {
    private final @NotNull ItemStack transfromInto;

    public static UseRemainderComponent fromMinecraftComponent(UseRemainder useRemainder) {
        return new UseRemainderComponent(useRemainder.transformInto());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.USE_REMAINDER;
    }

    public static UseRemainderComponent readFromSection(ConfigurationSection cs) {
        ConfigurationSection itemSection = cs.getConfigurationSection("transformInto");
        if (itemSection == null) {
            return null;
        }

        MittelItem mittelItem = MittelItem.readFromSection(itemSection);
        return new UseRemainderComponent(mittelItem.get());
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
}
