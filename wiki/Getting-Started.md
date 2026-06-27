Getting Started
==

English | [简体中文](Getting-Started-zh)

## 1. Requirements

- **Java 21** or newer.
- A **Paper** or **Folia** server (the `plugin.yml` declares `folia-supported: true`).
- The library targets the Paper `1.21.x` API but degrades gracefully on older
  releases via runtime version detection.

## 2. Add the dependency

MittelLib is published on **Maven Central** under the coordinates
`io.github.lijinhong11:MittelLib`.

**Maven**

```xml
<dependency>
    <groupId>io.github.lijinhong11</groupId>
    <artifactId>MittelLib</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

**Gradle (Groovy)**

```groovy
compileOnly 'io.github.lijinhong11:MittelLib:VERSION'
```

**Gradle (Kotlin)**

```kotlin
compileOnly("io.github.lijinhong11:MittelLib:VERSION")
```

Replace `VERSION` with the latest released version. Because MittelLib is a runtime
plugin, use the `provided` / `compileOnly` scope and **do not** shade it into your
own jar.

## 3. Depend on the plugin at runtime

MittelLib is a standalone plugin that must be installed on the server next to your
plugin. Declare it in your `plugin.yml` so it loads first:

```yaml
name: MyPlugin
main: com.example.MyPlugin
version: 1.0.0
api-version: '1.20'
depend:
  - MittelLib
```

## 4. Use it

Most entry points are reachable from the `MittelLib` singleton or from static
helper classes. A minimal example:

```java
public final class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // The library's own language manager
        SyncLanguageManager lib = MittelLib.getInstance().getLanguageManager();

        // A language manager scoped to *your* plugin (falls back to the library's)
        SyncLanguageManager mine = MittelLib.getInstance().getLanguageManager(this);

        // Build an item
        ItemStack sword = new MittelItem(Material.DIAMOND_SWORD)
                .enchant(Enchantment.SHARPNESS, 5)
                .get();
    }
}
```

### What to read next

- [Items (MittelItem)](Items) — the most commonly used class.
- [Content Providers](Content-Providers) — pull items from other plugins.
- [GUIs](GUIs) — build inventories.
- [Localization](Localization) — send translated messages.
