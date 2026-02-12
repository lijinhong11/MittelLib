package io.github.lijinhong11.mittellib.iface.actions;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public interface ItemAction extends BiConsumer<ItemStack, List<ItemStack>> {
    @Override
    void accept(ItemStack tool, List<ItemStack> drops);
}
