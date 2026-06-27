Areas & Math
==

The `math` package provides an integer block position type and two area shapes
useful for region selection, region effects, and iteration.

## BlockPos

[`BlockPos`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/math/BlockPos.java)
is an immutable-style integer `(x, y, z)` position. It implements `Comparable`
and [`ReadWriteObject`](configuration.md#readwriteobject).

```java
BlockPos pos = new BlockPos(10, 64, -20);

// Convert to/from Bukkit (validated against the world border)
BlockPos fromLoc = BlockPos.fromLocation(location);
Location loc     = pos.toLocation(world);

// Arithmetic (returns new instances)
BlockPos up   = pos.plus(0, 1, 0);
BlockPos diff = pos.minus(other);

// Distances
int sq  = pos.distanceSquared(other);
int man = pos.distanceManhattan(other);

// Corner helpers
BlockPos lo = pos.min(other);
BlockPos hi = pos.max(other);

// Packed long key (e.g. for maps/sets)
long key = pos.getBlockKey();
```

It serializes to a simple `x/y/z` section:

```yaml
x: 10
y: 64
z: -20
```

## AreaOfBlocks

Both shapes implement
[`AreaOfBlocks`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/math/AreaOfBlocks.java):

```java
boolean contains(BlockPos pos);
List<BlockPos> asPosList();
void forEach(Consumer<BlockPos> action);
int volume();
AreaType getType(); // CUBOID or SPHERE
```

Prefer `forEach` over `asPosList` for large areas — it avoids materializing the
whole list.

## CuboidArea

A rectangular box defined by two corners.

```java
CuboidArea area = new CuboidArea(pos1, pos2);
CuboidArea fromLocs = CuboidArea.createFromLocation(loc1, loc2);

BlockPos min = area.getMin();
BlockPos max = area.getMax();
Location center = area.getCenterLocation(world);

CuboidArea bigger = area.expand(1);        // all axes
CuboidArea bigger2 = area.expand(2, 0, 2); // per-axis

int sx = area.sizeX(), sy = area.sizeY(), sz = area.sizeZ();
int total = area.volume();

area.forEach(p -> world.getBlockAt(p.toLocation(world)).setType(Material.AIR));
```

## SphereArea

A sphere defined by a center and radius. Offsets per radius are cached, so
repeated iteration over the same radius is cheap.

```java
SphereArea sphere = new SphereArea(center, 5); // radius must be > 0

if (sphere.contains(pos)) { /* ... */ }
SphereArea bigger = sphere.expand(2);
int blocks = sphere.volume();

sphere.forEach(p -> { /* ... */ });
```

> Both `contains` and the generated set use the radius test
> `dx² + dy² + dz² <= r² + r`, which produces a slightly rounded, natural-looking
> sphere.
