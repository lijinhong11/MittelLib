package io.github.lijinhong11.mittellib.hook.content;

import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import java.util.List;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class CraftEngineContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "CraftEngine";
    }

    @Override
    public @Nullable ItemStack getItem(@NonNull String id) {
        NamespacedKey key = BukkitUtils.getNamespacedKey(id);
        if (key == null) {
            return null;
        }

        CustomItem<ItemStack> ci = CraftEngineItems.byId(Key.of(key.namespace(), key.value()));
        if (ci == null) {
            return null;
        }

        return ci.buildItemStack();
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        CustomItem<ItemStack> customItem = CraftEngineItems.byItemStack(item);

        if (customItem == null) {
            return null;
        }

        return customItem.id().asString();
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        NamespacedKey key = BukkitUtils.getNamespacedKey(id);
        if (key == null) {
            return null;
        }

        CustomBlock ci = CraftEngineBlocks.byId(Key.of(key.namespace(), key.value()));
        if (ci == null) {
            return null;
        }

        return new PackedCraftEngineBlock(ci);
    }

    @Override
    public void destroyBlock(Location loc) {
        CraftEngineBlocks.remove(loc.getBlock());
    }

    @Override
    public List<String> getItemSuggestions() {
        return CraftEngineItems.loadedItems().keySet().stream()
                .map(k -> "craftengine:" + k.asString())
                .toList();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return CraftEngineBlocks.loadedBlocks().entrySet().stream()
                .filter(b -> new PackedCraftEngineBlock(b.getValue()).toItem() != null)
                .map(k -> "craftengine:" + k.getKey().asString())
                .toList();
    }

    @Override
    public @Nullable PackedBlock getBlockByLocation(Location loc) {
        Block block = loc.getBlock();
        if (!CraftEngineBlocks.isCustomBlock(block)) {
            return null;
        }

        ImmutableBlockState ibs = CraftEngineBlocks.getCustomBlockState(block);
        if (ibs == null) {
            return null;
        }

        return new PackedCraftEngineBlock(ibs.behavior().block());
    }

    private record PackedCraftEngineBlock(CustomBlock block) implements PackedBlock {
        @Override
        public void place(@NotNull Location location) {
            CraftEngineBlocks.place(location, block.id(), true);
        }

        @Override
        public String getId() {
            return block.id().asString();
        }

        @Override
        public ItemStack toItem() {
            CustomItem<ItemStack> bind = CraftEngineItems.byId(block.id());
            if (bind == null) {
                return null;
            }

            return bind.buildItemStack();
        }
    }
}
