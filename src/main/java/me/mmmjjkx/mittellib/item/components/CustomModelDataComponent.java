package me.mmmjjkx.mittellib.item.components;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
public class CustomModelDataComponent extends ReadWriteItemComponent {
    private List<Float> floats = new ArrayList<>();
    private List<Boolean> flags = new ArrayList<>();
    private List<String> strings = new ArrayList<>();
    private List<Color> colors = new ArrayList<>();

    public static CustomModelDataComponent fromMinecraftComponent(CustomModelData customModelData) {
        return new CustomModelDataComponent(customModelData.floats(), customModelData.flags(), customModelData.strings(), customModelData.colors());
    }

    @Override
    public void applyToItem(ItemStack item) {
        CustomModelData cmd = CustomModelData.customModelData()
                .addFloats(floats)
                .addFlags(flags)
                .addStrings(strings)
                .addColors(colors)
                .build();

        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, cmd);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("floats", floats);
        cs.set("flags", flags);
        cs.set("strings", strings);
    }

    @Nullable
    public static CustomModelDataComponent readFromSection(ConfigurationSection cs) {

    }

    public void applyToMeta(ItemMeta meta) {
        var cmd = meta.getCustomModelDataComponent();
        cmd.setFloats(floats);
        cmd.setFlags(flags);
        cmd.setStrings(strings);
        cmd.setColors(colors);
    }
}
