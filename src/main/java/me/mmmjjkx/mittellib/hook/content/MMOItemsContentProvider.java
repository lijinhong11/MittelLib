package me.mmmjjkx.mittellib.hook.content;

import me.mmmjjkx.mittellib.iface.ContentProvider;
import me.mmmjjkx.mittellib.iface.block.PackedBlock;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class MMOItemsContentProvider implements ContentProvider {
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
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        try {
            int blockId = Integer.parseUnsignedInt(id);
            CustomBlock block = MMOItems.plugin.getCustomBlocks().getBlock(blockId);
            if (block == null) {
                return null;
            }

            return new PackedMMOItemsBlock(block);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void destroyBlock(Location loc) {
        Block block = loc.getBlock();
        block.setType(Material.AIR);
        block.setBlockData(Material.AIR.createBlockData());
    }

    @Override
    public List<String> getItemSuggestions() {
        return List.of();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return List.of();
    }

    private record PackedMMOItemsBlock(CustomBlock block) implements PackedBlock {
        @Override
        public void place(Location location) {
            Block modify = location.getBlock();
            modify.setType(block.getState().getType());
            modify.setBlockData(block.getState().getBlockData());
        }

        @Override
        public String getId() {
            return String.valueOf(block.getId());
        }
    }
}
