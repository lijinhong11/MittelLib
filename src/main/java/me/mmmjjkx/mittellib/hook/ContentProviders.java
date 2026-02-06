package me.mmmjjkx.mittellib.hook;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.ssomar.score.api.executableblocks.ExecutableBlocksAPI;
import com.ssomar.score.api.executableblocks.config.ExecutableBlockInterface;
import com.ssomar.score.api.executableblocks.config.ExecutableBlocksManagerInterface;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.api.executableitems.config.ExecutableItemsManagerInterface;
import com.ssomar.score.utils.place.OverrideMode;
import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.Mechanic;
import me.mmmjjkx.mittellib.item.block.PackedBlock;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.NullUtils;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
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
            if (!id.contains(":")) {
                return null;
            }

            String type = id.substring(0, id.indexOf('.'));
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
                net.Indyuce.mmoitems.api.block.CustomBlock block = MMOItems.plugin.getCustomBlocks().getBlock(blockId);
                if (block == null) {
                    return null;
                }

                return new PackedMMOItemsBlock(block);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        private record PackedMMOItemsBlock(net.Indyuce.mmoitems.api.block.CustomBlock block) implements PackedBlock {
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
    },
    EXECUTABLEITEMS {
        @Override
        public @Nullable ItemStack getItem(@NotNull String id) {
            ExecutableItemsManagerInterface manager = ExecutableItemsAPI.getExecutableItemsManager();
            Optional<ExecutableItemInterface> itemOptional = manager.getExecutableItem(id);
            return itemOptional.map(item -> item.buildItem(1, Optional.empty())).orElse(null);
        }

        @Override
        public @Nullable PackedBlock getBlock(@NotNull String id) {
            return null;
        }
    },
    EXECUTABLEBLOCKS {
        @Override
        public @Nullable ItemStack getItem(@NotNull String id) {
            return null;
        }

        @Override
        public @Nullable PackedBlock getBlock(@NotNull String id) {
            ExecutableBlocksManagerInterface manager = ExecutableBlocksAPI.getExecutableBlocksManager();
            Optional<ExecutableBlockInterface> blockOptional = manager.getExecutableBlock(id);
            return blockOptional.map(PackedExecutableBlocksBlock::new).orElse(null);
        }

        private record PackedExecutableBlocksBlock(ExecutableBlockInterface block) implements PackedBlock {
            @Override
            public void place(Location location) {
                block.place(location, true, OverrideMode.REMOVE_EXISTING, null, null);
            }

            @Override
            public String getId() {
                return block.getId();
            }
        }
    },
    ECOITEMS {
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
