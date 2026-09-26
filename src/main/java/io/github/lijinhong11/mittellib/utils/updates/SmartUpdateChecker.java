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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Selects an update service according to the server's public IP location.
 * Mainland China servers use NexusMC, while all other servers use Modrinth.
 */
public class SmartUpdateChecker {
    private static final URI LOCATION_URI = URI.create("https://www.cloudflare.com/cdn-cgi/trace");

    private final JavaPlugin plugin;
    private final String nexusResourceId;
    private final String modrinthProjectId;
    private final Function<Player, Component> adminJoinMessage;
    private final HttpClient httpClient;

    /**
     * Creates a smart update checker.
     *
     * @param plugin plugin whose current version should be checked
     * @param nexusResourceId NexusMC resource UUID
     * @param modrinthProjectId Modrinth project ID or slug
     */
    public SmartUpdateChecker(JavaPlugin plugin, String nexusResourceId, String modrinthProjectId) {
        this(plugin, nexusResourceId, modrinthProjectId, null);
    }

    /**
     * Creates a smart update checker with an administrator join notification.
     *
     * @param plugin plugin whose current version should be checked
     * @param nexusResourceId NexusMC resource UUID
     * @param modrinthProjectId Modrinth project ID or slug
     * @param adminJoinMessage message generated for OPs when an update is found
     */
    public SmartUpdateChecker(
            JavaPlugin plugin,
            String nexusResourceId,
            String modrinthProjectId,
            Function<Player, Component> adminJoinMessage) {
        this.plugin = plugin;
        this.nexusResourceId = nexusResourceId;
        this.modrinthProjectId = modrinthProjectId;
        this.adminJoinMessage = adminJoinMessage;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Detects the server location and starts an asynchronous update check.
     */
    public void check() {
        CompletableFuture.runAsync(() -> {
            boolean useNexusMC = isMainlandChinaServer();
            plugin.getLogger().info("Using " + (useNexusMC ? "NexusMC" : "Modrinth") + " for update checks.");
            if (useNexusMC) {
                new NexusMCUpdateChecker(plugin, nexusResourceId, adminJoinMessage).check();
            } else {
                new ModrinthUpdateChecker(plugin, modrinthProjectId, adminJoinMessage).check();
            }
        });
    }

    private boolean isMainlandChinaServer() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(LOCATION_URI)
                    .timeout(Duration.ofSeconds(8))
                    .header("Accept", "text/plain")
                    .header("User-Agent", plugin.getName())
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                plugin.getLogger()
                        .warning("Server IP location detection failed (HTTP " + response.statusCode()
                                + "); using Modrinth.");
                return false;
            }

            return "CN".equals(parseCountryCode(response.body()));
        } catch (Exception exception) {
            plugin.getLogger()
                    .warning("Server IP location detection failed: " + exception.getMessage() + "; using Modrinth.");
            return false;
        }
    }

    static String parseCountryCode(String responseBody) {
        for (String line : responseBody.lines().toList()) {
            if (line.startsWith("loc=")) {
                return line.substring(4).trim().toUpperCase(Locale.ROOT);
            }
        }
        return "";
    }
}
