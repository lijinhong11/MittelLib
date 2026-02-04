package me.mmmjjkx.mittellib.item.components;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
public class ConsumableComponent extends ReadWriteItemComponent {
    private final @NonNegative float consumeSeconds;
    private ItemUseAnimation animation;
    private Key sound;
    private boolean hasConsumeParticles;
    private List<ConsumeEffect> effects;

    public static ConsumableComponent fromMinecraftComponent(Consumable consumable) {
        return new ConsumableComponent(consumable.consumeSeconds(), consumable.animation(), consumable.sound(), consumable.hasConsumeParticles(), consumable.consumeEffects());
    }

    @Override
    public void applyToItem(ItemStack item) {
        Consumable consumable = Consumable.consumable()
                .consumeSeconds(consumeSeconds)
                .animation(animation)
                .sound(sound)
                .hasConsumeParticles(hasConsumeParticles)
                .effects(effects)
                .build();

        item.setData(DataComponentTypes.CONSUMABLE, consumable);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("consumeSeconds", consumeSeconds);
        cs.set("animation", animation.toString());
        cs.set("sound", sound.asString());
        cs.set("hasConsumeParticles", hasConsumeParticles);
    }

    @Override
    public void read(ConfigurationSection cs) {

    }
}
