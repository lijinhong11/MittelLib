package io.github.lijinhong11.mittellib.hook.content;

import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EcoItemsContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "EcoItems";
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull String id) {
        EcoItem item = EcoItems.INSTANCE.getByID(id);
        if (item == null) {
            return null;
        }

        return item.getItemStack();
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        return EcoItems.INSTANCE.values().stream().filter(p -> p.getItemStack().equals(item)).findFirst().map(e -> e.getId().asString()).orElse(null);
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        return null;
    }

    @Override
    public void destroyBlock(Location loc) {

    }

    @Override
    public List<String> getItemSuggestions() {
        return EcoItems.INSTANCE.values().stream().map(ei -> "ecoitems" + ei.getId().asString()).toList();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return List.of();
    }
}
