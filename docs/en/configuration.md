Configuration
==

MittelLib's configuration helpers reduce the boilerplate around Bukkit's
`YamlConfiguration`: a richer config wrapper and a small serialization contract.

## MittelConfig

[`MittelConfig`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/configuration/MittelConfig.java)
wraps a `YamlConfiguration` and adds typed getters, unsigned getters, enum
getters, component getters and item getters.

### Loading

```java
// From an arbitrary file (optionally create it if missing)
MittelConfig cfg = MittelConfig.load(new File(getDataFolder(), "data.yml"), true);

// From a bundled resource — copies & key-completes it into the data folder
MittelConfig cfg2 = MittelConfig.load(this, "config.yml");

// A language resource (completes against the bundled file, does not create)
MittelConfig lang = MittelConfig.loadLang(this, "language/en-US.yml");
```

`load(Plugin, resourcePath)` runs **file completion**: it copies the resource on
first run and, on later runs, adds any keys (and comments) that exist in the
bundled file but are missing from the on-disk file. This keeps user configs
up-to-date across plugin updates.

### Reading values

```java
String name   = cfg.getString("name", "default");
String must   = cfg.requireString("name");          // throws if missing
int amount    = cfg.getInt("amount", 1);
double d      = cfg.getDouble("ratio");
boolean flag  = cfg.getBoolean("enabled", true);
List<String> lines = cfg.getStringList("lore");

// Unsigned variants coerce negatives to their absolute value
int u = cfg.getUnsignedInt("amount");

// Adventure component (parsed with ComponentUtils)
Component title = cfg.getComponent("title");

// Enums (case-insensitive, see EnumUtils)
Material mat = cfg.getEnum("material", Material.class, Material.STONE);

// An ItemStack via MittelItem
ItemStack item = cfg.getItemStack("reward");
```

### Sections & keys

```java
boolean isSection = cfg.isSection("rewards");
ConfigurationSection sec = cfg.getSectionOrCreate("rewards");
Set<String> keys = cfg.getKeys(false);
Set<String> sub  = cfg.getKeys("rewards", false);
```

### Writing & persistence

```java
cfg.remove("old-key");
cfg.clear();           // remove all top-level keys
cfg.save();            // throws if no file is associated
cfg.reload();          // re-read from disk
```

## ReadWriteObject

[`ReadWriteObject`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/configuration/ReadWriteObject.java)
is the contract for anything that can persist itself to a `ConfigurationSection`:

```java
public interface ReadWriteObject {
    void write(ConfigurationSection cs);
    void read(ConfigurationSection cs);
}
```

`MittelItem`, `MittelItemMeta`, `BlockPos` and all item components implement it.

### Reading a custom object

`MittelConfig` can construct any `ReadWriteObject` that has a
`(ConfigurationSection)` constructor:

```java
public class Home implements ReadWriteObject {
    private BlockPos pos;
    public Home() {}
    public Home(ConfigurationSection cs) { read(cs); }

    @Override public void write(ConfigurationSection cs) { pos.write(cs.createSection("pos")); }
    @Override public void read(ConfigurationSection cs)  { pos = new BlockPos(cs.getConfigurationSection("pos")); }
}

Home home = cfg.getRWObject("home", Home.class);
```

`ReadWriteObject.read(Class, ConfigurationSection)` is the static helper used
internally; it logs and returns `null` if the class has no suitable constructor.

> **Item components** are special: they extend `ReadWriteItemComponent`, whose
> `read(...)` intentionally throws. Build them with each component's static
> `readFromSection(...)` instead. See [Item Components](item-components.md).
