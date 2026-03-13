package io.github.lijinhong11.mittellib.math;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a spherical area defined by a center position and radius.
 * <p>
 * Internally uses cached offset masks to achieve high-performance iteration.
 * The offsets are generated once per radius and reused for all spheres with
 * the same radius.
 */
public record SphereArea(@NotNull BlockPos center, int radius) {
    private static final Map<Integer, int[]> CACHE = new HashMap<>();

    /**
     * Creates a spherical area.
     *
     * @param center the center position
     * @param radius the sphere radius (in blocks)
     */
    public SphereArea {
        Objects.requireNonNull(center);
        if (radius <= 0) throw new IllegalArgumentException("radius <= 0");
    }

    /**
     * Gets the center position of this sphere.
     *
     * @return the center BlockPos
     */
    @Override
    public BlockPos center() {
        return center;
    }

    /**
     * Gets the radius of this sphere.
     *
     * @return the radius
     */
    @Override
    public int radius() {
        return radius;
    }

    /**
     * Computes the offset mask for a given radius.
     *
     * @param r the radius
     * @return packed offset array (x,y,z,x,y,z,...)
     */
    private static int[] computeOffsets(int r) {
        int r2 = r * r;
        List<Integer> offsets = new ArrayList<>();

        for (int x = -r; x <= r; x++) {
            int x2 = x * x;

            for (int y = -r; y <= r; y++) {
                int xy2 = x2 + y * y;
                if (xy2 > r2) continue;

                for (int z = -r; z <= r; z++) {
                    if (xy2 + z * z <= r2) {
                        offsets.add(x);
                        offsets.add(y);
                        offsets.add(z);
                    }
                }
            }
        }

        int[] result = new int[offsets.size()];
        for (int i = 0; i < offsets.size(); i++) {
            result[i] = offsets.get(i);
        }

        return result;
    }

    /**
     * Gets cached offsets for a radius.
     *
     * @param radius the radius
     * @return packed offsets
     */
    private static int[] offsets(int radius) {
        return CACHE.computeIfAbsent(radius, SphereArea::computeOffsets);
    }

    /**
     * Checks if a position is inside this sphere.
     *
     * @param pos the position to check
     * @return true if inside the sphere
     */
    public boolean contains(@NotNull BlockPos pos) {

        int dx = pos.x() - center.x();
        int dy = pos.y() - center.y();
        int dz = pos.z() - center.z();

        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    /**
     * Performs an action for each block position inside this sphere.
     *
     * @param action the action to perform
     */
    public void forEach(@NotNull Consumer<BlockPos> action) {
        int cx = center.x();
        int cy = center.y();
        int cz = center.z();

        int[] off = offsets(radius);

        for (int i = 0; i < off.length; i += 3) {
            action.accept(new BlockPos(
                    cx + off[i],
                    cy + off[i + 1],
                    cz + off[i + 2]
            ));
        }
    }

    /**
     * Converts this sphere into a list of block positions.
     *
     * @return a list of all BlockPos inside this sphere
     */
    public List<BlockPos> asPosList() {
        int[] off = offsets(radius);

        int cx = center.x();
        int cy = center.y();
        int cz = center.z();

        List<BlockPos> list = new ArrayList<>(off.length / 3);

        for (int i = 0; i < off.length; i += 3) {
            list.add(new BlockPos(
                    cx + off[i],
                    cy + off[i + 1],
                    cz + off[i + 2]
            ));
        }

        return list;
    }

    /**
     * Gets the number of blocks contained in this sphere.
     *
     * @return block count
     */
    public int volume() {
        return offsets(radius).length / 3;
    }

    /**
     * Expands this sphere by the specified amount.
     *
     * @param amount expansion amount
     * @return a new expanded SphereArea
     */
    public SphereArea expand(int amount) {
        return new SphereArea(center, radius + amount);
    }

    @Override
    public @NotNull String toString() {
        return "SphereArea{center=" + center + ", radius=" + radius + '}';
    }
}