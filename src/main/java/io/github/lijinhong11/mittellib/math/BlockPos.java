package io.github.lijinhong11.mittellib.math;

import io.github.lijinhong11.mittellib.configuration.ReadWriteObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Represents a block position in 3D space with integer coordinates.
 */
@NoArgsConstructor
@AllArgsConstructor
public class BlockPos extends ReadWriteObject implements Comparable<BlockPos> {
    private @Range(from = Integer.MIN_VALUE + 1, to = Integer.MAX_VALUE - 1) int x = 0;
    private @Range(from = Integer.MIN_VALUE + 1, to = Integer.MAX_VALUE - 1) int y = 0;
    private @Range(from = Integer.MIN_VALUE + 1, to = Integer.MAX_VALUE - 1) int z = 0;

    public BlockPos(final ConfigurationSection cs) {
        super(cs);
    }

    /**
     * Get x coordinate
     * @return the x coordinate
     */
    public int x() {
        return x;
    }

    /**
     * Get y coordinate
     * @return the y coordinate
     */
    public int y() {
        return y;
    }

    /**
     * Get z coordinate
     * @return the z coordinate
     */
    public int z() {
        return z;
    }

    /**
     * Creates a BlockPos from a Bukkit Location.
     *
     * @param location the location to convert
     * @return a new BlockPos with the location's block coordinates
     */
    public static BlockPos fromLocation(final Location location) {
        if (!location.getWorld().getWorldBorder().isInside(location)) {
            throw new IllegalArgumentException("The location must in the world boarder");
        }

        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Converts this BlockPos to a Bukkit Location in the specified world.
     *
     * @param world the world for the location
     * @return a new Location at this block position
     */
    public Location toLocation(final World world) {
        Location loc = new Location(world, x, y, z);

        if (!world.getWorldBorder().isInside(loc)) {
            throw new IllegalArgumentException("The BlockPos isn't in the world boarder");
        }

        return loc;
    }

    /**
     * Adds the specified offsets to this position.
     *
     * @param x the X offset
     * @param y the Y offset
     * @param z the Z offset
     * @return a new BlockPos with the added coordinates
     */
    public BlockPos plus(final int x, final int y, final int z) {
        return new BlockPos(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Adds another BlockPos to this position.
     *
     * @param pos the position to add
     * @return a new BlockPos with the added coordinates
     */
    public BlockPos plus(final BlockPos pos) {
        return new BlockPos(this.x + pos.x, this.y + pos.y, this.z + pos.z);
    }

    /**
     * Subtracts the specified offsets from this position.
     *
     * @param x the X offset
     * @param y the Y offset
     * @param z the Z offset
     * @return a new BlockPos with the subtracted coordinates
     */
    public BlockPos minus(final int x, final int y, final int z) {
        return new BlockPos(this.x - x, this.y - y, this.z - z);
    }

    /**
     * Subtracts another BlockPos from this position.
     *
     * @param pos the position to subtract
     * @return a new BlockPos with the subtracted coordinates
     */
    public BlockPos minus(final BlockPos pos) {
        return new BlockPos(this.x - pos.x, this.y - pos.y, this.z - pos.z);
    }

    /**
     * Calculates the squared distance to another BlockPos.
     *
     * @param other the other position
     * @return the squared distance
     */
    public int distanceSquared(final BlockPos other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        int dz = this.z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Calculates the Manhattan distance (sum of absolute differences) to another
     * BlockPos.
     *
     * @param other the other position
     * @return the Manhattan distance
     */
    public int distanceManhattan(final BlockPos other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y) + Math.abs(this.z - other.z);
    }

    /**
     * Returns a BlockPos with the minimum coordinates of this and another position.
     *
     * @param other the other position
     * @return a new BlockPos with minimum coordinates
     */
    public BlockPos min(final BlockPos other) {
        return new BlockPos(Math.min(this.x, other.x), Math.min(this.y, other.y), Math.min(this.z, other.z));
    }

    /**
     * Returns a BlockPos with the maximum coordinates of this and another position.
     *
     * @param other the other position
     * @return a new BlockPos with maximum coordinates
     */
    public BlockPos max(final BlockPos other) {
        return new BlockPos(Math.max(this.x, other.x), Math.max(this.y, other.y), Math.max(this.z, other.z));
    }

    /**
     * Converts this BlockPos to a long number which represents this BlockPos
     *
     * @return a long number which represents this BlockPos
     */
    public long getBlockKey() {
        return (long) x & 134217727L | ((long) z & 134217727L) << 27 | (long) y << 54;
    }

    @Override
    public int compareTo(BlockPos other) {
        int cmpX = Integer.compare(this.x, other.x);
        if (cmpX != 0) return cmpX;

        int cmpY = Integer.compare(this.y, other.y);
        if (cmpY != 0) return cmpY;

        return Integer.compare(this.z, other.z);
    }

    @Override
    public @NotNull String toString() {
        return x + ", " + y + ", " + z;
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("x", x);
        cs.set("y", y);
        cs.set("z", z);
    }

    @Override
    public void read(ConfigurationSection cs) {
        x = cs.getInt("x");
        y = cs.getInt("y");
        z = cs.getInt("z");
    }
}
