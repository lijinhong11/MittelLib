package io.github.lijinhong11.mittellib;

import io.github.lijinhong11.mittellib.hook.ContentProviders;
import io.github.lijinhong11.mittellib.message.LanguageManager;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class MittelLib extends JavaPlugin {
    private final Map<Plugin, LanguageManager> pluginLanguages = new HashMap<>();

    @Getter
    private static MittelLib instance;

    /**
     * Get MittelLib's language manager <br>
     * For other plugin, use {@link #getLanguageManager(Plugin)}
     */
    @Getter
    private LanguageManager languageManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        languageManager = new LanguageManager(this);

        ContentProviders.init();

        getLogger().info("MittelLib is enabled!");
        getLogger().info("Detected MC version: " + MCVersion.getCurrent());
    }

    @Override
    public void onDisable() {
        getLogger().info("MittelLib is disabled!");
    }

    /**
     * Get the language manager for the plugin
     * @param plugin the plugin
     * @return the language manager for the plugin
     */
    public LanguageManager getLanguageManager(Plugin plugin) {
        if (plugin == this) {
            return languageManager;
        }

        return pluginLanguages.computeIfAbsent(plugin, pl -> {
            LanguageManager manager = new LanguageManager(pl);
            manager.setFallback(languageManager); // 插件 fallback 到 MittelLib
            return manager;
        });
    }
}
