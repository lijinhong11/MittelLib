package me.mmmjjkx.mittellib.item.components;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.mmmjjkx.mittellib.MittelLib;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.EnumUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

        if (effects != null) {
            ConfigurationSection effectsSection = cs.createSection("effects");
            for (ConsumeEffect ce : effects) {
                if (ce instanceof ConsumeEffect.TeleportRandomly tr) {
                    effectsSection.set("teleportRandom", tr.diameter());
                }

                if (ce instanceof ConsumeEffect.PlaySound ps) {
                    effectsSection.set("sound", ps.sound().asString());
                }

                if (ce instanceof ConsumeEffect.ClearAllStatusEffects) {
                    effectsSection.set("clearAllEffects", true);
                }

                if (ce instanceof ConsumeEffect.ApplyStatusEffects ase) {
                    ConfigurationSection applyEffectsSection = effectsSection.createSection("applyEffects");
                    applyEffectsSection.set("probability", ase.probability());
                    applyEffectsSection.set("effects", ase.effects());
                }

                if (ce instanceof ConsumeEffect.RemoveStatusEffects rse) {
                    List<String> keys = rse.removeEffects().values().stream().map(k -> k.key().asString()).toList();
                    effectsSection.set("removeEffects", keys);
                }
            }
        }
    }

    @Nullable
    public static ConsumableComponent readFromSection(ConfigurationSection cs) {
        float consumeSeconds = (float) cs.getDouble("consumeSeconds", 1.6);

        if (consumeSeconds < 0) {
            return null;
        }

        String animationStr = cs.getString("animation", "EAT");
        ItemUseAnimation animation = EnumUtils.readEnum(ItemUseAnimation.class, animationStr);
        if (animation == null) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot find a item use animation with the name " + animationStr + "! Fallback to EAT");
        }

        Key sound = BukkitUtils.getNamespacedKey(cs.getString("sound", "minecraft:entity.generic.eat"));
        boolean hasConsumeParticles = cs.getBoolean("hasConsumeParticles", true);

        List<ConsumeEffect> effects = ItemComponentSerializer.readConsumeEffects(cs);

        return new ConsumableComponent(consumeSeconds, animation, sound, hasConsumeParticles, effects);
    }
}
