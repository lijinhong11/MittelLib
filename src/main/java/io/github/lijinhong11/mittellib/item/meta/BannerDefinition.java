package io.github.lijinhong11.mittellib.item.meta;

import io.github.lijinhong11.mittellib.configuration.ReadWriteObject;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.EnumUtils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class BannerDefinition extends ReadWriteObject {
    private static final Registry<PatternType> REGISTRY = RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.BANNER_PATTERN);

    private List<Pattern> bannerPatterns;

    public static BannerDefinition empty() {
        return new BannerDefinition(new ArrayList<>());
    }

    public static BannerDefinition fromBannerMeta(BannerMeta bannerMeta) {
        return new BannerDefinition(bannerMeta.getPatterns());
    }

    public BannerDefinition(ConfigurationSection cs) {
        super(cs);
    }

    @NotNull
    public Pattern getPattern(int index) {
        return bannerPatterns.get(index);
    }

    public void addPattern(Pattern pattern) {
        bannerPatterns.add(pattern);
    }

    public void setPattern(int index, Pattern pattern) {
        bannerPatterns.set(index, pattern);
    }

    public void removePattern(int index) {
        bannerPatterns.remove(index);
    }

    public int numberOfPatterns() {
        return bannerPatterns.size();
    }

    @Override
    public void write(ConfigurationSection cs) {
        List<String> stringPatterns = new ArrayList<>();
        for (Pattern pattern : bannerPatterns) {
            stringPatterns.add(pattern.getColor() + ";" + REGISTRY.getKey(pattern.getPattern()));
        }

        cs.set("patterns", stringPatterns);
    }

    @Override
    public void read(ConfigurationSection cs) {
        List<String> stringPatterns = cs.getStringList("patterns");
        if (stringPatterns.isEmpty()) {
            return;
        }

        bannerPatterns.clear();
        for (String s : stringPatterns) {
            String[] parts = s.split(";", 2);
            if (parts.length != 2) {
                continue;
            }

            DyeColor color = EnumUtils.readEnum(DyeColor.class, parts[0].trim());
            NamespacedKey key = BukkitUtils.getNamespacedKey(parts[1].trim());
            if (color == null || key == null) {
                continue;
            }

            PatternType type = REGISTRY.get(key);
            if (type == null) {
                continue;
            }

            bannerPatterns.add(new Pattern(color, type));
        }
    }

    public void applyTo(BannerMeta meta) {
        meta.setPatterns(bannerPatterns);
    }
}
