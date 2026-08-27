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

import io.github.lijinhong11.mittellib.configuration.MittelConfig;
import io.github.lijinhong11.mittellib.gui.inventory.MittelGUIListener;
import io.github.lijinhong11.mittellib.hook.ContentProviders;
import io.github.lijinhong11.mittellib.hook.economy.EconomyProviders;
import io.github.lijinhong11.mittellib.hook.point.PlayerPointsHook;
import io.github.lijinhong11.mittellib.message.SyncLanguageManager;
import io.github.lijinhong11.mittellib.utils.components.MittelLibTranslator;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class MittelLib extends JavaPlugin {
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
        MittelConfig.load(this, "config.yml");

        System.setProperty("bstats.relocatecheck", "false");

        languageManager = new SyncLanguageManager(this);
        GlobalTranslator.translator().addSource(new MittelLibTranslator(this, languageManager));

        registerCommand("mittellib", new BasicCommand() {
            @Override
            public @NotNull String permission() {
                return "mittellib.reload";
            }

            @Override
            @ParametersAreNonnullByDefault
            public void execute(CommandSourceStack cst, String[] args) {
                CommandSender sender = cst.getSender();
                if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage("Usage: /mittellib reload");
                    return;
                }

                languageManager.reload();
                sender.sendMessage("MittelLib language files reloaded.");
            }
        });

        ContentProviders.init();
        EconomyProviders.init();
        PlayerPointsHook.init();

        Bukkit.getPluginManager().registerEvents(new MittelGUIListener(), this);

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
}
