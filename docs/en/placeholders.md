Placeholders
==

MittelLib lets you register placeholders **once** and expose them to both
[PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) and
[MiniPlaceholders](https://github.com/MiniPlaceholders/MiniPlaceholders) at the
same time, via
[`UniversalPlaceholderExpansion`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/hook/placeholder/UniversalPlaceholderExpansion.java).

## Defining an expansion

Extend the class, declare the identifier/author/version, register placeholders in
the constructor, and call `register()`:

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

// On enable:
new MyExpansion().register();
```

`register()` registers with PlaceholderAPI and/or MiniPlaceholders depending on
which plugins are installed. Register all placeholders **before** calling it —
registering afterwards throws `IllegalStateException`.

## Placeholder types

| Type | PlaceholderAPI | MiniPlaceholders | Handler args |
| --- | --- | --- | --- |
| `GLOBAL` | non-relational | global | viewer = null |
| `AUDIENCE` | non-relational | audience (per player) | viewer = the player |
| `RELATIONAL` | relational | relational | viewer + target |

The handler signature is:

```java
String parse(@Nullable OfflinePlayer viewer, @Nullable OfflinePlayer target, String[] args);
```

## Usage in messages

- **PlaceholderAPI:** `%myplugin_coins%`, `%myplugin_server_name%`. Extra
  underscore-separated parts are passed as `args`, e.g. `%myplugin_top_3%` →
  `args = ["3"]` for the `top` placeholder.
- **MiniPlaceholders / MiniMessage:** `<myplugin_coins>`. Returned strings are
  themselves parsed as MiniMessage, so you can return formatted text.

## How placeholders are parsed elsewhere

[`ComponentUtils.deserialize`](utilities.md#componentutils) and
[`StringUtils.parsePlaceholders`](utilities.md#stringutils) automatically resolve
PlaceholderAPI and MiniPlaceholders when those plugins are present, so any message
sent through the [localization](localization.md) system supports them out of the box.
