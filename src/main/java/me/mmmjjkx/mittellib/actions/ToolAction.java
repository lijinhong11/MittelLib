package me.mmmjjkx.mittellib.actions;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public interface ToolAction extends BiConsumer<ItemStack, List<ItemStack>> {
    @Override
    void accept(ItemStack tool, List<ItemStack> drops);
}
