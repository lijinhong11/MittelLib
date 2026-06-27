MittelLib 维基
==

[English](Home) | 简体中文

**MittelLib** 是一个面向 Paper/Folia 的 Minecraft 插件工具库。它把大多数服务器插件
都要重复实现的样板代码封装好了：物品序列化/反序列化、现代数据组件处理、跨插件的
物品/方块提供者、容器 GUI、本地化、配置辅助、区域数学以及一系列小工具。

> **环境要求：** Java 21+，Paper（或 Folia）服务端。基于 Paper `1.21.x` API 构建；
> 许多功能会通过 [`MCVersion`](Utilities-zh#mcversion) 版本检测在旧版本上自动降级。

## 页面索引

| 页面 | 内容 |
| --- | --- |
| [快速开始](Getting-Started-zh) | 引入 MittelLib 依赖与第一段代码 |
| [物品 (MittelItem)](Items-zh) | 构建物品、读写到配置 |
| [物品组件](Item-Components-zh) | `1.20.5+` 现代数据组件（食物、工具、皮肤等） |
| [内容提供者](Content-Providers-zh) | 来自 ItemsAdder、Nexo、Oraxen、MMOItems 等的物品/方块 |
| [GUI 界面](GUIs-zh) | 箱子、分页箱子、铁砧界面构建器 |
| [本地化](Localization-zh) | 多语言消息与语言管理器 |
| [配置文件](Configuration-zh) | `MittelConfig` 与 `ReadWriteObject` 约定 |
| [区域与数学](Areas-and-Math-zh) | `BlockPos`、长方体与球形区域 |
| [工具类](Utilities-zh) | 字符串、组件、数字、随机、聊天输入、更新检查 |
| [变量/占位符](Placeholders-zh) | 一次注册，同时支持 PlaceholderAPI 与 MiniPlaceholders |

## 快速示例

```java
// 包装一个已有的 ItemStack，添加组件，再取回
MittelItem item = new MittelItem(itemStack);
item.component(new FoodComponent(4, 2.4f, true));
ItemStack result = item.get();

// 获取其他插件的物品
ItemStack nexoItem = ContentProviders.getItemStack("nexo:ruby");
```

## 功能概览

1. 物品序列化 / 反序列化
2. 物品组件序列化 / 反序列化（支持几乎所有 Minecraft 组件）
3. 同时支持 MiniPlaceholders 与 PlaceholderAPI 的通用占位符扩展
4. 来自其他插件的物品与方块（ItemsAdder、Nexo 等）
5. 容器 GUI 构建器、本地化、区域数学等

## 许可证

MittelLib 以 **GPL-3.0** 许可证发布，见
[`LICENSE.md`](https://github.com/lijinhong11/MittelLib/blob/main/LICENSE.md)。
