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
package io.github.lijinhong11.mittellib.gui.inventory.choosers;

import io.github.lijinhong11.mittellib.gui.inventory.MittelGUI;
import io.github.lijinhong11.mittellib.gui.inventory.impl.PaginatedChestGUI;
import io.github.lijinhong11.mittellib.gui.inventory.item.MittelGUIItem;
import io.github.lijinhong11.mittellib.utils.components.ComponentUtils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class BiomeChooser {
    private static final BiPredicate<String, MittelGUIItem> LOOKUP = (query, item) -> item
                    instanceof BiomeItem biomeItem
            && biomeItem.biomeKey().asString().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));

    private BiomeChooser() {}

    public static void openBiomeChooser(@NotNull Player player, @NotNull Consumer<Biome> biomeConsumer) {
        PaginatedChestGUI gui = ChooserCommons.buildGUI(player, LOOKUP);

        Registry<Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);

        gui.setPageItems(registry.stream()
                .map(biome -> new BiomeItem(getBiomeIcon(biome), biome, biome.getKey(), player, biomeConsumer))
                .toList());
        gui.open(player);
    }

    private static @NotNull ItemStack getBiomeIcon(@NotNull Biome biome) {
        NamespacedKey key = biome.getKey();
        Material icon =
                switch (key.getKey()) {
                    case "badlands", "eroded_badlands", "wooded_badlands" -> Material.TERRACOTTA;
                    case "bamboo_jungle" -> Material.BAMBOO;
                    case "basalt_deltas" -> Material.BASALT;
                    case "beach" -> Material.SAND;
                    case "birch_forest", "old_growth_birch_forest" -> Material.BIRCH_SAPLING;
                    case "cherry_grove" -> Material.CHERRY_SAPLING;
                    case "cold_ocean", "deep_cold_ocean" -> Material.COD;
                    case "crimson_forest" -> Material.CRIMSON_STEM;
                    case "dark_forest" -> Material.DARK_OAK_SAPLING;
                    case "deep_dark" -> Material.SCULK;
                    case "deep_frozen_ocean" -> Material.PACKED_ICE;
                    case "deep_lukewarm_ocean" -> Material.PRISMARINE;
                    case "deep_ocean" -> Material.DARK_PRISMARINE;
                    case "desert" -> Material.CACTUS;
                    case "dripstone_caves" -> Material.POINTED_DRIPSTONE;
                    case "end_barrens", "end_highlands", "end_midlands", "small_end_islands", "the_end" ->
                        Material.END_STONE;
                    case "flower_forest" -> Material.DANDELION;
                    case "forest", "windswept_forest" -> Material.OAK_SAPLING;
                    case "frozen_ocean", "frozen_river", "ice_spikes" -> Material.ICE;
                    case "frozen_peaks", "jagged_peaks", "snowy_slopes" -> Material.SNOW_BLOCK;
                    case "grove", "snowy_taiga" -> Material.SPRUCE_SAPLING;
                    case "jungle", "sparse_jungle" -> Material.JUNGLE_SAPLING;
                    case "lukewarm_ocean" -> Material.PUFFERFISH;
                    case "lush_caves" -> Material.MOSS_BLOCK;
                    case "mangrove_swamp" -> Material.MANGROVE_PROPAGULE;
                    case "meadow" -> Material.CORNFLOWER;
                    case "mushroom_fields" -> Material.RED_MUSHROOM;
                    case "nether_wastes" -> Material.NETHERRACK;
                    case "ocean", "river" -> Material.WATER_BUCKET;
                    case "old_growth_pine_taiga", "old_growth_spruce_taiga", "taiga" -> Material.SPRUCE_LOG;
                    case "pale_garden" -> Material.PALE_OAK_SAPLING;
                    case "plains" -> Material.GRASS_BLOCK;
                    case "savanna", "savanna_plateau", "windswept_savanna" -> Material.ACACIA_SAPLING;
                    case "snowy_beach", "snowy_plains" -> Material.SNOW;
                    case "soul_sand_valley" -> Material.SOUL_SAND;
                    case "stony_peaks", "stony_shore", "windswept_hills" -> Material.STONE;
                    case "sunflower_plains" -> Material.SUNFLOWER;
                    case "swamp" -> Material.LILY_PAD;
                    case "the_void" -> Material.BARRIER;
                    case "warm_ocean" -> Material.TROPICAL_FISH;
                    case "warped_forest" -> Material.WARPED_STEM;
                    case "windswept_gravelly_hills" -> Material.GRAVEL;
                    default -> Material.PAPER;
                };

        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ComponentUtils.text(key.asString()));
        item.setItemMeta(meta);
        return item;
    }

    private record BiomeItem(
            @NotNull ItemStack item,
            @NotNull Biome biome,
            @NotNull NamespacedKey biomeKey,
            @NotNull Player player,
            @NotNull Consumer<Biome> biomeConsumer)
            implements MittelGUIItem {
        @Override
        public @NotNull ItemStack getItem() {
            return item;
        }

        @Override
        public boolean onClick(@NotNull MittelGUI gui, @NotNull InventoryClickEvent event) {
            biomeConsumer.accept(biome);
            player.closeInventory();
            return false;
        }
    }
}
