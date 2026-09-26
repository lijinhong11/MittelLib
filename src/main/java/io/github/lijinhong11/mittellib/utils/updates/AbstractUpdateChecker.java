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
package io.github.lijinhong11.mittellib.utils.updates;

import com.google.gson.Gson;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

/** Common lifecycle and notification behavior for update checkers. */
public abstract sealed class AbstractUpdateChecker permits ModrinthUpdateChecker, NexusMCUpdateChecker {
    protected final JavaPlugin plugin;
    protected final HttpClient httpClient;
    protected final Gson gson = new Gson();
    protected final String gameVersion;
    protected final String loader;

    private final Function<Player, Component> adminJoinMessage;
    private volatile UpdateInfo availableUpdate;

    protected AbstractUpdateChecker(JavaPlugin plugin, Function<Player, Component> adminJoinMessage) {
        this.plugin = plugin;
        this.adminJoinMessage = adminJoinMessage;
        this.gameVersion = Bukkit.getMinecraftVersion();
        this.loader = detectLoader();
        this.httpClient =
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        if (adminJoinMessage != null) {
            plugin.getServer().getPluginManager().registerEvents(new AdminJoinListener(), plugin);
        }
    }

    /** Starts an asynchronous update check. */
    public final void check() {
        CompletableFuture.runAsync(() -> {
            try {
                LatestUpdate latest = findLatestUpdate();
                if (latest == null
                        || latest.version() == null
                        || latest.version().isBlank()) {
                    plugin.getLogger().info("No " + serviceName() + " updates found.");
                    return;
                }

                String currentVersion = plugin.getDescription().getVersion();
                if (isNewer(latest.version(), currentVersion)) {
                    availableUpdate = new UpdateInfo(currentVersion, latest.version(), latest.url());
                    plugin.getComponentLogger()
                            .info(
                                    adminJoinMessage == null
                                            ? Component.text(
                                                    plugin.getName() + " has a new version: " + latest.version())
                                            : formatMessage(null, availableUpdate));
                } else {
                    plugin.getComponentLogger()
                            .info(Component.text(plugin.getName() + " is up to date on " + serviceName() + "."));
                }
            } catch (Exception exception) {
                plugin.getLogger().warning(serviceName() + " update check error: " + exception.getMessage());
            }
        });
    }

    protected abstract LatestUpdate findLatestUpdate() throws Exception;

    protected abstract String serviceName();

    protected final String userAgent() {
        return plugin.getName();
    }

    private String detectLoader() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return "folia";
        } catch (ClassNotFoundException exception) {
            return "paper";
        }
    }

    private Component formatMessage(@Nullable Player player, UpdateInfo update) {
        return adminJoinMessage
                .apply(player)
                .replaceText(replacement -> replacement.matchLiteral("%plugin%").replacement(plugin.getName()))
                .replaceText(replacement ->
                        replacement.matchLiteral("%current_version%").replacement(update.currentVersion))
                .replaceText(replacement ->
                        replacement.matchLiteral("%latest_version%").replacement(update.latestVersion))
                .replaceText(
                        replacement -> replacement.matchLiteral("%update_url%").replacement(update.url));
    }

    private boolean isNewer(String latest, String current) {
        String[] latestParts = normalize(latest);
        String[] currentParts = normalize(current);
        int max = Math.max(latestParts.length, currentParts.length);

        for (int index = 0; index < max; index++) {
            int latestPart = index < latestParts.length ? parseInt(latestParts[index]) : 0;
            int currentPart = index < currentParts.length ? parseInt(currentParts[index]) : 0;
            if (latestPart != currentPart) {
                return latestPart > currentPart;
            }
        }
        return false;
    }

    private String[] normalize(String version) {
        return version.replaceFirst("^[vV]", "").replace("-SNAPSHOT", "").split("\\.");
    }

    private int parseInt(String value) {
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    protected record LatestUpdate(String version, String url) {}

    private record UpdateInfo(String currentVersion, String latestVersion, String url) {}

    private final class AdminJoinListener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            UpdateInfo update = availableUpdate;
            if (update != null && event.getPlayer().isOp()) {
                event.getPlayer().sendMessage(formatMessage(event.getPlayer(), update));
            }
        }
    }
}
