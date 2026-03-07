package io.github.lijinhong11.mittellib.actions.item;

import io.github.lijinhong11.mittellib.actions.ItemAction;
import java.util.List;
import lombok.AllArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
class DropMultiple implements ItemAction {
    private final int multi;

    public void accept(ItemStack item, List<ItemStack> drops, @Nullable Block block) {
        drops.forEach(i -> {
            int amount = i.getAmount();
            i.setAmount(Math.min(amount * multi, i.getMaxStackSize()));
        });
    }
}
