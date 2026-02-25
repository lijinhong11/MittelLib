package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SeededContainerLoot;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@ItemComponentSpec(key = "containerLoot", requiredVersion = MCVersion.V1_20_5)
@RequiredArgsConstructor
@AllArgsConstructor
public class ContainerLootComponent extends ReadWriteItemComponent {
    private final @NotNull Key lootTable;
    private long seed = 0L;

    public static ContainerLootComponent fromMinecraftComponent(SeededContainerLoot containerLoot) {
        return new ContainerLootComponent(containerLoot.lootTable(), containerLoot.seed());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.CONTAINER_LOOT;
    }

    @Override
    public void applyToItem(ItemStack item) {
        SeededContainerLoot loot = (seed == 0L)
                ? SeededContainerLoot.seededContainerLoot(lootTable).build()
                : SeededContainerLoot.seededContainerLoot(lootTable, seed);

        item.setData(DataComponentTypes.CONTAINER_LOOT, loot);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("lootTable", lootTable.asString());

        if (seed != 0) {
            cs.set("seed", seed);
        }
    }

    public static ContainerLootComponent readFromSection(ConfigurationSection cs) {
        Key key = BukkitUtils.getNamespacedKey(cs.getString("lootTable"));
        if (key == null) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Failed to define a container loot component: the loot table key is null");
            return null;
        }

        long seed = cs.getLong("seed", 0);

        return new ContainerLootComponent(key, seed);
    }
}
