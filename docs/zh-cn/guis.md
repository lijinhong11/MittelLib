GUI 界面
==

MittelLib 通过
[`MittelGUI`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/gui/MittelGUI.java)
接口提供基于构建器的容器 GUI。点击处理由 MittelLib 在启用时注册的全局监听器自动完成
—— 你只需描述布局以及每个物品的行为。

提供三种 GUI 类型：

- **箱子（Chest）** —— 静态箱子界面。
- **分页箱子（Paginated chest）** —— 带有内容区、可跨多页翻页的箱子。
- **铁砧（Anvil）** —— 铁砧界面（实验性）。

## GUI 物品

格子由 `MittelGUIItem` 填充。便捷的实现是
[`ButtonItem`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/gui/item/ButtonItem.java)：

```java
// 一个点击时执行代码的按钮。
// BiFunction 返回是否“允许”这次点击（true）或取消（false）。
MittelGUIItem run = ButtonItem.clickable(icon, (gui, event) -> {
    Player p = (Player) event.getWhoClicked();
    p.sendMessage("Clicked!");
    return false; // 取消点击（不让玩家把物品拿走）
});

// 一个打开另一个 GUI 的按钮
MittelGUIItem open = ButtonItem.openGUI(icon, otherGui);

// 一个纯装饰、不可交互的物品
MittelGUIItem filler = ButtonItem.unclickable(pane);
```

你也可以直接实现 `MittelGUIItem`；其 `onClick` 返回 `true` 表示允许点击，`false`
表示取消（默认 `true`）。

## 箱子 GUI

布局用**结构（structure）**描述：每行一个字符串，每个字符绑定一个物品；空格为空格子。

```java
ChestGUI gui = MittelGUI.chestBuilder()
        .title(Component.text("Menu"))
        .structure(
                "#########",
                "#   A   #",
                "#########")
        .bind('#', ButtonItem.unclickable(filler))
        .bind('A', ButtonItem.clickable(icon, (g, e) -> { /* ... */ return false; }))
        .onOpen((player, g) -> { /* ... */ })
        .onClose((player, g) -> { /* ... */ })
        .build();

gui.open(player);
```

- 每行结构最多 9 个字符；行数必须为 1–6。
- 省略 `size`（或不是 9 的倍数）时，默认为 `行数 * 9`。
- 构建后可用 `putItem(slot, item)` 与 `removeItem(slot)` 动态修改格子。

## 分页箱子 GUI

将某个字符标记为**内容区**；MittelLib 会把分页物品铺到所有匹配的格子，并处理翻页。

```java
PaginatedChestGUI gui = MittelGUI.pagedChestBuilder()
        .title(Component.text("Shop"))
        .structure(
                "#########",
                "#OOOOOOO#",
                "#P     N#")
        .bind('#', ButtonItem.unclickable(filler))
        .content('O')                          // 内容格子
        .previousPage('P', ButtonItem.unclickable(prevIcon))
        .nextPage('N', ButtonItem.unclickable(nextIcon))
        .items(myItems)                        // 初始分页物品
        .build();

gui.open(player);
```

运行时辅助方法：

```java
gui.currentPage();          // 从 0 开始
gui.pageCount();
gui.setPage(2);
gui.nextPage();
gui.previousPage();
gui.addPageItem(item);
gui.setPageItems(items);
gui.contentSlots();         // 解析出的内容格子
```

上一页/下一页按钮会被自动接管 —— 点击它们即可翻页。必须设置内容绑定字符，且至少
要有一个内容格子，否则 `build()` 会抛出异常。

## 铁砧 GUI（实验性）

```java
AnvilGUI gui = MittelGUI.anvilBuilder()
        .title(Component.text("Rename"))
        .firstItem(ButtonItem.unclickable(input))
        .resultItem(ButtonItem.clickable(result, (g, e) -> { /* ... */ return false; }))
        .prepareListener((player, view) -> {
            String text = view.getRenameText();
            // 根据输入的文本更新结果物品
        })
        .onOpen((player, g) -> { /* ... */ })
        .onClose((player, g) -> { /* ... */ })
        .build();

gui.open(player);
```

铁砧 GUI 暴露 `0`（第一格）、`1`（第二格）、`2`（结果格）三个格子，以及一个在玩家
输入时触发、可访问 `AnvilView` 的 `prepareListener`。

> 铁砧构建器标注了 `@ApiStatus.Experimental`，将来可能变动。
