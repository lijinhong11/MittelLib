变量 / 占位符
==

MittelLib 让你**只注册一次**占位符，就能同时暴露给
[PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) 和
[MiniPlaceholders](https://github.com/MiniPlaceholders/MiniPlaceholders)，这通过
[`UniversalPlaceholderExpansion`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/hook/placeholder/UniversalPlaceholderExpansion.java)
实现。

## 定义一个扩展

继承该类，声明 identifier/author/version，在构造函数中注册占位符，然后调用
`register()`：

```java
public class MyExpansion extends UniversalPlaceholderExpansion {
    public MyExpansion() {
        registerPlaceholder("coins", PlaceholderType.AUDIENCE,
                (viewer, target, args) -> String.valueOf(getCoins(viewer)));

        registerPlaceholder("server_name", PlaceholderType.GLOBAL,
                (viewer, target, args) -> "Hub");

        registerPlaceholder("is_friend", PlaceholderType.RELATIONAL,
                (viewer, target, args) -> areFriends(viewer, target) ? "yes" : "no");
    }

    @Override public String identifier() { return "myplugin"; }
    @Override public String author()     { return "me"; }
    @Override public String version()    { return "1.0.0"; }
}

// 在 onEnable 中：
new MyExpansion().register();
```

`register()` 会根据已安装的插件，注册到 PlaceholderAPI 和/或 MiniPlaceholders。请在
调用它**之前**注册好所有占位符 —— 之后再注册会抛出 `IllegalStateException`。

## 占位符类型

| 类型 | PlaceholderAPI | MiniPlaceholders | 处理器参数 |
| --- | --- | --- | --- |
| `GLOBAL` | 非关系型 | global | viewer = null |
| `AUDIENCE` | 非关系型 | audience（按玩家） | viewer = 该玩家 |
| `RELATIONAL` | 关系型 | relational | viewer + target |

处理器签名为：

```java
String parse(@Nullable OfflinePlayer viewer, @Nullable OfflinePlayer target, String[] args);
```

## 在消息中使用

- **PlaceholderAPI：** `%myplugin_coins%`、`%myplugin_server_name%`。多余的下划线
  分段会作为 `args` 传入，例如 `%myplugin_top_3%` → 对 `top` 占位符
  `args = ["3"]`。
- **MiniPlaceholders / MiniMessage：** `<myplugin_coins>`。返回的字符串本身会被当作
  MiniMessage 再次解析，因此你可以返回带格式的文本。

## 占位符在其他地方如何被解析

[`ComponentUtils.deserialize`](utilities.md#componentutils) 与
[`StringUtils.parsePlaceholders`](utilities.md#stringutils) 会在 PlaceholderAPI 与
MiniPlaceholders 存在时自动解析它们，因此任何经由[本地化](localization.md)系统发送的
消息都开箱即用地支持占位符。
