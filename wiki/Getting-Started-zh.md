快速开始
==

[English](Getting-Started) | 简体中文

## 1. 环境要求

- **Java 21** 或更高版本。
- **Paper** 或 **Folia** 服务端（`plugin.yml` 中声明了 `folia-supported: true`）。
- 该库面向 Paper `1.21.x` API，但会通过运行时版本检测在旧版本上优雅降级。

## 2. 引入依赖

MittelLib 已发布到 **Maven Central**，坐标为
`io.github.lijinhong11:MittelLib`。

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

把 `VERSION` 替换为最新发布版本。由于 MittelLib 是一个独立运行的插件，请使用
`provided` / `compileOnly` 作用域，**不要**把它打包（shade）进你自己的 jar。

## 3. 在运行期依赖该插件

MittelLib 是一个需要与你的插件一起安装到服务器上的独立插件。请在 `plugin.yml`
中声明依赖，让它先加载：

```yaml
name: MyPlugin
main: com.example.MyPlugin
version: 1.0.0
api-version: '1.20'
depend:
  - MittelLib
```

## 4. 开始使用

大多数入口都可以从 `MittelLib` 单例或静态辅助类访问。最小示例：

```java
public final class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // 该库自身的语言管理器
        SyncLanguageManager lib = MittelLib.getInstance().getLanguageManager();

        // 绑定到“你的”插件的语言管理器（会回退到该库的管理器）
        SyncLanguageManager mine = MittelLib.getInstance().getLanguageManager(this);

        // 构建一个物品
        ItemStack sword = new MittelItem(Material.DIAMOND_SWORD)
                .enchant(Enchantment.SHARPNESS, 5)
                .get();
    }
}
```

### 接下来阅读

- [物品 (MittelItem)](Items-zh) —— 最常用的类。
- [内容提供者](Content-Providers-zh) —— 获取其他插件的物品。
- [GUI 界面](GUIs-zh) —— 构建容器界面。
- [本地化](Localization-zh) —— 发送翻译后的消息。
