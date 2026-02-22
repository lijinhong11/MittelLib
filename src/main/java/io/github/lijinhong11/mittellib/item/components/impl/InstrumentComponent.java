package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.AllArgsConstructor;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "instrument", requiredVersion = MCVersion.V1_21_2)
@AllArgsConstructor
public class InstrumentComponent extends ReadWriteItemComponent {
    private final @NotNull MusicInstrument instrument;

    public static InstrumentComponent fromMinecraftComponent(MusicInstrument instrument) {
        return new InstrumentComponent(instrument);
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.INSTRUMENT;
    }

    @Nullable
    public static InstrumentComponent readFromSection(ConfigurationSection cs) {
        String keyStr = cs.getString("instrument", "null");
        NamespacedKey key = BukkitUtils.getNamespacedKey(keyStr);
        if (key == null) {
            return null;
        }

        MusicInstrument instrument = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).get(key);
        if (instrument == null) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot find a MusicInstrument with key " + key.asString());
            return null;
        }

        return new InstrumentComponent(instrument);
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.INSTRUMENT, instrument);
    }

    @Override
    public void write(ConfigurationSection cs) {
        NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).getKey(instrument);

        cs.set("instrument", key.asString());
    }
}

