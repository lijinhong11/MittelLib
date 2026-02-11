package me.mmmjjkx.mittellib.hook;

import lombok.experimental.UtilityClass;
import me.mmmjjkx.mittellib.iface.ContentProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
@UtilityClass
public class ContentProviders {
    private static final Map<String, ContentProvider> contentProviders = new HashMap<>();

    public static void init() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            String name = plugin.getName() + "ContentProvider";
            try {
                Class<?> theClass = Class.forName("me.mmmjjkx.mittellib.hook." + name);
                Constructor<? extends ContentProvider> constructor = ((Class<? extends ContentProvider>) theClass).getConstructor();
                contentProviders.put(name.toLowerCase(), constructor.newInstance());
            } catch (Exception ignore) {
            }
        }
    }

    public static ContentProvider getByName(String name) {
        for (ContentProvider provider : contentProviders.values()) {
            if (provider.toString().equalsIgnoreCase(name)) {
                return provider;
            }
        }

        return null;
    }
}
