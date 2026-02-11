package me.mmmjjkx.mittellib.item;

import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import me.mmmjjkx.mittellib.item.meta.BannerDefinition;
import me.mmmjjkx.mittellib.item.meta.FireworkDefinition;
import me.mmmjjkx.mittellib.item.meta.MapDefinition;
import me.mmmjjkx.mittellib.item.meta.SkullDefinition;
import me.mmmjjkx.mittellib.utils.EnumUtils;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MittelItemMeta extends ReadWriteObject {
    private @Nullable Set<ItemFlag> itemFlags;

    private @Nullable BannerDefinition banner;
    private @Nullable SkullDefinition skull;
    private @Nullable FireworkDefinition firework;
    private @Nullable MapDefinition map;
    private @Nullable Color leatherArmorColor;

    public static MittelItemMeta empty() {
        return new MittelItemMeta();
    }

    public static MittelItemMeta fromItemStack(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return empty();
        }

        MittelItemMeta newOne = empty();
        newOne.itemFlags = meta.getItemFlags().isEmpty() ? null : new HashSet<>(meta.getItemFlags());

        if (meta instanceof BannerMeta bannerMeta) {
            newOne.banner = BannerDefinition.fromBannerMeta(bannerMeta);
        }

        if (meta instanceof FireworkMeta fireworkMeta) {
            newOne.firework = FireworkDefinition.fromFireworkMeta(fireworkMeta);
        }

        if (meta instanceof MapMeta mapMeta) {
            newOne.map = MapDefinition.fromMapMeta(mapMeta);
        }

        if (meta instanceof SkullMeta skullMeta) {
            newOne.skull = SkullDefinition.fromSkullMeta(skullMeta);
        }

        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            newOne.leatherArmorColor = leatherArmorMeta.getColor();
        }

        return newOne;
    }

    private MittelItemMeta() {
    }

    public void applyToItemStack(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        if (itemFlags != null) {
            meta.removeItemFlags(ItemFlag.values());
            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        }

        if (meta instanceof BannerMeta bm && banner != null) {
            banner.applyTo(bm);
        }

        if (meta instanceof FireworkMeta fm && firework != null) {
            firework.applyTo(fm);
        }

        if (meta instanceof LeatherArmorMeta lam && leatherArmorColor != null) {
            lam.setColor(leatherArmorColor);
        }

        item.setItemMeta(meta);
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (itemFlags != null && !itemFlags.isEmpty()) {
            cs.set("itemFlags", itemFlags.stream().map(Enum::name).toList());
        }

        if (banner != null) {
            ConfigurationSection bannerSection = cs.createSection("banner");
            banner.write(bannerSection);
        }

        if (skull != null) {
            ConfigurationSection skullSection = cs.createSection("skull");
            skull.write(skullSection);
        }

        if (firework != null) {
            ConfigurationSection fireworkSection = cs.createSection("firework");
            firework.write(fireworkSection);
        }

        if (map != null) {
            ConfigurationSection mapSection = cs.createSection("map");
            map.write(mapSection);
        }

        if (leatherArmorColor != null) {
            ConfigurationSection colorSection = cs.createSection("leatherArmorColor");
            colorSection.set("alpha", leatherArmorColor.getAlpha());
            colorSection.set("red", leatherArmorColor.getRed());
            colorSection.set("green", leatherArmorColor.getGreen());
            colorSection.set("blue", leatherArmorColor.getBlue());
        }
    }

    @Override
    public void read(ConfigurationSection cs) {
        if (cs.contains("itemFlags")) {
            itemFlags = cs.getStringList("itemFlags").stream()
                    .map(s -> EnumUtils.readEnum(ItemFlag.class, s))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (itemFlags.isEmpty()) {
                itemFlags = null;
            }
        }

        ConfigurationSection bannerSection = cs.getConfigurationSection("banner");
        if (bannerSection != null) {
            banner = BannerDefinition.empty();
            banner.read(bannerSection);
        }

        ConfigurationSection skullSection = cs.getConfigurationSection("skull");
        if (skullSection != null) {
            skull = SkullDefinition.empty();
            skull.read(skullSection);
        }

        ConfigurationSection fireworkSection = cs.getConfigurationSection("firework");
        if (fireworkSection != null) {
            firework = FireworkDefinition.empty();
            firework.read(fireworkSection);
        }

        ConfigurationSection mapSection = cs.getConfigurationSection("map");
        if (mapSection != null) {
            map = MapDefinition.empty();
            map.read(mapSection);
        }

        ConfigurationSection colorSection = cs.getConfigurationSection("leatherArmorColor");
        if (colorSection != null) {
            int alpha = colorSection.getInt("alpha", 255);
            int red = colorSection.getInt("red", 0);
            int green = colorSection.getInt("green", 0);
            int blue = colorSection.getInt("blue", 0);
            leatherArmorColor = Color.fromARGB(alpha, red, green, blue);
        }
    }
}
