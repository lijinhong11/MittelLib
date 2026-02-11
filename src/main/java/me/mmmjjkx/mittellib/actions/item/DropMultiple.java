package me.mmmjjkx.mittellib.actions.item;

import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.iface.actions.ItemAction;
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
