package io.github.lijinhong11.mittellib;

import io.github.lijinhong11.mittellib.gui.MittelGUI;
import io.github.lijinhong11.mittellib.gui.MittelGUIListener;
import io.github.lijinhong11.mittellib.gui.item.ButtonItem;
import io.github.lijinhong11.mittellib.hook.ContentProviders;
import io.github.lijinhong11.mittellib.message.SyncLanguageManager;
import io.github.lijinhong11.mittellib.utils.ModrinthUpdateChecker;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        new ModrinthUpdateChecker(this, "mittellib").check();

        languageManager = new SyncLanguageManager(this);

        ContentProviders.init();

        Bukkit.getPluginManager().registerEvents(new MittelGUIListener(), this);

        getLogger().info("MittelLib is enabled!");
        getLogger().info("Detected MC version: " + MCVersion.getCurrent());

        Bukkit.getCommandMap().register("mittellib", new Command("mittellib") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
                if (sender instanceof Player p) {
                    MittelGUI.chestBuilder()
                            .structure("XXXXXXXXX",
                                    "LOLLOL FF")
                            .bind('X', ButtonItem.unclickable(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
                            .bind('L', ButtonItem.clickable(new ItemStack(Material.APPLE), (g, i) -> {
                                Player player = (Player) i.getWhoClicked();
                                player.sendMessage("This is an apple");
                                return false;
                            }))
                            .onOpen((pl, g) -> {
                                pl.sendMessage("it opened");
                            })
                            .build()
                            .open(p);

                }

                return true;
            }
        });
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
            return manager;
        });
    }
}
