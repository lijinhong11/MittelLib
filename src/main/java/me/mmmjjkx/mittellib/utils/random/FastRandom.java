package me.mmmjjkx.mittellib.utils.random;

@SuppressWarnings("deprecation")
public final class FastRandom {
    private static final ThreadLocal<SplitMix64> LOCAL =
            ThreadLocal.withInitial(() ->
                    new SplitMix64(mixSeed()));

    private static long mixSeed() {
        long id = Thread.currentThread().getId();
        long x = System.nanoTime() ^ (id << 32);

        x ^= (x >>> 33);
        x *= 0xff51afd7ed558ccdL;
        x ^= (x >>> 33);
        x *= 0xc4ceb9fe1a85ec53L;
        x ^= (x >>> 33);
        return x;
    }

    private static SplitMix64 rng() {
        return LOCAL.get();
    }

    public static int nextInt() {
        return rng().nextInt();
    }

    public static int nextInt(int bound) {
        return rng().nextInt(bound);
    }

    public static int nextInt(int origin, int bound) {
        return rng().nextInt(origin, bound);
    }

    public static long nextLong() {
        return rng().nextLong();
    }

    public static long nextLong(long bound) {
        return rng().nextLong(bound);
    }

    public static long nextLong(long origin, long bound) {
        return rng().nextLong(origin, bound);
    }

    public static double nextDouble() {
        return rng().nextDouble();
    }

    public static double nextDouble(double bound) {
        return rng().nextDouble(0d, bound);
    }

    public static double nextDouble(double origin, double bound) {
        return rng().nextDouble(origin, bound);
    }

    private static final class SplitMix64 {
        private long state;

        SplitMix64(long seed) {
            this.state = seed;
        }

        long nextLong() {
            long z = (state += 0x9E3779B97F4A7C15L);
            z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
            z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
            return z ^ (z >>> 31);
        }

        int nextInt() {
            return (int) nextLong();
        }

        int nextInt(int bound) {
            if (bound <= 0)
                throw new IllegalArgumentException("bound must be positive");
            return nextInt(0, bound);
        }

        int nextInt(int origin, int bound) {
            if (origin >= bound) {
                throw new IllegalArgumentException("origin >= bound");
            }

            int range = bound - origin;
            int m = range - 1;
            int r;

            if ((range & m) == 0) {
                // power of two
                r = (int) (nextLong() & m);
            } else {
                int u;
                do {
                    u = (int) (nextLong() >>> 1);
                    r = u % range;
                } while (u + m - r < 0);
            }
            return r + origin;
        }

        long nextLong(long bound) {
            if (bound <= 0) {
                throw new IllegalArgumentException("bound must be positive");
            }

            return nextLong(0, bound);
        }

        long nextLong(long origin, long bound) {
            if (origin >= bound) {
                throw new IllegalArgumentException("origin >= bound");
            }

            long range = bound - origin;
            long m = range - 1;
            long r;

            if ((range & m) == 0) {
                r = nextLong() & m;
            } else {
                long u;
                do {
                    u = nextLong() >>> 1;
                    r = u % range;
                } while (u + m - r < 0);
            }
            return r + origin;
        }

        double nextDouble() {
            return (nextLong() >>> 11) * 0x1.0p-53;
        }

        double nextDouble(double origin, double bound) {
            if (!(origin < bound)) {
                throw new IllegalArgumentException("origin >= bound");
            }
            return origin + (bound - origin) * nextDouble();
        }
    }
}
