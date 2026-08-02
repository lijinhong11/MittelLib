/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.mittellib;

import io.github.lijinhong11.mittellib.gui.inventory.MittelGUIListener;
import io.github.lijinhong11.mittellib.gui.inventory.choosers.MaterialChooser;
import io.github.lijinhong11.mittellib.hook.ContentProviders;
import io.github.lijinhong11.mittellib.hook.economy.VaultHook;
import io.github.lijinhong11.mittellib.hook.point.PlayerPointsHook;
import io.github.lijinhong11.mittellib.message.SyncLanguageManager;
import io.github.lijinhong11.mittellib.utils.ModrinthUpdateChecker;
import io.github.lijinhong11.mittellib.utils.components.MittelLibTranslator;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class MittelLib extends JavaPlugin implements Listener {
    private final Map<Plugin, SyncLanguageManager> pluginLanguages = new HashMap<>();

    @Getter
    private static MittelLib instance;

    /**
     * Get MittelLib's language manager <br>
     * For other plugin, use {@link #getLanguageManager(Plugin)}
     */
    @Getter
    private SyncLanguageManager languageManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Shut up bstats relocation
        System.setProperty("bstats.relocatecheck", "false");

        new ModrinthUpdateChecker(this, "mittellib").check();

        languageManager = new SyncLanguageManager(this);
        GlobalTranslator.translator().addSource(new MittelLibTranslator(this, languageManager));

        ContentProviders.init();
        VaultHook.init();
        PlayerPointsHook.init();

        Bukkit.getPluginManager().registerEvents(new MittelGUIListener(), this);
        getServer().getPluginManager().registerEvents(this, this);

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
    public SyncLanguageManager getLanguageManager(Plugin plugin) {
        if (plugin == this) {
            return languageManager;
        }

        return pluginLanguages.computeIfAbsent(plugin, pl -> {
            SyncLanguageManager manager = new SyncLanguageManager(pl);
            manager.setFallback(languageManager);

            GlobalTranslator.translator().addSource(new MittelLibTranslator(pl, manager));

            return manager;
        });
    }

    @EventHandler
    public void a(PlayerJoinEvent e) {
        MaterialChooser.openUsableBlockChooser(e.getPlayer(), b -> {});
    }
}
