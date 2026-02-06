package me.mmmjjkx.mittellib.hook;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.Mechanic;
import me.mmmjjkx.mittellib.item.block.PackedBlock;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.NullUtils;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public enum ContentProviders implements ContentProvider {
    NEXO {
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
    },
    ITEMSADDER {
        @Override
        public @Nullable ItemStack getItem(@NonNull String id) {
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

        private record PackedItemsAdderBlock(CustomBlock block) implements PackedBlock {
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
            NamespacedKey key = BukkitUtils.getNamespacedKey(id);
            if (key == null) {
                return null;
            }

            CustomItem<ItemStack> ci = CraftEngineItems.byId(Key.of(key.namespace(), key.value()));
            if (ci == null) {
                return null;
            }

            return ci.buildItemStack();
        }

        @Override
        public @Nullable PackedBlock getBlock(@NotNull String id) {
            NamespacedKey key = BukkitUtils.getNamespacedKey(id);
            if (key == null) {
                return null;
            }

            net.momirealms.craftengine.core.block.CustomBlock ci = CraftEngineBlocks.byId(Key.of(key.namespace(), key.value()));
            if (ci == null) {
                return null;
            }

            return new PackedCraftEngineBlock(ci);
        }

        private record PackedCraftEngineBlock(net.momirealms.craftengine.core.block.CustomBlock block) implements PackedBlock {
            @Override
            public void place(Location location) {
                CraftEngineBlocks.place(location, block.id(), true);
            }

            @Override
            public String getId() {
                return block.id().asString();
            }
        }
    },
    ORAXEN {
        @Override
        public @Nullable ItemStack getItem(@NonNull String id) {
            Optional<io.th0rgal.oraxen.items.ItemBuilder> optional = OraxenItems.getOptionalItemById(id);
            return optional.map(io.th0rgal.oraxen.items.ItemBuilder::build).orElse(null);
        }

        @Override
        public @Nullable PackedBlock getBlock(@NotNull String id) {
            Mechanic block = NullUtils.findAnyNonNull(OraxenBlocks.getChorusMechanic(id), OraxenBlocks.getNoteBlockMechanic(id), OraxenBlocks.getStringMechanic(id));
            if (block == null) {
                return null;
            }

            Optional<io.th0rgal.oraxen.items.ItemBuilder> itemBuilderOptional = OraxenItems.getOptionalItemById(block.getItemID());
            if (itemBuilderOptional.isEmpty()) {
                return null;
            }

            return new PackedOraxenBlock(block);
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
