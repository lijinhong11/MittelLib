物品组件
==

[English](Item-Components) | 简体中文

Minecraft `1.20.5` 用**数据组件（data components）**取代了大多数物品 NBT 数据。
MittelLib 封装了 Paper 的数据组件 API，使组件既可在代码中创建，也可与配置互相序列化。

所有组件类都继承
[`ReadWriteItemComponent`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/configuration/ReadWriteItemComponent.java)，
它在 [`ReadWriteObject`](Configuration-zh#readwriteobject) 约定之上增加了
`applyToItem(ItemStack)` 方法。

> **版本限制：** 每个组件都声明了最低 Minecraft 版本。在低于该版本的服务端上，
> 组件会被跳过而不是抛出异常。整体的组件功能需要 `1.20.5+`。

## 给物品添加组件

```java
MittelItem item = new MittelItem(Material.APPLE);

// 1. 类型化组件实例
item.component(new FoodComponent(4, 2.4f, true));

// 2. 从已有的 Paper 组件值转换而来
ResolvableProfile profile = ...;
item.component(ProfileComponent.fromMinecraftComponent(profile));

// 3. 原始 Paper DataComponentType + 上下文
item.component(DataComponentTypes.MAX_STACK_SIZE, 16);

ItemStack stack = item.get(); // 组件在此处被应用
```

每个具体组件还提供：

- `static X fromMinecraftComponent(<paper 值>)` —— 从实时 Paper 值构建。
- `static X readFromSection(ConfigurationSection)` —— 从配置构建。
- `static DataComponentType getDataComponentType()` —— 它映射到的 Paper 类型。

## 完整组件

这些是带有各自配置结构的完整类（括号内为 YAML 键）：

| 键 | 类 | 最低版本 |
| --- | --- | --- |
| `food` | `FoodComponent` | 1.20.5 |
| `profile` | `ProfileComponent` | 1.20.5 |
| `potionContents` | `PotionContentsComponent` | 1.20.5 |
| `containerLoot` | `ContainerLootComponent` | 1.20.5 |
| `lodestoneTracker` | `LodestoneTrackerComponent` | 1.20.5 |
| `tool` | `ToolComponent` | 1.20.5 |
| `trim` | `ArmorTrimComponent` | 1.21.2 |
| `instrument` | `InstrumentComponent` | 1.21.2 |
| `damageResistant` | `DamageResistantComponent` | 1.21.2 |
| `useCooldown` | `UseCooldownComponent` | 1.21.2 |
| `tooltipDisplay` | `TooltipDisplayComponent` | 1.21.2 |
| `consumable` | `ConsumableComponent` | 1.21.2 |
| `equippable` | `EquippableComponent` | 1.21.2 |
| `useRemainder` | `UseRemainderComponent` | 1.21.2 |
| `deathProtection` | `DeathProtectionComponent` | 1.21.2 |
| `modelData` | `CustomModelDataComponent` | 1.21.4 |
| `weapon` | `WeaponComponent` | 1.21.5 |
| `attackRange` | `AttackRangeComponent` | 1.21.11 |
| `kineticWeapon` | `KineticWeaponComponent` | 1.21.11 |
| `piercingWeapon` | `PiercingWeaponComponent` | 1.21.11 |
| `useEffects` | `UseEffectsComponent` | 1.21.11 |

### 示例：`food`

```java
// 营养值、饱和度、是否总能食用
item.component(new FoodComponent(4, 2.4f, true));
```

```yaml
components:
  food:
    nutrition: 4
    saturation: 2.4
    canAlwaysEat: true
```

### 示例：`profile`（玩家头颅）

```java
item.component(ProfileComponent.fromMinecraftComponent(resolvableProfile));
```

```yaml
components:
  profile:
    id: "00000000-0000-0000-0000-000000000000"
    name: "Notch"
    properties:
      - name: "textures"
        value: "<base64>"
        signature: "<signature>"
    skin:
      model: CLASSIC   # 或 SLIM
```

## 简单组件

基本类型与命名空间键类型的组件，会被通用地注册为
[`SimpleItemComponent`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/item/components/impl/SimpleItemComponent.java)。
它们的 YAML 键直接保存值（没有嵌套节）：

| 键 | 值类型 | 最低版本 |
| --- | --- | --- |
| `damage` | int | 1.20.5 |
| `maxDamage` | int | 1.20.5 |
| `maxStackSize` | int | 1.20.5 |
| `enchantable` | int | 1.20.5 |
| `baseColor` | `DyeColor` 名称 | 1.20.5 |
| `rarity` | `ItemRarity` 名称 | 1.20.5 |
| `jukeboxPlayable` | 命名空间键 | 1.21.1 |
| `glider` | boolean | 1.21.2 |
| `itemModel` | 命名空间键 | 1.21.2 |
| `tooltipStyle` | 命名空间键 | 1.21.2 |
| `damageType` | 命名空间键 | 1.21.11 |
| `intangibleProjectile` | boolean | 1.21.11 |
| `minimumAttackCharge` | float | 1.21.11 |

```yaml
components:
  maxStackSize: 16
  rarity: EPIC
  glider: true
  itemModel: "minecraft:elytra"
```

## 编写你自己的组件

继承 `ReadWriteItemComponent`，用 `@ItemComponentSpec` 注解它，并提供序列化器所需的
三个静态方法：

```java
@ItemComponentSpec(key = "myComponent", requiredVersion = MCVersion.V1_20_5)
public class MyComponent extends ReadWriteItemComponent {
    public static DataComponentType getDataComponentType() { return DataComponentTypes.XXX; }
    public static MyComponent fromMinecraftComponent(SomeValue value) { ... }
    public static MyComponent readFromSection(ConfigurationSection cs) { ... }

    @Override public void applyToItem(ItemStack item) { ... }
    @Override public void write(ConfigurationSection cs) { ... }
}
```

> 注解中的 `key` 与 `requiredVersion` 会在运行时由内部的 `ItemComponentSerializer`
> 读取。内置组件位于 `io.github.lijinhong11.mittellib.item.components.impl` 包中。
