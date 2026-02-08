package me.mmmjjkx.mittellib;

import lombok.Getter;
import me.mmmjjkx.mittellib.hook.ContentProviders;
import me.mmmjjkx.mittellib.utils.MCVersion;
import org.bukkit.plugin.java.JavaPlugin;

public final class MittelLib extends JavaPlugin {
    @Getter
    private static MittelLib instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        ContentProviders.init();

        getLogger().info("MittelLib is enabled!");
        getLogger().info("Detected MC version: " + MCVersion.getCurrent());
    }

    @Override
    public void onDisable() {

    }
}
