package io.github.lijinhong11.mittellib.actions;

import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemAction {
    void accept(ItemStack tool, List<ItemStack> drops, @Nullable Block block);
}
