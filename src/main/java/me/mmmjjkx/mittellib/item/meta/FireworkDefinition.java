package me.mmmjjkx.mittellib.item.meta;

import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.FireworkMeta;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FireworkDefinition extends ReadWriteObject {
    private final List<FireworkEffect> fireworkEffects;
    private final @NonNegative int power;

    public static FireworkDefinition empty() {
        return new FireworkDefinition(new ArrayList<>(), 0);
    }

    public static FireworkDefinition fromFireworkMeta(FireworkMeta meta) {
        return new FireworkDefinition(meta.getEffects(), meta.hasPower() ? meta.getPower() : 0);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("power", power);
    }

    @Override
    public void read(ConfigurationSection cs) {
    }

    @Nullable
    public static FireworkDefinition readFromSection(ConfigurationSection cs) {
        int power = cs.getInt("power");
        if (power < 0) {
            return null;
        }

        
    }
}
