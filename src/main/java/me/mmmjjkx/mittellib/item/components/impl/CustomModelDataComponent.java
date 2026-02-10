package me.mmmjjkx.mittellib.item.components.impl;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.item.components.internal.ItemComponentSpec;
import me.mmmjjkx.mittellib.utils.MCVersion;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ItemComponentSpec(key = "modelData", requiredVersion = MCVersion.V1_21_4)
@RequiredArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unchecked")
public class CustomModelDataComponent extends ReadWriteItemComponent {
    private List<Float> floats = new ArrayList<>();
    private List<Boolean> flags = new ArrayList<>();
    private List<String> strings = new ArrayList<>();
    private List<Color> colors = new ArrayList<>();

    public static CustomModelDataComponent fromMinecraftComponent(CustomModelData customModelData) {
        return new CustomModelDataComponent(customModelData.floats(), customModelData.flags(), customModelData.strings(), customModelData.colors());
    }

    @NotNull
    public static CustomModelDataComponent readFromSection(ConfigurationSection cs) {
        List<Float> floats = cs.getFloatList("floats");
        List<Boolean> flags = cs.getBooleanList("flags");
        List<String> strings = cs.getStringList("strings");

        List<Color> colors = new ArrayList<>();
        List<Map<?, ?>> colorMaps = cs.getMapList("colors");
        for (Map<?, ?> color : colorMaps) {
            Map<String, Integer> colorMap = (Map<String, Integer>) color;
            Color bukkit = Color.fromARGB(colorMap.getOrDefault("alpha", 255), colorMap.get("red"), colorMap.get("green"), colorMap.get("blue"));
            colors.add(bukkit);
        }

        return new CustomModelDataComponent(floats, flags, strings, colors);
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

        if (this.colors != null) {
            List<Map<String, Integer>> colors = this.colors.stream()
                    .filter(Objects::nonNull)
                    .map(c -> {
                        Map<String, Integer> color = new HashMap<>();
                        color.put("alpha", c.getAlpha());
                        color.put("red", c.getRed());
                        color.put("green", c.getGreen());
                        color.put("blue", c.getBlue());
                        return color;
                    }).toList();

            cs.set("colors", colors);
        }
    }

    public void applyToMeta(ItemMeta meta) {
        var cmd = meta.getCustomModelDataComponent();
        cmd.setFloats(floats);
        cmd.setFlags(flags);
        cmd.setStrings(strings);
        cmd.setColors(colors);

        meta.setCustomModelDataComponent(cmd);
    }
}
