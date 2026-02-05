package me.mmmjjkx.mittellib.actions.tool;

import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.actions.ToolAction;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
class DropMultiple implements ToolAction {
    private final int multi;

    @Override
    public void accept(ItemStack tool, List<ItemStack> drops) {
        drops.forEach(i -> {
            int amount = i.getAmount();
            i.setAmount(Math.min(amount * multi, i.getMaxStackSize()));
        });
    }
}
