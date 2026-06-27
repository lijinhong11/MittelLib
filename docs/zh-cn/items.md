物品 (MittelItem)
==

[`MittelItem`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/item/MittelItem.java)
是对 Bukkit `ItemStack` 的可变、可序列化封装。它保存材质、数量、meta、附魔以及
现代数据组件，并能把自身读写到配置节（ConfigurationSection）。

## 创建物品

```java
// 通过材质
MittelItem a = new MittelItem(Material.DIAMOND);
MittelItem b = new MittelItem(Material.DIAMOND, 16);   // 指定数量

// 通过已有的 ItemStack（该物品必须带有 item meta）
MittelItem c = new MittelItem(itemStack);
MittelItem d = new MittelItem(itemStack, 32);          // 覆盖数量

// 通过内容提供者（见“内容提供者”页面）
MittelItem e = new MittelItem(ContentProviders.getById("nexo"), "ruby");
```

> 接收 `ItemStack` 的构造函数在物品没有 item meta 时会抛出
> `IllegalArgumentException`。内容提供者构造函数在 id 无法解析时会抛出异常。

## 修改物品

`MittelItem` 是 Lombok 的 `@Data` 类，所有字段都有 getter/setter。此外还提供了
若干链式辅助方法：

```java
MittelItem item = new MittelItem(Material.DIAMOND_SWORD)
        .enchant(Enchantment.SHARPNESS, 5)              // 添加附魔
        .component(new FoodComponent(4, 2.4f, true))    // 添加类型化组件
        .component(DataComponentTypes.GLIDER, null);    // 添加原始 Paper 组件

item.getMeta().setDisplayName("<gold>Excalibur");       // MiniMessage / 旧版颜色
item.getMeta().setLore("<gray>A legendary blade");
```

显示名与 lore 都会经过 [`ComponentUtils`](utilities.md#componentutils) 解析，
因此 MiniMessage 标签、旧版 `&`/`§` 代码以及十六进制颜色都可用。

## 生成 `ItemStack`

```java
ItemStack stack = item.get();
```

`get()` 会从头重建物品：解析提供者物品（如果有）、应用 meta，再应用每个组件。
当数量 `<= 0` 时会抛出异常。

## 读写配置

`MittelItem` 实现了 [`ReadWriteObject`](configuration.md#readwriteobject)：

```java
// 写入
MittelItem item = new MittelItem(itemStack);
item.write(section);

// 读取
MittelItem loaded = MittelItem.readFromSection(section);
ItemStack stack = loaded.get();
```

序列化后的物品大致如下：

```yaml
material: DIAMOND_SWORD
amount: 1
enchantments:
  minecraft:sharpness: 5
meta:
  displayName: "<gold>Excalibur"
  itemFlags:
    - HIDE_ENCHANTS
components:
  food:
    nutrition: 4
    saturation: 2.4
    canAlwaysEat: true
```

当物品来自内容提供者时，写入的是 `provider` 和提供者物品 id，而不是原版 `material`：

```yaml
provider: nexo
id: ruby
amount: 1
```

> **版本说明：** 数据组件仅在 Minecraft `1.20.5+`（`MCVersion.V1_20_5`）上读写。
> 在更旧的服务端上，`components` 节会被忽略。

## 物品 meta

[`MittelItemMeta`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/item/MittelItemMeta.java)
类涵盖显示名、lore、物品标志（item flag）、不可破坏标志、旧版自定义模型数据，以及
若干专用 meta 定义：

- `BannerDefinition` —— 旗帜图案
- `SkullDefinition` —— 玩家头颅档案
- `FireworkDefinition` —— 烟花效果
- `MapDefinition` —— 地图数据
- `PotionDefinition` —— 药水效果
- 皮革护甲颜色

每个定义本身都是 `ReadWriteObject`，因此会序列化为 `meta` 下的嵌套节。

> 在 `1.21.4+` 上，`MittelItemMeta#customModelData` 自定义模型数据已**弃用**；
> 请改用 [`CustomModelDataComponent`](item-components.md)。
