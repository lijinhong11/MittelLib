package me.mmmjjkx.mittellib.item.components.impl;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.mmmjjkx.mittellib.MittelLib;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.item.components.internal.ItemComponentSpec;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.EnumUtils;
import me.mmmjjkx.mittellib.utils.enums.MCVersion;
import me.mmmjjkx.mittellib.utils.NumberUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ItemComponentSpec(key = "consumable", requiredVersion = MCVersion.V1_21_2)
@NoArgsConstructor
@AllArgsConstructor
public class ConsumableComponent extends ReadWriteItemComponent {
    private @NonNegative float consumeSeconds = 1.6f;
    private ItemUseAnimation animation = ItemUseAnimation.EAT;
    private Key sound = NamespacedKey.minecraft("entity.generic.eat");
    private boolean hasConsumeParticles = false;
    private List<ConsumeEffect> effects = new ArrayList<>();

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

        if (effects != null && !effects.isEmpty()) {
            ComponentCommons.writeConsumeEffects(effects, cs);
        }
    }

    public static @NotNull ConsumableComponent readFromSection(ConfigurationSection cs) {
        float consumeSeconds = NumberUtils.asUnsigned((float) cs.getDouble("consumeSeconds", 1.6));

        String animationStr = cs.getString("animation", "EAT");
        ItemUseAnimation animation = EnumUtils.readEnum(ItemUseAnimation.class, animationStr);
        if (animation == null) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot find a item use animation with the name " + animationStr + "! Fallback to EAT");
            animation = ItemUseAnimation.EAT;
        }

        Key sound = BukkitUtils.getNamespacedKey(cs.getString("sound", "minecraft:entity.generic.eat"));
        boolean hasConsumeParticles = cs.getBoolean("hasConsumeParticles", true);

        List<ConsumeEffect> effects = ComponentCommons.readConsumeEffects(cs);

        return new ConsumableComponent(consumeSeconds, animation, sound, hasConsumeParticles, effects);
    }
}
