Items (MittelItem)
==

[`MittelItem`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/item/MittelItem.java)
is a mutable, serializable wrapper around a Bukkit `ItemStack`. It stores the
material, amount, meta, enchantments and modern data components, and can read or
write itself to a configuration section.

## Creating items

```java
// From a material
MittelItem a = new MittelItem(Material.DIAMOND);
MittelItem b = new MittelItem(Material.DIAMOND, 16);   // with amount

// From an existing ItemStack (the item must have item meta)
MittelItem c = new MittelItem(itemStack);
MittelItem d = new MittelItem(itemStack, 32);          // override amount

// From a content provider (see the Content Providers page)
MittelItem e = new MittelItem(ContentProviders.getById("nexo"), "ruby");
```

> Constructors that take an `ItemStack` throw `IllegalArgumentException` if the
> stack has no item meta. The content-provider constructor throws if the id cannot
> be resolved.

## Modifying items

`MittelItem` is a Lombok `@Data` class, so every field has a getter/setter. In
addition, several fluent helpers exist:

```java
MittelItem item = new MittelItem(Material.DIAMOND_SWORD)
        .enchant(Enchantment.SHARPNESS, 5)              // add enchantment
        .component(new FoodComponent(4, 2.4f, true))    // add a typed component
        .component(DataComponentTypes.GLIDER, null);    // add a raw Paper component

item.getMeta().setDisplayName("<gold>Excalibur");       // MiniMessage / legacy color
item.getMeta().setLore("<gray>A legendary blade");
```

The display name and lore are parsed through
[`ComponentUtils`](utilities.md#componentutils), so MiniMessage tags, legacy `&`/`§`
codes and hex colors all work.

## Producing an `ItemStack`

```java
ItemStack stack = item.get();
```

`get()` rebuilds the stack from scratch: it resolves the provider item (if any),
applies the meta, then applies every component. It throws if the amount is `<= 0`.

## Reading & writing configuration

`MittelItem` implements [`ReadWriteObject`](configuration.md#readwriteobject):

```java
// Write
MittelItem item = new MittelItem(itemStack);
item.write(section);

// Read
MittelItem loaded = MittelItem.readFromSection(section);
ItemStack stack = loaded.get();
```

A serialized item looks roughly like this:

```yaml
material: DIAMOND_SWORD
amount: 1
enchantments:
  minecraft:sharpness: 5
meta:
  displayName: "<gold>Excalibur"
  itemFlags:
    - HIDE_ENCHANTS
components:
  food:
    nutrition: 4
    saturation: 2.4
    canAlwaysEat: true
```

When the item comes from a content provider, `provider` and the provider item id
are written instead of a vanilla `material`:

```yaml
provider: nexo
id: ruby
amount: 1
```

> **Version note:** data components are only read/written on Minecraft `1.20.5+`
> (`MCVersion.V1_20_5`). On older servers the `components` section is ignored.

## Item meta

The [`MittelItemMeta`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/item/MittelItemMeta.java)
class captures display name, lore, item flags, the unbreakable flag, legacy custom
model data and several specialized meta definitions:

- `BannerDefinition` — banner patterns
- `SkullDefinition` — player head profiles
- `FireworkDefinition` — firework effects
- `MapDefinition` — map data
- `PotionDefinition` — potion effects
- leather armor color

Each definition is itself a `ReadWriteObject`, so it serializes into a nested
section under `meta`.

> Custom model data via `MittelItemMeta#customModelData` is **deprecated** for
> `1.21.4+`; use the [`CustomModelDataComponent`](item-components.md) instead.
