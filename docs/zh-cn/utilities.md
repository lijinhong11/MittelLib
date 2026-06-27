工具类
==

`utils` 包收集了一批小巧、无额外依赖的辅助方法，库内部到处都在用，对插件也很方便。

## ComponentUtils

Adventure `Component` 的（反）序列化，支持 MiniMessage、旧版颜色代码（`&`/`§`）、
十六进制颜色与占位符。

```java
Component c = ComponentUtils.deserialize("<gold>Hello <bold>world");
Component c2 = ComponentUtils.deserialize(sender, "&aHi %player_name%"); // 针对 sender 解析占位符
String mini  = ComponentUtils.serialize(component);
Component plain = ComponentUtils.text("无格式，关闭斜体");
```

反序列化会关闭物品 lore 默认的斜体，解析 PlaceholderAPI / MiniPlaceholders（存在时），
并同时接受 `&` 与 `§` 旧版代码以及 `&#rrggbb` 十六进制。

## StringUtils

```java
String s   = StringUtils.parsePlaceholders(sender, "Hi %player_name%");
String yes = StringUtils.toBooleanStatus(sender, true);  // 本地化的“已启用/已禁用”
String code = StringUtils.convertToRightLangCode("zh_cn"); // -> zh-CN

// zlib + Base64 往返
String packed = StringUtils.compress(longString);
String back   = StringUtils.decompress(packed);
```

## NumberUtils

```java
int u = NumberUtils.asUnsigned(-5);       // 5（也有 long/double/float 重载）
Number n = NumberUtils.asNumber(obj);     // 不是 Number 时返回 null
String t = NumberUtils.formatSeconds(sender, 3725); // 本地化的“1 小时 2 分钟 5 秒”
```

## EnumUtils

供配置与组件读取器使用的大小写不敏感枚举查找。

```java
Material mat = EnumUtils.readEnum(Material.class, "diamond_block");
```

## BukkitUtils

一组 Bukkit 转换的杂项工具：

```java
Material mat = BukkitUtils.getMaterial("STONE");                 // 空安全
Material def = BukkitUtils.getMaterialDef("???");               // 回退为 BARRIER
NamespacedKey key = BukkitUtils.getNamespacedKey("minecraft:stone");

// 玩家头颅皮肤
String url = BukkitUtils.getProfileSkinURL(head);
BukkitUtils.setProfileBySkinURL(head, "https://textures.minecraft.net/texture/...");

// 药水效果与坐标 与配置互转
PotionEffect eff = BukkitUtils.readPotionEffect(section);
BukkitUtils.writeLocationSection(section, location);
Location loc = BukkitUtils.readLocation(section);
```

## 随机工具

### RandomizedList

一个用于快速随机取值的 `List` 混入接口：

```java
T one = list.randomOne();
List<T> some = list.randomMulti(3);
List<T> distinct = list.randomMulti(3, true);
```

### WeightedRandomMap

基于别名法（Alias method）的加权选择器（惰性重建后每次抽取 O(1)）。

```java
WeightedRandomMap<String> drops = new WeightedRandomMap<>();
drops.put("common", 80.0);
drops.put("rare", 19.0);
drops.put("legendary", 1.0);

String roll = drops.randomOne();
double p = drops.getProbability("rare");       // 0..1
String pretty = drops.getDisplayProbability("rare");
```

权重必须为正数。

### FastRandom

库内部使用的快速伪随机数发生器（`FastRandom.nextInt(bound)`、`FastRandom.nextDouble()`）。

## 聊天输入

[`ChatInput`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/utils/chat/ChatInput.java)
等待玩家的下一条聊天消息并执行回调（异步）。

```java
ChatInput.waitForPlayer(plugin, player, input -> {
    player.sendMessage("You typed: " + input);
});

// 带过滤器（如忽略命令）并可访问玩家对象
ChatInput.waitForPlayer(plugin, player,
        msg -> !msg.startsWith("/"),
        (p, msg) -> { /* ... */ });
```

## MCVersion

按协议号进行的运行时 Minecraft 版本检测。

```java
MCVersion current = MCVersion.getCurrent();
if (current.isAtLeast(MCVersion.V1_20_5)) {
    // 数据组件可用
}
boolean old = current.isLowerThan(MCVersion.V1_21_2);
```

## ModrinthUpdateChecker

异步检查 Modrinth 上是否有新版本并记录结果。会自动检测 Paper / Folia 以及正在运行的
游戏版本。

```java
new ModrinthUpdateChecker(this, "your-modrinth-project-id").check();
```
