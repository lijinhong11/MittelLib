package io.github.lijinhong11.mittellib.hook;

import io.github.lijinhong11.mittellib.hook.content.MinecraftContentProvider;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@UtilityClass
public class ContentProviders {
    private static final Map<String, ContentProvider> contentProviders = new LinkedHashMap<>();

    public static void init() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            String name = plugin.getName() + "ContentProvider";
            try {
                Class<?> theClass = Class.forName("io.github.lijinhong11.mittellib.hook." + name);
                Constructor<? extends ContentProvider> constructor = ((Class<? extends ContentProvider>) theClass).getConstructor();
                contentProviders.put(name.toLowerCase(), constructor.newInstance());
            } catch (Exception ignore) {
            }
        }

        contentProviders.put("minecraft", new MinecraftContentProvider());
    }

    /**
     * Get a content provider by its id
     *
     * @param id the content provider's id
     * @return the content provider, null if not found
     */
    public static @Nullable ContentProvider getById(String id) {
        for (ContentProvider provider : contentProviders.values()) {
            if (provider.getId().equalsIgnoreCase(id)) {
                return provider;
            }
        }

        return null;
    }

    /**
     * Get an item stack through content provider by its id
     *
     * @param fullNamespaceKey the item id expression (e.g. <code>nexo:example_block</code>)
     * @return the item stack, null if not found
     */
    public static @Nullable ItemStack getItemStack(@NotNull String fullNamespaceKey) {
        String[] split = fullNamespaceKey.split(":", 2);
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
            if (cp.getIdFromItem(item) != null) {
                return cp.getIdFromItem(item);
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
        List<String> suggest = new ArrayList<>();
        for (ContentProvider cp : contentProviders.values()) {
            suggest.addAll(cp.getItemSuggestions());
        }

        return suggest;
    }

    /**
     * Get all block suggestions for tab complete
     *
     * @return a list of block suggestion
     */
    public static @NotNull List<String> getBlockSuggestions() {
        List<String> suggest = new ArrayList<>();
        for (ContentProvider cp : contentProviders.values()) {
            suggest.addAll(cp.getBlockSuggestions());
        }

        return suggest;
    }
}
