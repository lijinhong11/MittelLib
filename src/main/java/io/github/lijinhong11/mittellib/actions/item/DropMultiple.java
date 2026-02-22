package io.github.lijinhong11.mittellib.actions.item;

import io.github.lijinhong11.mittellib.iface.actions.ItemAction;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
class DropMultiple implements ItemAction {
    private final int multi;

    @Override
    public void accept(ItemStack tool, List<ItemStack> drops) {
        drops.forEach(i -> {
            int amount = i.getAmount();
            i.setAmount(Math.min(amount * multi, i.getMaxStackSize()));
        });
    }
}
