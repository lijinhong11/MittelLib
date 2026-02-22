package io.github.lijinhong11.mittellib.hook.content;

import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MinecraftContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "Minecraft";
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull String id) {
        Material material = BukkitUtils.getMaterial(id);
        return new ItemStack(material);
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        return "minecraft:" + item.getType().toString().toLowerCase();
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        Material material = BukkitUtils.getMaterial(id);
        if (!material.isBlock()) {
            return null;
        }

        return new PackedMinecraftBlock(material);
    }

    @Override
    public void destroyBlock(Location loc) {
        Block block = loc.getBlock();
        block.setType(Material.AIR);
        block.setBlockData(Material.AIR.createBlockData());
    }

    @Override
    public List<String> getItemSuggestions() {
        return Arrays.stream(Material.values()).filter(m -> !m.isAir() && m.isItem()).map(m -> "minecraft:" + m.toString().toLowerCase()).toList();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return Arrays.stream(Material.values()).filter(m -> !m.isAir() && m.isBlock()).map(m -> "minecraft:" + m.toString().toLowerCase()).toList();
    }

    private record PackedMinecraftBlock(Material material) implements PackedBlock {
        @Override
        public void place(Location location) {
            location.getBlock().setType(material);
        }

        @Override
        public String getId() {
            return "minecraft:" + material.toString().toLowerCase();
        }
    }
}
