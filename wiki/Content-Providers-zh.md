内容提供者
==

[English](Content-Providers) | 简体中文

**内容提供者（content provider）**是一个适配器，把其他插件的物品和方块通过统一的
接口暴露出来。MittelLib 为几款流行的自定义内容插件内置了提供者，并为原版物品提供了
内置的 `minecraft` 提供者。

入口是
[`ContentProviders`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/hook/ContentProviders.java)
工具类。

## 支持的提供者

启动时，会为每个已安装且存在对应挂钩类的插件自动检测并注册提供者。开箱即用地支持：

- **Minecraft**（始终可用，id 为 `minecraft`）
- **ItemsAdder**
- **Nexo**
- **Oraxen**
- **CraftEngine**
- **MMOItems**
- **EcoItems**
- **ExecutableItems**

如果对应插件未安装，其提供者就不会被注册。

## 查找物品

使用 `provider:id` 表达式。省略命名空间时默认为 `minecraft`。

```java
ItemStack ruby   = ContentProviders.getItemStack("nexo:ruby");
ItemStack custom = ContentProviders.getItemStack("itemsadder:myset:gem");
ItemStack stone  = ContentProviders.getItemStack("stone"); // -> minecraft:stone
```

反向查找 —— 找出物品属于哪个提供者：

```java
String id = ContentProviders.getIdFromItem(itemStack); // 未知时返回 null
```

## 查找方块

自定义方块由
[`PackedBlock`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/iface/block/PackedBlock.java)
表示：

```java
PackedBlock block = ContentProviders.getBlock("nexo:ruby_block");
if (block != null) {
    block.place(location);
    ItemStack asItem = block.toItem(); // 可能为 null
}

// 无论属于哪个插件，识别/移除某位置的方块
PackedBlock here = ContentProviders.getBlockByLocation(location);
ContentProviders.destroyBlock(location);
```

## Tab 补全辅助

```java
List<String> itemSuggestions  = ContentProviders.getItemSuggestions();
List<String> blockSuggestions = ContentProviders.getBlockSuggestions();
List<PackedBlock> usableBlocks = ContentProviders.getAllUsableBlocks(); // 绑定了物品的方块
```

## 直接获取提供者

```java
ContentProvider nexo = ContentProviders.getById("nexo");
if (nexo != null) {
    ItemStack ruby = nexo.getItem("ruby");
}
```

## 实现自定义提供者

实现
[`ContentProvider`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/iface/ContentProvider.java)
接口。最小接口如下：

```java
public interface ContentProvider {
    String getId();
    ItemStack getItem(String id);
    String getIdFromItem(ItemStack item);
    PackedBlock getBlock(String id);
    void destroyBlock(Location loc);
    List<String> getItemSuggestions();
    List<String> getBlockSuggestions();
    PackedBlock getBlockByLocation(Location loc);
    // getAllBlocks() 有默认实现
}
```

> **自动注册：** 启动时 MittelLib 会遍历已安装的插件，为每个它能识别的插件注册一个
> 匹配的挂钩提供者（无参构造的 `…ContentProvider` 类）。内置挂钩位于
> `io.github.lijinhong11.mittellib.hook.content` 包中，`minecraft` 提供者始终被注册。
