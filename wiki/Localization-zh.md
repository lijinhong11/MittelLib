本地化
==

[English](Localization) | 简体中文

MittelLib 内置了围绕
[`ILanguageManager`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/message/ILanguageManager.java)
接口构建的本地化框架。它加载各语言的 YAML 文件、为每个玩家选择正确的语言、解析
MiniMessage / 旧版颜色代码与占位符，并发送 Adventure `Component`。

## 获取语言管理器

```java
// 该库自身的管理器
SyncLanguageManager lib = MittelLib.getInstance().getLanguageManager();

// 绑定到“你的”插件的管理器。对于你文件中未定义的键，会回退到该库的管理器。
SyncLanguageManager mine = MittelLib.getInstance().getLanguageManager(myPlugin);
```

### `SyncLanguageManager` 与 `LocalLanguageManager`

| | `SyncLanguageManager` | `LocalLanguageManager` |
| --- | --- | --- |
| 来源 | 把 jar 内的 `language/*.yml` 复制到插件目录 | 只读取插件目录中已有的文件 |
| 自动补全 | 可选地用内置默认值补全缺失键 | 否 |
| 典型用途 | 大多数插件 | 由服主提供语言文件的场景 |

两者都从 `<插件数据目录>/language/*.yml` 读取语言文件，默认语言为 `en-US`。

## 语言文件

在 `src/main/resources/language/` 下为每种语言放一个 YAML 文件，例如
`en-US.yml`、`zh-CN.yml`。键为点分路径：

```yaml
common:
  no-permission: "&c你没有权限这么做！"
  enabled: "&a已启用"
  location-format: "&fX: &b%x%, &fY: &b%y%, &fZ: &b%z%"
```

内置语言包括 `en-US`、`zh-CN`、`zh-TW`、`zh-HK`、`fr-FR`、`ru-RU`、`es-ES`、`pt-BR`。

## 发送消息

```java
// 发送单行
mine.sendMessage(sender, "common.no-permission");

// 发送列表（列表每一项作为一条消息）
mine.sendMessages(sender, "help.lines");

// 带占位符：MessageReplacement.replace(token, value)
mine.sendMessage(sender, "shop.bought",
        MessageReplacement.replace("%item%", "Diamond"),
        MessageReplacement.replace("%price%", "10"));

// 带点击事件
mine.sendMessage(sender, "menu.open",
        ClickEvent.runCommand("/menu"));
```

## 获取值（而非发送）

```java
String  raw      = mine.getMsg(sender, "common.enabled");
Component comp   = mine.getMsgComponent(sender, "common.enabled");
List<String> raw2 = mine.getMsgList(sender, "help.lines");
List<Component> c = mine.getMsgComponentList(sender, "help.lines");

// 无视查看者，强制指定语言
String zh = mine.getMsgByLanguage("zh-CN", "common.enabled");
```

## 本地化物品

构建一个名称与 lore 来自配置节（`<sectionKey>.name` 与 `<sectionKey>.lore`）的物品：

```java
ItemStack item = mine.getMessagedItem(Material.PAPER, "gui.info", player,
        MessageReplacement.replace("%count%", "3"));
```

## 语言选择

默认情况下，管理器使用**玩家客户端语言**（`detectPlayerLocale`）。当关闭检测时，
或对于非玩家发送者，会回退到插件配置中的 `language` 键，再回退到默认语言。

`LocalLanguageManager` 通过 `Options` 对象来调整这些行为：

```java
ILanguageManager.Options options = new ILanguageManager.Options();
options.setDetectPlayerLocale(true);
options.setDefaultLanguage("en-US");
options.setLanguageSetterKey("language"); // 保存强制语言的配置键
```

## 其他辅助方法

```java
mine.reload();                        // 从磁盘重新加载所有语言文件
mine.getTranslationKeys();            // 默认语言中定义的所有键
mine.getParsedLocation(sender, loc);  // 通过 common.location-format 得到 "X: .., Y: .., Z: .."
```
