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
package io.github.lijinhong11.mittellib.hook;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.hook.content.MinecraftContentProvider;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
@UtilityClass
public final class ContentProviders {
    private static final Map<String, ContentProvider> contentProviders = new LinkedHashMap<>();
    private static volatile List<String> itemSuggestions = List.of();
    private static volatile List<String> blockSuggestions = List.of();
    private static volatile List<PackedBlock> usableBlocks = List.of();
    private static boolean reloadListenersRegistered;

    private static final List<ReloadBinding> RELOAD_BINDINGS = List.of(
            new ReloadBinding("Nexo", "com.nexomc.nexo.api.events.NexoItemsLoadedEvent", "nexo"),
            new ReloadBinding("Oraxen", "io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent", "oraxen"),
            new ReloadBinding("ItemsAdder", "dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent", "itemsadder"),
            new ReloadBinding(
                    "CraftEngine", "net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent", "craftengine"),
            new ReloadBinding(
                    "ExecutableItems",
                    "com.ssomar.score.api.executableitems.load.ExecutableItemsPostLoadEvent",
                    "executableitems"),
            new ReloadBinding("MMOItems", "net.Indyuce.mmoitems.api.event.MMOItemsReloadEvent", "mmoitems"),
            new ReloadBinding("MythicMobs", "io.lumine.mythic.bukkit.events.MythicReloadCompleteEvent", "mythicmobs"));

    public static void init() {
        contentProviders.clear();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            String name = plugin.getName() + "ContentProvider";
            try {
                Class<?> theClass = Class.forName("io.github.lijinhong11.mittellib.hook.content." + name);
                Constructor<? extends ContentProvider> constructor =
                        ((Class<? extends ContentProvider>) theClass).getConstructor();
                register(constructor.newInstance());
            } catch (Exception ignore) {
            }
        }

        register(new MinecraftContentProvider());
        rebuildCaches();
        registerReloadListeners();
    }

    @SuppressWarnings("unchecked")
    private static void registerReloadListeners() {
        if (reloadListenersRegistered || MittelLib.getInstance() == null) {
            return;
        }

        for (ReloadBinding binding : RELOAD_BINDINGS) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(binding.pluginName);
            if (plugin == null || !plugin.isEnabled() || getById(binding.providerId) == null) {
                continue;
            }

            try {
                Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(binding.eventClassName);
                EventExecutor executor = (_, _) -> refresh(binding.providerId);
                Bukkit.getPluginManager()
                        .registerEvent(
                                eventClass,
                                new ReloadListener(),
                                EventPriority.MONITOR,
                                executor,
                                MittelLib.getInstance());
            } catch (ClassNotFoundException ignored) {
                // The corresponding optional plugin or API is not installed.
            }
        }
        reloadListenersRegistered = true;
    }

    /**
     * Refresh all registered content providers and rebuild the lookup caches.
     * Call this after a content plugin reloads its custom items or blocks.
     */
    public static void refresh() {
        for (ContentProvider provider : contentProviders.values()) {
            provider.refresh();
        }
        rebuildCaches();
    }

    /**
     * Refresh one content provider and rebuild the lookup caches.
     *
     * @param id provider namespace, for example {@code nexo}
     * @return whether a provider with the given namespace was found
     */
    public static boolean refresh(@NotNull String id) {
        ContentProvider provider = getById(id);
        if (provider == null) {
            return false;
        }

        provider.refresh();
        rebuildCaches();
        return true;
    }

    private static void register(@NotNull ContentProvider provider) {
        contentProviders.put(normalize(provider.getId()), provider);
    }

    private static String normalize(@NotNull String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static void rebuildCaches() {
        Set<String> items = new LinkedHashSet<>();
        Set<String> blocks = new LinkedHashSet<>();
        List<PackedBlock> allUsableBlocks = new ArrayList<>();

        for (ContentProvider provider : contentProviders.values()) {
            items.addAll(provider.getItemSuggestions());
            blocks.addAll(provider.getBlockSuggestions());
            allUsableBlocks.addAll(provider.getAllBlocks().stream()
                    .filter(block -> block.toItem() != null)
                    .toList());
        }

        itemSuggestions = List.copyOf(items);
        blockSuggestions = List.copyOf(blocks);
        usableBlocks = List.copyOf(allUsableBlocks);
    }

    private record ReloadBinding(String pluginName, String eventClassName, String providerId) {}

    private static final class ReloadListener implements org.bukkit.event.Listener {}

    /**
     * Get a content provider by its id
     *
     * @param id the content provider's id
     * @return the content provider, null if not found
     */
    public static @Nullable ContentProvider getById(@NotNull String id) {
        return contentProviders.get(normalize(id));
    }

    /**
     * Get an item stack through content provider by its id
     *
     * @param fullNamespaceKey the item id expression (e.g. <code>nexo:example_block</code>)
     * @return the item stack, null if not found
     */
    public static @Nullable ItemStack getItemStack(@NotNull String fullNamespaceKey) {
        String[] split = fullNamespaceKey.split(":", 2);
        if (split.length < 2) {
            return getItemStack("minecraft:" + fullNamespaceKey);
        }

        String id = split[0];
        String item = split[1];

        ContentProvider contentProvider = getById(id);
        if (contentProvider == null) {
            return null;
        }

        return contentProvider.getItem(item);
    }

    /**
     * Get item's id
     *
     * @param item the item
     * @return the item's id in content provider, null if not found
     */
    public static @Nullable String getIdFromItem(@NotNull ItemStack item) {
        for (ContentProvider cp : contentProviders.values()) {
            String id = cp.getIdFromItem(item);
            if (id != null) {
                return id;
            }
        }

        return null;
    }

    /**
     * Get a block through content provider by its id
     *
     * @param fullNamespaceKey the block id expression (e.g. <code>nexo:example_block</code>)
     * @return the block, null if not found
     */
    public static @Nullable PackedBlock getBlock(@NotNull String fullNamespaceKey) {
        String[] split = fullNamespaceKey.split(":", 2);
        if (split.length < 2) {
            return getBlock("minecraft:" + fullNamespaceKey);
        }

        String id = split[0];
        String block = split[1];

        ContentProvider contentProvider = getById(id);
        if (contentProvider == null) {
            return null;
        }

        return contentProvider.getBlock(block);
    }

    /**
     * Remove the block whatever which plugin owns it
     */
    public static void destroyBlock(@NotNull Location loc) {
        for (ContentProvider cp : contentProviders.values()) {
            cp.destroyBlock(loc);
        }
    }

    /**
     * Get all item suggestions (including blocks) for tab complete
     *
     * @return a list of item suggestion
     */
    public static @NotNull List<String> getItemSuggestions() {
        return itemSuggestions;
    }

    /**
     * Get all block suggestions for tab complete
     *
     * @return a list of block suggestion
     */
    public static @NotNull List<String> getBlockSuggestions() {
        return blockSuggestions;
    }

    /**
     * Get all usable blocks (which bind an item)
     *
     * @return a list of all usable blocks
     */
    public static @NotNull List<PackedBlock> getAllUsableBlocks() {
        return usableBlocks;
    }

    /**
     * Get the block by its location
     *
     * @return the block
     */
    public static @Nullable PackedBlock getBlockByLocation(@NotNull Location loc) {
        for (ContentProvider cp : contentProviders.values()) {
            PackedBlock block = cp.getBlockByLocation(loc);
            if (block != null) {
                return block;
            }
        }

        return null;
    }
}
