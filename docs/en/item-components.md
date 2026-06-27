Item Components
==

Minecraft `1.20.5` replaced most NBT item data with **data components**. MittelLib
wraps the Paper data-component API so that components can be created in code and
serialized to / from configuration.

All component classes extend
[`ReadWriteItemComponent`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/configuration/ReadWriteItemComponent.java),
which adds an `applyToItem(ItemStack)` method on top of the
[`ReadWriteObject`](configuration.md#readwriteobject) contract.

> **Version gating:** every component declares a minimum Minecraft version. On a
> server older than that version the component is skipped instead of throwing.
> Component handling as a whole requires `1.20.5+`.

## Adding components to an item

```java
MittelItem item = new MittelItem(Material.APPLE);

// 1. Typed component instance
item.component(new FoodComponent(4, 2.4f, true));

// 2. Convert from an existing Paper component value
ResolvableProfile profile = ...;
item.component(ProfileComponent.fromMinecraftComponent(profile));

// 3. Raw Paper DataComponentType + context
item.component(DataComponentTypes.MAX_STACK_SIZE, 16);

ItemStack stack = item.get(); // components are applied here
```

Each concrete component also exposes:

- `static X fromMinecraftComponent(<paper value>)` — build from a live Paper value.
- `static X readFromSection(ConfigurationSection)` — build from config.
- `static DataComponentType getDataComponentType()` — the Paper type it maps to.

## Rich components

These are full classes with their own config schema (the YAML key is shown in
parentheses):

| Key | Class | Min version |
| --- | --- | --- |
| `food` | `FoodComponent` | 1.20.5 |
| `profile` | `ProfileComponent` | 1.20.5 |
| `potionContents` | `PotionContentsComponent` | 1.20.5 |
| `containerLoot` | `ContainerLootComponent` | 1.20.5 |
| `lodestoneTracker` | `LodestoneTrackerComponent` | 1.20.5 |
| `tool` | `ToolComponent` | 1.20.5 |
| `trim` | `ArmorTrimComponent` | 1.21.2 |
| `instrument` | `InstrumentComponent` | 1.21.2 |
| `damageResistant` | `DamageResistantComponent` | 1.21.2 |
| `useCooldown` | `UseCooldownComponent` | 1.21.2 |
| `tooltipDisplay` | `TooltipDisplayComponent` | 1.21.2 |
| `consumable` | `ConsumableComponent` | 1.21.2 |
| `equippable` | `EquippableComponent` | 1.21.2 |
| `useRemainder` | `UseRemainderComponent` | 1.21.2 |
| `deathProtection` | `DeathProtectionComponent` | 1.21.2 |
| `modelData` | `CustomModelDataComponent` | 1.21.4 |
| `weapon` | `WeaponComponent` | 1.21.5 |
| `attackRange` | `AttackRangeComponent` | 1.21.11 |
| `kineticWeapon` | `KineticWeaponComponent` | 1.21.11 |
| `piercingWeapon` | `PiercingWeaponComponent` | 1.21.11 |
| `useEffects` | `UseEffectsComponent` | 1.21.11 |

### Example: `food`

```java
// nutrition, saturation, canAlwaysEat
item.component(new FoodComponent(4, 2.4f, true));
```

```yaml
components:
  food:
    nutrition: 4
    saturation: 2.4
    canAlwaysEat: true
```

### Example: `profile` (player head)

```java
item.component(ProfileComponent.fromMinecraftComponent(resolvableProfile));
```

```yaml
components:
  profile:
    id: "00000000-0000-0000-0000-000000000000"
    name: "Notch"
    properties:
      - name: "textures"
        value: "<base64>"
        signature: "<signature>"
    skin:
      model: CLASSIC   # or SLIM
```

## Simple components

Primitive- and key-valued components are registered generically as
[`SimpleItemComponent`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/item/components/impl/SimpleItemComponent.java).
Their YAML key holds the value directly (no nested section):

| Key | Value type | Min version |
| --- | --- | --- |
| `damage` | int | 1.20.5 |
| `maxDamage` | int | 1.20.5 |
| `maxStackSize` | int | 1.20.5 |
| `enchantable` | int | 1.20.5 |
| `baseColor` | `DyeColor` name | 1.20.5 |
| `rarity` | `ItemRarity` name | 1.20.5 |
| `jukeboxPlayable` | namespaced key | 1.21.1 |
| `glider` | boolean | 1.21.2 |
| `itemModel` | namespaced key | 1.21.2 |
| `tooltipStyle` | namespaced key | 1.21.2 |
| `damageType` | namespaced key | 1.21.11 |
| `intangibleProjectile` | boolean | 1.21.11 |
| `minimumAttackCharge` | float | 1.21.11 |

```yaml
components:
  maxStackSize: 16
  rarity: EPIC
  glider: true
  itemModel: "minecraft:elytra"
```

## Writing your own component

Extend `ReadWriteItemComponent`, annotate it with `@ItemComponentSpec`, and
provide the three static methods the serializer expects:

```java
@ItemComponentSpec(key = "myComponent", requiredVersion = MCVersion.V1_20_5)
public class MyComponent extends ReadWriteItemComponent {
    public static DataComponentType getDataComponentType() { return DataComponentTypes.XXX; }
    public static MyComponent fromMinecraftComponent(SomeValue value) { ... }
    public static MyComponent readFromSection(ConfigurationSection cs) { ... }

    @Override public void applyToItem(ItemStack item) { ... }
    @Override public void write(ConfigurationSection cs) { ... }
}
```

> The annotation `key` and `requiredVersion` are read at runtime by the internal
> `ItemComponentSerializer`. Built-in components live in the
> `io.github.lijinhong11.mittellib.item.components.impl` package.
