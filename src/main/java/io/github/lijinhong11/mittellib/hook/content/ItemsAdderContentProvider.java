package io.github.lijinhong11.mittellib.hook.content;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ItemsAdderContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "ItemsAdder";
    }

    @Override
    public @Nullable ItemStack getItem(@NonNull String id) {
        CustomStack stack = CustomStack.getInstance(id);
        if (stack == null) {
            return null;
        }

        return stack.getItemStack();
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        CustomStack customStack = CustomStack.byItemStack(item);
        if (customStack == null) {
            return null;
        }

        return customStack.getId();
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        CustomBlock block = CustomBlock.getInstance(id);
        if (block == null) {
            return null;
        }

        return new PackedItemsAdderBlock(block);
    }

    @Override
    public void destroyBlock(Location loc) {
        CustomBlock.remove(loc);
    }

    @Override
    public List<String> getItemSuggestions() {
        return CustomStack.getNamespacedIdsInRegistry().stream().map(s -> "itemsadder:" + s).toList();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return CustomBlock.getNamespacedIdsInRegistry().stream().map(s -> "itemsadder:" + s).toList();
    }

    private record PackedItemsAdderBlock(CustomBlock block) implements PackedBlock {
        @Override
        public void place(Location location) {
            block.place(location);
        }

        @Override
        public String getId() {
            return block.getId();
        }
    }
}
