package io.github.lijinhong11.mittellib.actions.item;

import io.github.lijinhong11.mittellib.actions.ItemAction;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
class MineRangedPlane implements ItemAction {
    private final @Positive short range;

    @Override
    public void accept(ItemStack tool, List<ItemStack> drops, @Nullable Block block) {
        if (block == null) return;

        World world = block.getWorld();

        final int baseX = block.getX();
        final int baseY = block.getY();
        final int baseZ = block.getZ();

        BlockFace face = block.getFace(block);
        if (face == null) face = BlockFace.UP;

        final boolean horizontal = (face == BlockFace.UP || face == BlockFace.DOWN);

        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {

                int x = baseX;
                int y = baseY;
                int z = baseZ;

                if (horizontal) {
                    x += dx;
                    z += dz;
                } else if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
                    x += dx;
                    y += dz;
                } else { // EAST / WEST
                    z += dx;
                    y += dz;
                }

                Block target = world.getBlockAt(x, y, z);

                if (target.isEmpty()) continue;

                if (!target.isPreferredTool(tool)) continue;

                Collection<ItemStack> blockDrops = target.getDrops(tool);

                if (!blockDrops.isEmpty()) {
                    drops.addAll(blockDrops);
                }

                target.breakNaturally(tool);
            }
        }
    }
}
