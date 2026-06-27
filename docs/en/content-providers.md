Content Providers
==

A **content provider** is an adapter that exposes items and blocks from another
plugin behind a single interface. MittelLib ships providers for several popular
custom-content plugins and a built-in `minecraft` provider for vanilla items.

The entry point is the
[`ContentProviders`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/hook/ContentProviders.java)
utility class.

## Supported providers

Providers are auto-detected at startup for every installed plugin whose name has a
matching hook class. Out of the box MittelLib hooks:

- **Minecraft** (always available, id `minecraft`)
- **ItemsAdder**
- **Nexo**
- **Oraxen**
- **CraftEngine**
- **MMOItems**
- **EcoItems**
- **ExecutableItems**

If the corresponding plugin is not installed, its provider is simply not
registered.

## Looking up items

Use a `provider:id` expression. When the namespace is omitted, `minecraft` is
assumed.

```java
ItemStack ruby   = ContentProviders.getItemStack("nexo:ruby");
ItemStack custom = ContentProviders.getItemStack("itemsadder:myset:gem");
ItemStack stone  = ContentProviders.getItemStack("stone"); // -> minecraft:stone
```

Reverse lookup — find which provider an item belongs to:

```java
String id = ContentProviders.getIdFromItem(itemStack); // null if unknown
```

## Looking up blocks

Custom blocks are represented by
[`PackedBlock`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/iface/block/PackedBlock.java):

```java
PackedBlock block = ContentProviders.getBlock("nexo:ruby_block");
if (block != null) {
    block.place(location);
    ItemStack asItem = block.toItem(); // may be null
}

// Identify / remove a block at a location regardless of owning plugin
PackedBlock here = ContentProviders.getBlockByLocation(location);
ContentProviders.destroyBlock(location);
```

## Tab-completion helpers

```java
List<String> itemSuggestions  = ContentProviders.getItemSuggestions();
List<String> blockSuggestions = ContentProviders.getBlockSuggestions();
List<PackedBlock> usableBlocks = ContentProviders.getAllUsableBlocks(); // blocks bound to an item
```

## Getting a provider directly

```java
ContentProvider nexo = ContentProviders.getById("nexo");
if (nexo != null) {
    ItemStack ruby = nexo.getItem("ruby");
}
```

## Implementing a custom provider

Implement the
[`ContentProvider`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/iface/ContentProvider.java)
interface. The minimum surface is:

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
    // getAllBlocks() has a default implementation
}
```

> **Auto-registration:** at startup MittelLib iterates over the installed plugins
> and registers a matching hook provider (a no-args `…ContentProvider` class) for
> each one it recognises. The built-in hooks live in the
> `io.github.lijinhong11.mittellib.hook.content` package, and the `minecraft`
> provider is always registered.
