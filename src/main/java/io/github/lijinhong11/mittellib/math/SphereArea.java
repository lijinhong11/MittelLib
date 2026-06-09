package io.github.lijinhong11.mittellib.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a spherical area defined by a center position and radius.
 */
public record SphereArea(@NotNull BlockPos center, int radius) implements AreaOfBlocks {
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
        int r2 = r * r + r;
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

    public boolean contains(@NotNull BlockPos pos) {

        int dx = pos.x() - center.x();
        int dy = pos.y() - center.y();
        int dz = pos.z() - center.z();

        return dx * dx + dy * dy + dz * dz <= radius * radius + radius;
    }

    public void forEach(@NotNull Consumer<BlockPos> action) {
        int[] off = offsets(radius);

        int cx = center.x();
        int cy = center.y();
        int cz = center.z();

        for (int i = 0; i < off.length; i += 3) {
            action.accept(new BlockPos(cx + off[i], cy + off[i + 1], cz + off[i + 2]));
        }
    }

    public List<BlockPos> asPosList() {
        int[] off = offsets(radius);

        int cx = center.x();
        int cy = center.y();
        int cz = center.z();

        List<BlockPos> list = new ArrayList<>(off.length / 3);

        for (int i = 0; i < off.length; i += 3) {
            list.add(new BlockPos(cx + off[i], cy + off[i + 1], cz + off[i + 2]));
        }

        return list;
    }

    public int volume() {
        return offsets(radius).length / 3;
    }

    @Override
    public AreaType getType() {
        return AreaType.SPHERE;
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
