/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.mittellib.item;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.configuration.ReadWriteObject;
import io.github.lijinhong11.mittellib.hook.ContentProviders;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSerializer;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.*;
import java.util.function.Consumer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = false)
@Data
public class MittelItem implements ReadWriteObject {
    private @Nullable ContentProvider itemProvider = null;
    private @Nullable String itemIdByProvider = null;
    private @NotNull Material material = Material.BARRIER;
    private @Nullable MittelItemMeta meta;
    private int amount = 1;

    private @Nullable Map<Enchantment, Integer> enchantments = new HashMap<>();
    private @Nullable List<ReadWriteItemComponent> components = new ArrayList<>();

    private MittelItem() {}

    /**
     * Create a mittel item using item provider and item id
     * @param itemProvider the item provider
     * @param itemIdByProvider the item id to get item in the item provider
     */
    public MittelItem(@NotNull ContentProvider itemProvider, @NotNull String itemIdByProvider) {
        Preconditions.checkNotNull(itemProvider);
        Preconditions.checkNotNull(itemIdByProvider);

        ItemStack get = itemProvider.getItem(itemIdByProvider);
        if (get == null) {
            throw new RuntimeException(new IllegalArgumentException(
                    "Failed to find a item with id " + itemIdByProvider + " at " + itemProvider.getId()));
        }

        this.itemProvider = itemProvider;
        this.itemIdByProvider = itemIdByProvider;

        applyFromItemStack(get);
    }

    /**
     * Create a mittel item using {@link ItemStack}
     * @param itemStack the item stack
     */
    public MittelItem(@NotNull ItemStack itemStack) {
        applyFromItemStack(itemStack);
    }

    /**
     * Create a mittel item using {@link ItemStack} but specify its amount
     * @param itemStack the item stack
     * @param amount the amount
     */
    public MittelItem(@NotNull ItemStack itemStack, int amount) {
        applyFromItemStack(itemStack.asQuantity(amount));
    }

    public MittelItem(@NotNull Material material) {
        this(material, 1);
    }

    public MittelItem(@NotNull Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    /**
     * Read the item and apply it from the configuration section
     * @param cs the configuration section
     * @return a mittel item
     * @throws IllegalArgumentException if the material doesn't support item metas
     */
    public static MittelItem readFromSection(ConfigurationSection cs) {
        MittelItem item = new MittelItem();
        item.read(cs);
        return item;
    }

    @Contract(value = "_ -> this", mutates = "this")
    @CanIgnoreReturnValue
    public MittelItem applyFromItemStack(ItemStack itemStack) {
        if (!itemStack.getType().isItem()) {
            throw new IllegalArgumentException("Only allow items which have item meta");
        }

        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.meta = MittelItemMeta.fromItemStack(itemStack);
        this.enchantments = itemStack.getEnchantments();
        this.components = ItemComponentSerializer.readComponentsFromItem(itemStack);

        return this;
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (itemProvider != null && itemIdByProvider != null) {
            cs.set("provider", itemProvider.getId().toLowerCase());
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

        if (meta != null) {
            meta.write(cs.createSection("meta"));
        }

        if (components != null && !components.isEmpty()) {
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
                            .severe("Failed to find a item with id " + id + " at " + cs.getCurrentPath());
                } else {
                    this.itemProvider = contentProvider;
                    applyFromItemStack(item);
                }
            } else {
                MittelLib.getInstance()
                        .getLogger()
                        .severe("Failed to find a content provider called " + provider + " at " + cs.getCurrentPath());
            }
        } else {
            String mat = cs.getString("material", "null");
            material = BukkitUtils.getMaterial(mat);
            if (material == null) {
                MittelLib.getInstance()
                        .getLogger()
                        .severe("Failed to find a material called " + mat + " at " + cs.getCurrentPath());
                material = Material.BARRIER;
            }
        }

        int amount = cs.getInt("amount");
        if (amount < 1) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("the item amount " + amount + " must not lower than 1 (at " + cs.getCurrentPath() + ")");
            amount = 1;
        }

        ConfigurationSection components = cs.getConfigurationSection("components");
        if (components != null) {
            this.components = ItemComponentSerializer.readComponentsFromSection(components);
        }

        ConfigurationSection metaSection = cs.getConfigurationSection("meta");
        if (metaSection != null) {
            if (this.meta == null) {
                this.meta = MittelItemMeta.empty();
            }

            this.meta.read(metaSection);
        }

        ConfigurationSection enchantmentSection = cs.getConfigurationSection("enchantments");
        if (enchantmentSection != null) {
            if (this.enchantments == null) {
                this.enchantments = new HashMap<>();
            }

            for (String k : enchantmentSection.getKeys(false)) {
                NamespacedKey key = BukkitUtils.getNamespacedKey(k);
                if (key == null) {
                    continue;
                }

                Enchantment ench = RegistryAccess.registryAccess()
                        .getRegistry(RegistryKey.ENCHANTMENT)
                        .get(key);
                if (ench == null) {
                    MittelLib.getInstance()
                            .getLogger()
                            .severe("Failed to find a enchantment with key " + key.asString());
                    continue;
                }

                int lvl = enchantmentSection.getInt(k);

                this.enchantments.put(ench, lvl);
            }
        }

        this.material = material;
        this.amount = amount;
    }

    public ItemStack get() {
        if (amount <= 0) {
            throw new RuntimeException(new IllegalArgumentException("amount must greater than 0"));
        }

        ItemStack newOne = null;
        if (itemProvider != null && itemIdByProvider != null) {
            newOne = itemProvider.getItem(itemIdByProvider);
        }

        if (newOne == null) {
            newOne = new ItemStack(material, amount);
        }

        if (meta != null) {
            meta.applyToItemStack(newOne);
        }

        if (components != null) {
            for (ReadWriteItemComponent component : components) {
                component.applyToItem(newOne);
            }
        }

        return newOne;
    }

    @CanIgnoreReturnValue
    public MittelItem component(@NotNull ReadWriteItemComponent component) {
        if (components == null) {
            components = new ArrayList<>();
        }

        components.add(component);

        return this;
    }

    /**
     * Set an item component into the item
     * @param dataComponentType the paper data component type. See {@link io.papermc.paper.datacomponent.DataComponentTypes}
     * @param dataComponentContext the paper data component context
     * @return the item itself
     */
    @CanIgnoreReturnValue
    public MittelItem component(@NotNull DataComponentType dataComponentType, @Nullable Object dataComponentContext) {
        if (components == null) {
            components = new ArrayList<>();
        }

        components.add(ItemComponentSerializer.getFromMinecraftComponent(dataComponentType, dataComponentContext));

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

    public MittelItemMeta getMeta() {
        if (meta == null) {
            meta = MittelItemMeta.empty();
        }

        return meta;
    }

    public MittelItem editMeta(Consumer<MittelItemMeta> consumer) {
        consumer.accept(getMeta());

        return this;
    }
}
