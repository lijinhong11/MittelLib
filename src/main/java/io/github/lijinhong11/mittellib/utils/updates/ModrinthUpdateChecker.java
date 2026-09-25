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
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A modrinth update checker
 */
public class ModrinthUpdateChecker {
    private final JavaPlugin plugin;
    private final String projectId;
    private final Component adminJoinMessage;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private volatile UpdateInfo availableUpdate;

    /**
     * Create a modrinth update checker
     * @param plugin the plugin
     * @param projectId the project id
     */
    public ModrinthUpdateChecker(JavaPlugin plugin, String projectId) {
        this(plugin, projectId, null);
    }

    /**
     * Create a modrinth update checker with an administrator join notification.
     *
     * @param plugin the plugin
     * @param projectId the project id
     * @param adminJoinMessage message sent to OPs when they join after an update is found
     */
    public ModrinthUpdateChecker(JavaPlugin plugin, String projectId, Component adminJoinMessage) {
        this.plugin = plugin;
        this.projectId = projectId;
        this.adminJoinMessage = adminJoinMessage;
        if (adminJoinMessage != null) {
            plugin.getServer().getPluginManager().registerEvents(new AdminJoinListener(), plugin);
        }
    }

    /**
     * Start the update checking task
     */
    public void check() {
        CompletableFuture.runAsync(() -> {
            try {
                String currentVersion = plugin.getDescription().getVersion();
                String url = buildUrl();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", plugin.getName())
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    plugin.getLogger().warning("Update check failed (HTTP " + response.statusCode() + ")");
                    return;
                }

                Type listType = new TypeToken<List<ModrinthVersion>>() {}.getType();
                List<ModrinthVersion> versions = gson.fromJson(response.body(), listType);

                if (versions == null || versions.isEmpty()) {
                    plugin.getLogger().info("No updates found.");
                    return;
                }

                versions.sort(Comparator.comparing(v -> v.datePublished));
                ModrinthVersion latest = versions.getLast();

                if (isNewer(latest.versionNumber, currentVersion)) {
                    availableUpdate = new UpdateInfo(
                            currentVersion,
                            latest.versionNumber,
                            "https://modrinth.com/plugin/" + projectId);
                    plugin.getComponentLogger().info(formatMessage(availableUpdate));
                } else {
                    plugin.getComponentLogger().info(Component.text(plugin.getName() + " is up to date."));
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Update check error: " + e.getMessage());
            }
        });
    }

    private final class AdminJoinListener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            UpdateInfo update = availableUpdate;
            if (update == null || !event.getPlayer().isOp()) {
                return;
            }

            event.getPlayer().sendMessage(formatMessage(update));
        }
    }

    private Component formatMessage(UpdateInfo update) {
        return adminJoinMessage
                .replaceText(replacement -> replacement.matchLiteral("%plugin%").replacement(plugin.getName()))
                .replaceText(replacement -> replacement
                        .matchLiteral("%current_version%")
                        .replacement(update.currentVersion))
                .replaceText(replacement -> replacement
                        .matchLiteral("%latest_version%")
                        .replacement(update.latestVersion))
                .replaceText(replacement -> replacement.matchLiteral("%update_url%").replacement(update.url));
    }

    private record UpdateInfo(String currentVersion, String latestVersion, String url) {}

    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private String detectLoader() {
        return isFolia() ? "folia" : "paper";
    }

    private void runSync(Runnable runnable) {
        if (isFolia()) {
            plugin.getServer().getGlobalRegionScheduler().execute(plugin, runnable);
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    private String buildUrl() {
        String loadersJson = "[\"" + detectLoader() + "\"]";
        String versionsJson = "[\"" + Bukkit.getMinecraftVersion() + "\"]";

        return "https://api.modrinth.com/v2/project/" + projectId + "/version"
                + "?include_changelog=false"
                + "&loaders=" + URLEncoder.encode(loadersJson, StandardCharsets.UTF_8)
                + "&game_versions=" + URLEncoder.encode(versionsJson, StandardCharsets.UTF_8);
    }

    private boolean isNewer(String latest, String current) {
        String[] l = normalize(latest);
        String[] c = normalize(current);

        int max = Math.max(l.length, c.length);

        for (int i = 0; i < max; i++) {
            int li = i < l.length ? parseInt(l[i]) : 0;
            int ci = i < c.length ? parseInt(c[i]) : 0;

            if (li > ci) return true;
            if (li < ci) return false;
        }
        return false;
    }

    private String[] normalize(String v) {
        return v.replace("v", "").replace("-SNAPSHOT", "").split("\\.");
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private static class ModrinthVersion {
        @SerializedName("version_number")
        String versionNumber;

        @SerializedName("date_published")
        OffsetDateTime datePublished;
    }
}
