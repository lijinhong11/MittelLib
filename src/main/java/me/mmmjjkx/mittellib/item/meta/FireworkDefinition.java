package me.mmmjjkx.mittellib.item.meta;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
public class FireworkDefinition extends ReadWriteObject {
    private final List<FireworkEffect> effects;
    @Setter
    private int power = 0;

    public static FireworkDefinition empty() {
        return new FireworkDefinition(new ArrayList<>());
    }

    public static FireworkDefinition fromFireworkMeta(FireworkMeta meta) {
        return new FireworkDefinition(meta.getEffects(), meta.getPower());
    }

    @Override
    public void write(ConfigurationSection cs) {
    }

    @Override
    public void read(ConfigurationSection cs) {
    }
}
