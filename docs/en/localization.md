Localization
==

MittelLib includes a localization framework built around the
[`ILanguageManager`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/message/ILanguageManager.java)
interface. It loads per-language YAML files, picks the right one for each player,
parses MiniMessage / legacy color codes and placeholders, and sends Adventure
`Component`s.

## Getting a language manager

```java
// The library's own manager
SyncLanguageManager lib = MittelLib.getInstance().getLanguageManager();

// A manager scoped to YOUR plugin. It falls back to the library's manager
// for any key your files don't define.
SyncLanguageManager mine = MittelLib.getInstance().getLanguageManager(myPlugin);
```

### `SyncLanguageManager` vs `LocalLanguageManager`

| | `SyncLanguageManager` | `LocalLanguageManager` |
| --- | --- | --- |
| Source | Copies `language/*.yml` out of your jar into the plugin folder | Reads only the files already in the plugin folder |
| Auto-complete | Optionally fills missing keys from the bundled defaults | No |
| Typical use | Most plugins | Packs where languages are provided by the server owner |

Both read language files from `<plugin data folder>/language/*.yml` and default to
`en-US`.

## Language files

Put one YAML file per locale under `src/main/resources/language/`, e.g.
`en-US.yml`, `zh-CN.yml`. Keys are dotted paths:

```yaml
common:
  no-permission: "&cYou don't have permission to do that!"
  enabled: "&aEnabled"
  location-format: "&fX: &b%x%, &fY: &b%y%, &fZ: &b%z%"
```

The bundled languages include `en-US`, `zh-CN`, `zh-TW`, `zh-HK`, `fr-FR`,
`ru-RU`, `es-ES` and `pt-BR`.

## Sending messages

```java
// Send a single line
mine.sendMessage(sender, "common.no-permission");

// Send a list (each list entry becomes one message)
mine.sendMessages(sender, "help.lines");

// With placeholders: MessageReplacement.replace(token, value)
mine.sendMessage(sender, "shop.bought",
        MessageReplacement.replace("%item%", "Diamond"),
        MessageReplacement.replace("%price%", "10"));

// With a click event
mine.sendMessage(sender, "menu.open",
        ClickEvent.runCommand("/menu"));
```

## Getting values instead of sending

```java
String  raw      = mine.getMsg(sender, "common.enabled");
Component comp   = mine.getMsgComponent(sender, "common.enabled");
List<String> raw2 = mine.getMsgList(sender, "help.lines");
List<Component> c = mine.getMsgComponentList(sender, "help.lines");

// Force a specific language regardless of the viewer
String zh = mine.getMsgByLanguage("zh-CN", "common.enabled");
```

## Localized items

Build an item whose name and lore come from a config section
(`<sectionKey>.name` and `<sectionKey>.lore`):

```java
ItemStack item = mine.getMessagedItem(Material.PAPER, "gui.info", player,
        MessageReplacement.replace("%count%", "3"));
```

## Locale selection

By default the manager uses the **player's client locale** (`detectPlayerLocale`).
When detection is off, or for non-player senders, it falls back to the `language`
key in the plugin config, then to the default language.

`LocalLanguageManager` exposes an `Options` object to tune this:

```java
ILanguageManager.Options options = new ILanguageManager.Options();
options.setDetectPlayerLocale(true);
options.setDefaultLanguage("en-US");
options.setLanguageSetterKey("language"); // config key holding the forced language
```

## Other helpers

```java
mine.reload();                        // reload all language files from disk
mine.getTranslationKeys();            // all keys defined in the default language
mine.getParsedLocation(sender, loc);  // "X: .., Y: .., Z: .." via common.location-format
```
