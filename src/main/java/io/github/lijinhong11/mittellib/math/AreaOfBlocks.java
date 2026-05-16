package io.github.lijinhong11.mittellib.math;

import java.util.List;
import java.util.function.Consumer;

/**
 * An interface which manage an area of blocks
 */
public interface AreaOfBlocks {
    /**
     * Checks if a block position is contained within this area.
     *
     * @param pos the position to check
     * @return true if the position is within the area, false otherwise
     */
    boolean contains(BlockPos pos);

    /**
     * Converts this area to a list of all block positions it contains.
     *
     * @return a list of all {@link BlockPos} in this area
     */
    List<BlockPos> asPosList();

    /**
     * Performs an action for each block position inside this area.
     *
     * @param action the action to perform
     */
    void forEach(Consumer<BlockPos> action);

    /**
     * Gets the total number of blocks in this area.
     *
     * @return the total number of blocks
     */
    int volume();

    /**
     * Gets the area type
     * @return the area type
     */
    AreaType getType();
}
