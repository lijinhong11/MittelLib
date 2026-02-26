package io.github.lijinhong11.mittellib.actions;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface ItemAction {
    void accept(ItemStack tool, List<ItemStack> drops, @Nullable Block block);
}
