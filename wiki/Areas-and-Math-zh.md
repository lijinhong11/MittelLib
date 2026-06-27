区域与数学
==

[English](Areas-and-Math) | 简体中文

`math` 包提供了一个整数方块坐标类型，以及两种区域形状，适用于区域选择、区域效果和
迭代遍历。

## BlockPos

[`BlockPos`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/math/BlockPos.java)
是一个偏不可变风格的整数 `(x, y, z)` 坐标。它实现了 `Comparable` 与
[`ReadWriteObject`](Configuration-zh#readwriteobject)。

```java
BlockPos pos = new BlockPos(10, 64, -20);

// 与 Bukkit 互转（会按世界边界进行校验）
BlockPos fromLoc = BlockPos.fromLocation(location);
Location loc     = pos.toLocation(world);

// 运算（返回新实例）
BlockPos up   = pos.plus(0, 1, 0);
BlockPos diff = pos.minus(other);

// 距离
int sq  = pos.distanceSquared(other);
int man = pos.distanceManhattan(other);

// 角点辅助
BlockPos lo = pos.min(other);
BlockPos hi = pos.max(other);

// 打包的 long 键（如用于 map/set）
long key = pos.getBlockKey();
```

它序列化为简单的 `x/y/z` 节：

```yaml
x: 10
y: 64
z: -20
```

## AreaOfBlocks

两种形状都实现了
[`AreaOfBlocks`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/math/AreaOfBlocks.java)：

```java
boolean contains(BlockPos pos);
List<BlockPos> asPosList();
void forEach(Consumer<BlockPos> action);
int volume();
AreaType getType(); // CUBOID 或 SPHERE
```

对大区域，优先用 `forEach` 而非 `asPosList` —— 它避免一次性生成整个列表。

## CuboidArea

由两个角点定义的长方体。

```java
CuboidArea area = new CuboidArea(pos1, pos2);
CuboidArea fromLocs = CuboidArea.createFromLocation(loc1, loc2);

BlockPos min = area.getMin();
BlockPos max = area.getMax();
Location center = area.getCenterLocation(world);

CuboidArea bigger = area.expand(1);        // 所有轴
CuboidArea bigger2 = area.expand(2, 0, 2); // 按轴

int sx = area.sizeX(), sy = area.sizeY(), sz = area.sizeZ();
int total = area.volume();

area.forEach(p -> world.getBlockAt(p.toLocation(world)).setType(Material.AIR));
```

## SphereArea

由中心与半径定义的球体。每个半径的偏移量会被缓存，因此对同一半径反复迭代开销很低。

```java
SphereArea sphere = new SphereArea(center, 5); // 半径必须 > 0

if (sphere.contains(pos)) { /* ... */ }
SphereArea bigger = sphere.expand(2);
int blocks = sphere.volume();

sphere.forEach(p -> { /* ... */ });
```

> `contains` 与生成的集合都使用半径判定式 `dx² + dy² + dz² <= r² + r`，从而产生略微
> 圆润、看起来自然的球体。
