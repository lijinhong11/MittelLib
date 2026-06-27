Utilities
==

The `utils` package collects small, dependency-free helpers used throughout the
library and handy for plugins too.

## ComponentUtils

Adventure `Component` (de)serialization with MiniMessage, legacy color codes
(`&`/`§`), hex colors and placeholder support.

```java
Component c = ComponentUtils.deserialize("<gold>Hello <bold>world");
Component c2 = ComponentUtils.deserialize(sender, "&aHi %player_name%"); // placeholders for sender
String mini  = ComponentUtils.serialize(component);
Component plain = ComponentUtils.text("no formatting, italics off");
```

Deserialization disables the default italic on item lore, parses PlaceholderAPI /
MiniPlaceholders (when present), and accepts both `&` and `§` legacy codes plus
`&#rrggbb` hex.

## StringUtils

```java
String s   = StringUtils.parsePlaceholders(sender, "Hi %player_name%");
String yes = StringUtils.toBooleanStatus(sender, true);  // localized enabled/disabled
String code = StringUtils.convertToRightLangCode("zh_cn"); // -> zh-CN

// zlib + Base64 round-trip
String packed = StringUtils.compress(longString);
String back   = StringUtils.decompress(packed);
```

## NumberUtils

```java
int u = NumberUtils.asUnsigned(-5);       // 5 (also long/double/float overloads)
Number n = NumberUtils.asNumber(obj);     // null if not a Number
String t = NumberUtils.formatSeconds(sender, 3725); // localized "1 hour 2 minutes 5 seconds"
```

## EnumUtils

Case-insensitive enum lookup used by the config and component readers.

```java
Material mat = EnumUtils.readEnum(Material.class, "diamond_block");
```

## BukkitUtils

A grab-bag of Bukkit conversions:

```java
Material mat = BukkitUtils.getMaterial("STONE");                 // null-safe
Material def = BukkitUtils.getMaterialDef("???");               // BARRIER fallback
NamespacedKey key = BukkitUtils.getNamespacedKey("minecraft:stone");

// Player-head skins
String url = BukkitUtils.getProfileSkinURL(head);
BukkitUtils.setProfileBySkinURL(head, "https://textures.minecraft.net/texture/...");

// Potion effects & locations to/from config
PotionEffect eff = BukkitUtils.readPotionEffect(section);
BukkitUtils.writeLocationSection(section, location);
Location loc = BukkitUtils.readLocation(section);
```

## Random utilities

### RandomizedList

A `List` mixin interface for quick random picks:

```java
T one = list.randomOne();
List<T> some = list.randomMulti(3);
List<T> distinct = list.randomMulti(3, true);
```

### WeightedRandomMap

A weighted picker backed by the Alias method (O(1) draws after a lazy rebuild).

```java
WeightedRandomMap<String> drops = new WeightedRandomMap<>();
drops.put("common", 80.0);
drops.put("rare", 19.0);
drops.put("legendary", 1.0);

String roll = drops.randomOne();
double p = drops.getProbability("rare");       // 0..1
String pretty = drops.getDisplayProbability("rare");
```

Weights must be positive.

### FastRandom

A fast PRNG used internally (`FastRandom.nextInt(bound)`, `FastRandom.nextDouble()`).

## Chat input

[`ChatInput`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/utils/chat/ChatInput.java)
waits for a player's next chat message and runs a callback (asynchronously).

```java
ChatInput.waitForPlayer(plugin, player, input -> {
    player.sendMessage("You typed: " + input);
});

// With a filter (e.g. ignore commands) and access to the player
ChatInput.waitForPlayer(plugin, player,
        msg -> !msg.startsWith("/"),
        (p, msg) -> { /* ... */ });
```

## MCVersion

Runtime Minecraft version detection by protocol number.

```java
MCVersion current = MCVersion.getCurrent();
if (current.isAtLeast(MCVersion.V1_20_5)) {
    // data components are available
}
boolean old = current.isLowerThan(MCVersion.V1_21_2);
```

## ModrinthUpdateChecker

Asynchronously checks Modrinth for a newer version and logs the result. Detects
Paper vs Folia and the running game version automatically.

```java
new ModrinthUpdateChecker(this, "your-modrinth-project-id").check();
```
