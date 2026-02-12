package io.github.lijinhong11.mittellib.hook.content;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import io.github.lijinhong11.mittellib.utils.NullUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OraxenContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "Oraxen";
    }

    @Override
    public @Nullable ItemStack getItem(@NonNull String id) {
        Optional<ItemBuilder> optional = OraxenItems.getOptionalItemById(id);
        return optional.map(ItemBuilder::build).orElse(null);
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        return OraxenItems.getIdByItem(item);
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        Mechanic block = NullUtils.findAnyNonNull(OraxenBlocks.getChorusMechanic(id), OraxenBlocks.getNoteBlockMechanic(id), OraxenBlocks.getStringMechanic(id));
        if (block == null) {
            return null;
        }

        Optional<ItemBuilder> itemBuilderOptional = OraxenItems.getOptionalItemById(block.getItemID());
        if (itemBuilderOptional.isEmpty()) {
            return null;
        }

        return new PackedOraxenBlock(block);
    }

    @Override
    public void destroyBlock(Location loc) {
        OraxenBlocks.remove(loc, null);
    }

    @Override
    public List<String> getItemSuggestions() {
        return Arrays.stream(OraxenItems.getItemNames()).map(i -> "oraxen:" + i).toList();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return OraxenBlocks.getBlockIDs().stream().map(b -> "oraxen:" + b).toList();
    }

    private record PackedOraxenBlock(Mechanic mechanic) implements PackedBlock {
        @Override
        public void place(Location location) {
            OraxenBlocks.place(mechanic.getItemID(), location);
        }

        @Override
        public String getId() {
            return mechanic.getItemID();
        }
    }
}
