MittelLib
=
A library contains useful things.  
This is not fully finished, you help us develop more things by make pull requests!  
**NOTE: JDK21 is required**

## Features
1. Item Serialization/Deserialization
2. Item Component Serialization/Deserialization (Support almost all minecraft components)
3. Universal placeholder expansion for MiniPlaceholders and PlaceholderAPI
4. Items & Blocks from other plugins (ItemsAdder, Nexo etc.)
5. More coming soon...

## Develop Examples
Set item component:
```java
ItemStack itemStack = ...;
MittelItem item = MittelItem.fromItemStack(itemStack);

ResolvableProfile component = ...;
ProfileComponent profileComponent = ProfileComponent.fromMinecraftComponent(component);

item.component(profileComponent);

ItemStack edited = item.get();
```
Write item into configuration:
```java
MittelItem mittelItem = ...;
ConfigurationSection itemSection = ...;

mittelItem.write(itemSection);
```
Read item from configuration:
```java
ConfigurationSection itemSection = ...;

MittelItem item = MittelItem.readFromSection(itemSection);
```
Get a custom item:
```java
ItemStack item1 = ContentProviders.getItemStack("nexo:thisexample");
ItemStack item2 = ContentProviders.getItemStack("itemsadder:example:ex");
```

## Import
The artifact is on Maven Central!  
Maven:
```xml
<dependency>
    <groupId>io.github.lijinhong11</groupId>
    <artifactId>MittelLib</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

Gradle (Groovy):
```groovy
compileOnly 'io.github.lijinhong11:MittelLib:VERSION'
```

Gradle (Kotlin):
```kotlin
compileOnly("io.github.lijinhong11:MittelLib:VERSION")
```