package io.github.lijinhong11.mittellib;

import io.github.lijinhong11.mittellib.hook.ContentProviders;
import io.github.lijinhong11.mittellib.message.LanguageManager;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class MittelLib extends JavaPlugin {
    @Getter
    private static MittelLib instance;

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
}
