package me.mmmjjkx.mittellib.hook.content;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.api.executableitems.config.ExecutableItemsManagerInterface;
import me.mmmjjkx.mittellib.hook.ContentProvider;
import me.mmmjjkx.mittellib.item.block.PackedBlock;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ExecutableItemsContentProvider implements ContentProvider {
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
