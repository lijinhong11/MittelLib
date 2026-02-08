package me.mmmjjkx.mittellib.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.mmmjjkx.mittellib.MittelLib;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.configuration.ReadWriteObject;
import me.mmmjjkx.mittellib.hook.ContentProvider;
import me.mmmjjkx.mittellib.hook.ContentProviders;
import me.mmmjjkx.mittellib.item.components.ItemComponentSerializer;
import me.mmmjjkx.mittellib.utils.EnumUtils;
import me.mmmjjkx.mittellib.utils.MCVersion;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class MittelItem extends ReadWriteObject {
    private @Nullable ContentProvider provider = null;
    private @Nullable String itemIdByProvider = null;
    private @NotNull Material material = Material.BARRIER;
    private @NotNull MittelItemMeta meta = MittelItemMeta.empty();
    private int amount = 1;

    private @Nullable List<Enchantment> enchantments = new ArrayList<>();
    private @Nullable List<ReadWriteItemComponent> components = new ArrayList<>();

    private MittelItem() {
    }

    public MittelItem(ContentProvider provider, String itemIdByProvider) {
        this.provider = provider;
        this.itemIdByProvider = itemIdByProvider;

        ItemStack get = provider.getItem(itemIdByProvider);
        if (get == null) {
            throw new RuntimeException(new IllegalArgumentException("Cannot find a item with id " + itemIdByProvider + " at " + provider.toString()));
        }
    
        applyFromItemStack(get);
    }

    public MittelItem(ItemStack itemStack) {
        applyFromItemStack(itemStack);
    }

    public MittelItem(Material material) {
        this.material = material;
    }

    public static MittelItem readFromSection(ConfigurationSection cs) {
        MittelItem item = new MittelItem();
        item.read(cs);
        return item;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public MittelItem applyFromItemStack(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            throw new RuntimeException(new IllegalArgumentException("Only allow items which have item meta"));
        }

        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.meta = MittelItemMeta.fromItemStack(itemStack);

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
            ContentProvider contentProvider = ContentProviders.getByName(provider);
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

        if (Objects.requireNonNull(MCVersion.getCurrent()).isAtLeast(MCVersion.V1_20_5)) {
            for (ReadWriteItemComponent component : components) {
                component.applyToItem(newOne);
            }
        }

        return newOne;
    }
}
