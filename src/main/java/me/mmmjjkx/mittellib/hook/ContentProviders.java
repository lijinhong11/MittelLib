package me.mmmjjkx.mittellib.hook;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.item.block.PackedBlock;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public enum ContentProviders implements ContentProvider {
    NEXO {
        @Override
        public ItemStack getItem(@NonNull String id) {
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

        @AllArgsConstructor
        private static class PackedNexoBlock extends PackedBlock {
            private final CustomBlockMechanic mechanic;

            @Override
            public void place(Location location) {
                NexoBlocks.place(mechanic.getItemID(), location);
            }

            @Override
            public String getId() {
                return mechanic.getItemID();
            }
        }
    },
    ITEMSADDER {
        @Override
        public ItemStack getItem(@NonNull String id) {
            CustomStack stack = CustomStack.getInstance(id);
            if (stack == null) {
                return null;
            }

            return stack.getItemStack();
        }

        @Override
        public @Nullable PackedBlock getBlock(@NotNull String id) {
            CustomBlock block = CustomBlock.getInstance(id);
            if (block == null) {
                return null;
            }

            return new PackedItemsAdderBlock(block);
        }

        @AllArgsConstructor
        private static class PackedItemsAdderBlock extends PackedBlock {
            private final CustomBlock block;

            @Override
            public void place(Location location) {
                block.place(location);
            }

            @Override
            public String getId() {
                return block.getId();
            }
        }
    },
    CRAFTENGINE {
        @Override
        public @Nullable ItemStack getItem(@NonNull String id) {
            return null;
        }

        @Override
        public @Nullable PackedBlock getBlock(@NotNull String id) {
            return null;
        }
    },
    ORAXEN {
        @Override
        public @Nullable ItemStack getItem(@NonNull String id) {
            return null;
        }

        @Override
        public @Nullable PackedBlock getBlock(@NotNull String id) {
            return null;
        }
    },
    MMOITEMS {
        @Override
        public @Nullable ItemStack getItem(@NonNull String id) {
            return null;
        }

        @Override
        public @Nullable PackedBlock getBlock(@NotNull String id) {
            return null;
        }
    };

    public static ContentProvider getByName(String name) {
        for (ContentProvider provider : values()) {
            if (provider.toString().equalsIgnoreCase(name)) {
                return provider;
            }
        }

        return null;
    }
}
