package me.mmmjjkx.mittellib.hook.content;

import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import me.mmmjjkx.mittellib.iface.ContentProvider;
import me.mmmjjkx.mittellib.iface.block.PackedBlock;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EcoItemsContentProvider implements ContentProvider {
    @Override
    public @Nullable ItemStack getItem(@NotNull String id) {
        EcoItem item = EcoItems.INSTANCE.getByID(id);
        if (item == null) {
            return null;
        }

        return item.getItemStack();
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
        return List.of();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return List.of();
    }
}
