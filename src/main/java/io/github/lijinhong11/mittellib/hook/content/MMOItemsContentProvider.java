package io.github.lijinhong11.mittellib.hook.content;

import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class MMOItemsContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "MMOItems";
    }

    @Override
    public @Nullable ItemStack getItem(@NonNull String id) {
        if (!id.contains(":")) {
            return null;
        }

        String type = id.substring(0, id.indexOf(':'));
        String itemId = id.substring(type.length());

        Type mmoType = Type.get(type);
        if (mmoType == null) {
            return null;
        }

        MMOItem item = MMOItems.plugin.getMMOItem(mmoType, itemId);
        if (item == null) {
            return null;
        }

        return item.newBuilder().build();
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        Type type = MMOItems.getType(item);
        if (type == null) {
            return null;
        }

        String id = MMOItems.getID(item);
        if (id == null) {
            return null;
        }

        return type.getId() + ":" + id;
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        return null; //its custom blocks are very limited
    }

    @Override
    public void destroyBlock(Location loc) {
        //its custom blocks are very limited
    }

    @Override
    public List<String> getItemSuggestions() {
        return MMOItems.plugin.getTemplates().collectTemplates().stream().map(t -> "mmoitems:" + t.getType().toString() + ":" + t.getId()).toList();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return List.of(); //its custom blocks are very limited
    }
}
