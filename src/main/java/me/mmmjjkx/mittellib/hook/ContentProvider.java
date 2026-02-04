package me.mmmjjkx.mittellib.hook;

import me.mmmjjkx.mittellib.item.block.PackedBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ContentProvider {
    @Nullable ItemStack getItem(@NotNull String id);

    @Nullable PackedBlock getBlock(@NotNull String id);
}
