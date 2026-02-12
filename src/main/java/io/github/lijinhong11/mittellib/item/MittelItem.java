package io.github.lijinhong11.mittellib.item;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.configuration.ReadWriteObject;
import io.github.lijinhong11.mittellib.hook.ContentProviders;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSerializer;
import io.github.lijinhong11.mittellib.utils.EnumUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class MittelItem extends ReadWriteObject {
    private @Nullable ContentProvider provider = null;
    private @Nullable String itemIdByProvider = null;
    private @NotNull Material material = Material.BARRIER;
    private @NotNull MittelItemMeta meta = MittelItemMeta.empty();
    private int amount = 1;

    private @Nullable Map<Enchantment, Integer> enchantments = new HashMap<>();
    private @Nullable List<ReadWriteItemComponent> components = new ArrayList<>();

    private MittelItem() {
    }

    public MittelItem(@NotNull ContentProvider provider, @NotNull String itemIdByProvider) {
        Preconditions.checkNotNull(provider);
        Preconditions.checkNotNull(itemIdByProvider);

        ItemStack get = provider.getItem(itemIdByProvider);
        if (get == null) {
            throw new RuntimeException(new IllegalArgumentException("Cannot find a item with id " + itemIdByProvider + " at " + provider));
        }

        this.provider = provider;
        this.itemIdByProvider = itemIdByProvider;

        applyFromItemStack(get);
    }

    public MittelItem(@NotNull ItemStack itemStack) {
        applyFromItemStack(itemStack);
    }

    public MittelItem(@NotNull Material material) {
        this(material, 1);
    }

    public MittelItem(@NotNull Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public static MittelItem readFromSection(ConfigurationSection cs) {
        MittelItem item = new MittelItem();
        item.read(cs);
        return item;
    }

    @Contract(value = "_ -> this", mutates = "this")
    @CanIgnoreReturnValue
    public MittelItem applyFromItemStack(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            throw new RuntimeException(new IllegalArgumentException("Only allow items which have item meta"));
        }

        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.meta = MittelItemMeta.fromItemStack(itemStack);
        this.enchantments = itemStack.getEnchantments();

        if (MCVersion.getCurrent().isAtLeast(MCVersion.V1_20_5)) {
            this.components = ItemComponentSerializer.readComponentsFromItem(itemStack);
        }

        return this;
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (provider != null) {
            cs.set("provider", provider.toString());
            cs.set("material", itemIdByProvider);
        } else {
            cs.set("material", material.toString());
        }

        cs.set("amount", amount);

        if (enchantments != null && !enchantments.isEmpty()) {
            ConfigurationSection enchant = cs.createSection("enchantments");
            for (Map.Entry<Enchantment, Integer> ench : enchantments.entrySet()) {
                enchant.set(ench.getKey().key().asString(), ench.getValue());
            }
        }

        meta.write(cs.createSection("meta"));

        if (components != null && !components.isEmpty() && MCVersion.getCurrent().isAtLeast(MCVersion.V1_20_5)) {
            ConfigurationSection componentsSection = cs.createSection("components");
            ItemComponentSerializer.writeComponentsToConfiguration(components, componentsSection);
        }
    }

    @Override
    public void read(ConfigurationSection cs) {
        Material material = Material.BARRIER;

        if (cs.contains("provider")) {
            String provider = cs.getString("provider");
            ContentProvider contentProvider = ContentProviders.getById(provider);
            if (contentProvider != null) {
                String id = cs.getString("id", "null");
                ItemStack item = contentProvider.getItem(id);
                if (item == null) {
                    MittelLib.getInstance()
                            .getLogger()
                            .severe("Cannot find a item with id " + id + " at "
                                    + cs.getCurrentPath());
                } else {
                    this.provider = contentProvider;
                    applyFromItemStack(item);
                }
            } else {
                MittelLib.getInstance()
                        .getLogger()
                        .severe("Cannot find a content provider called " + provider + " at "
                                + cs.getCurrentPath());
            }
        } else {
            String mat = cs.getString("material", "null");
            material = EnumUtils.readEnum(Material.class, mat);
            if (material == null) {
                MittelLib.getInstance()
                        .getLogger()
                        .severe("Cannot find a material called " + mat + " at "
                                + cs.getCurrentPath());
                material = Material.BARRIER;
            }
        }

        int amount = cs.getInt("amount");
        if (amount < 1) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("the item amount " + amount + " must not lower than 1 (at "
                            + cs.getCurrentPath() + ")");
            amount = 1;
        }

        ConfigurationSection components = cs.getConfigurationSection("components");
        if (components != null && MCVersion.getCurrent().isAtLeast(MCVersion.V1_20_5)) {
            this.components = ItemComponentSerializer.readComponentsFromSection(components);
        }

        ConfigurationSection metaSection = cs.getConfigurationSection("meta");
        if (metaSection != null) {
            meta.read(metaSection);
        }

        this.material = material;
        this.amount = amount;
    }

    public ItemStack get() {
        if (amount <= 0) {
            throw new RuntimeException(new IllegalArgumentException("amount must greater than 0"));
        }

        ItemStack newOne = new ItemStack(material, amount);

        meta.applyToItemStack(newOne);

        if (MCVersion.getCurrent().isAtLeast(MCVersion.V1_20_5) && components != null) {
            for (ReadWriteItemComponent component : components) {
                component.applyToItem(newOne);
            }
        }

        return newOne;
    }

    @CanIgnoreReturnValue
    public MittelItem component(@NotNull ReadWriteItemComponent component) {
        if (MCVersion.getCurrent().isAtLeast(MCVersion.V1_20_5)) {
            if (components == null) {
                components = new ArrayList<>();
            }

            components.add(component);
        }

        return this;
    }

    @CanIgnoreReturnValue
    public MittelItem enchant(@NotNull Enchantment enchantment, int lvl) {
        if (enchantments == null) {
            enchantments = new HashMap<>();
        }

        enchantments.put(enchantment, lvl);

        return this;
    }
}
