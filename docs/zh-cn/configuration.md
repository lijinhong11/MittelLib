配置文件
==

MittelLib 的配置辅助类减少了围绕 Bukkit `YamlConfiguration` 的样板代码：一个更强大的
配置封装，以及一个小巧的序列化约定。

## MittelConfig

[`MittelConfig`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/configuration/MittelConfig.java)
封装了 `YamlConfiguration`，并增加了类型化 getter、无符号 getter、枚举 getter、
组件 getter 与物品 getter。

### 加载

```java
// 从任意文件加载（可选在缺失时创建）
MittelConfig cfg = MittelConfig.load(new File(getDataFolder(), "data.yml"), true);

// 从内置资源加载 —— 复制并补全键到数据目录
MittelConfig cfg2 = MittelConfig.load(this, "config.yml");

// 语言资源（对照内置文件补全，但不创建）
MittelConfig lang = MittelConfig.loadLang(this, "language/en-US.yml");
```

`load(Plugin, resourcePath)` 会执行**文件补全**：首次运行时复制资源，之后的运行中，
把内置文件里存在、但磁盘文件中缺失的键（及注释）补上。这样可在插件更新时让用户配置
保持最新。

### 读取值

```java
String name   = cfg.getString("name", "default");
String must   = cfg.requireString("name");          // 缺失时抛异常
int amount    = cfg.getInt("amount", 1);
double d      = cfg.getDouble("ratio");
boolean flag  = cfg.getBoolean("enabled", true);
List<String> lines = cfg.getStringList("lore");

// 无符号变体会把负数取绝对值
int u = cfg.getUnsignedInt("amount");

// Adventure 组件（用 ComponentUtils 解析）
Component title = cfg.getComponent("title");

// 枚举（大小写不敏感，见 EnumUtils）
Material mat = cfg.getEnum("material", Material.class, Material.STONE);

// 通过 MittelItem 得到 ItemStack
ItemStack item = cfg.getItemStack("reward");
```

### 节与键

```java
boolean isSection = cfg.isSection("rewards");
ConfigurationSection sec = cfg.getSectionOrCreate("rewards");
Set<String> keys = cfg.getKeys(false);
Set<String> sub  = cfg.getKeys("rewards", false);
```

### 写入与持久化

```java
cfg.remove("old-key");
cfg.clear();           // 移除所有顶层键
cfg.save();            // 未关联文件时抛异常
cfg.reload();          // 从磁盘重新读取
```

## ReadWriteObject

[`ReadWriteObject`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/configuration/ReadWriteObject.java)
是“能把自身持久化到 `ConfigurationSection`”的约定：

```java
public interface ReadWriteObject {
    void write(ConfigurationSection cs);
    void read(ConfigurationSection cs);
}
```

`MittelItem`、`MittelItemMeta`、`BlockPos` 以及所有物品组件都实现了它。

### 读取自定义对象

`MittelConfig` 可以构造任何带有 `(ConfigurationSection)` 构造函数的 `ReadWriteObject`：

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

`ReadWriteObject.read(Class, ConfigurationSection)` 是内部使用的静态辅助方法；当类
没有合适的构造函数时，它会记录日志并返回 `null`。

> **物品组件** 较为特殊：它们继承 `ReadWriteItemComponent`，其 `read(...)` 会故意抛
> 异常。请改用每个组件的静态 `readFromSection(...)` 来构建。见
> [物品组件](item-components.md)。
