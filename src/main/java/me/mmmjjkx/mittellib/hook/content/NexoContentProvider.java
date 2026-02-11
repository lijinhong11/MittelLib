package me.mmmjjkx.mittellib.hook.content;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import me.mmmjjkx.mittellib.iface.ContentProvider;
import me.mmmjjkx.mittellib.iface.block.PackedBlock;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class NexoContentProvider implements ContentProvider {
    @Override
    public @Nullable ItemStack getItem(@NonNull String id) {
        Optional<ItemBuilder> optional = NexoItems.optionalItemFromId(id);
        return optional.map(ItemBuilder::build).orElse(null);
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        CustomBlockMechanic mechanic = NexoBlocks.customBlockMechanic(id);
        if (mechanic == null) {
            return null;
        }

        return new PackedNexoBlock(mechanic);
    }

    @Override
    public void destroyBlock(Location loc) {
        NexoBlocks.remove(loc);
    }

    @Override
    public List<String> getItemSuggestions() {
        return List.of();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return List.of();
    }

    private record PackedNexoBlock(CustomBlockMechanic mechanic) implements PackedBlock {
        @Override
        public void place(Location location) {
            NexoBlocks.place(mechanic.getItemID(), location);
        }

        @Override
        public String getId() {
            return mechanic.getItemID();
        }
    }
}
