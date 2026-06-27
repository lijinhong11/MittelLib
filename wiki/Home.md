MittelLib Wiki
==

English | [简体中文](Home-zh)

**MittelLib** is a utility library for Paper/Folia Minecraft plugins. It bundles the
boilerplate that most server plugins re-implement: item (de)serialization, modern
data-component handling, cross-plugin item/block providers, inventory GUIs,
localization, configuration helpers, area math and a handful of small utilities.

> **Requirements:** Java 21+, a Paper (or Folia) server. Built against the Paper
> `1.21.x` API; many features automatically degrade on older versions through the
> [`MCVersion`](Utilities#mcversion) detection.

## Pages

| Page | What it covers |
| --- | --- |
| [Getting Started](Getting-Started) | Adding MittelLib as a dependency and the first lines of code |
| [Items (MittelItem)](Items) | Building, reading and writing items to config |
| [Item Components](Item-Components) | Modern `1.20.5+` data components (food, tool, profile, …) |
| [Content Providers](Content-Providers) | Items & blocks from ItemsAdder, Nexo, Oraxen, MMOItems, … |
| [GUIs](GUIs) | Chest, paginated chest and anvil inventory builders |
| [Localization](Localization) | Multi-language messages and the language managers |
| [Configuration](Configuration) | `MittelConfig` and the `ReadWriteObject` contract |
| [Areas & Math](Areas-and-Math) | `BlockPos`, cuboid and sphere areas |
| [Utilities](Utilities) | Strings, components, numbers, random, chat input, update checks |
| [Placeholders](Placeholders) | One expansion for both PlaceholderAPI and MiniPlaceholders |

## Quick example

```java
// Wrap an existing ItemStack, add a component, get it back
MittelItem item = new MittelItem(itemStack);
item.component(new FoodComponent(4, 2.4f, true));
ItemStack result = item.get();

// Fetch an item from another plugin
ItemStack nexoItem = ContentProviders.getItemStack("nexo:ruby");
```

## Feature overview

1. Item serialization / deserialization
2. Item component serialization / deserialization (supports almost all Minecraft components)
3. A universal placeholder expansion for MiniPlaceholders and PlaceholderAPI
4. Items & blocks from other plugins (ItemsAdder, Nexo, etc.)
5. Inventory GUI builders, localization, area math and more

## License

MittelLib is distributed under the **GPL-3.0** license. See
[`LICENSE.md`](https://github.com/lijinhong11/MittelLib/blob/main/LICENSE.md).
