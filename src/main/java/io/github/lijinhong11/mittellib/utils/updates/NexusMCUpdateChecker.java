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
import com.google.gson.reflect.TypeToken;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Checks a plugin version against its public NexusMC resource.
 *
 * <p>The NexusMC update endpoint is public, so this checker does not require
 * or send a personal API token.
 */
public class NexusMCUpdateChecker {
    private static final String API_URL = "https://www.nexusmc.cn/api/resources/";
    private static final String RESOURCE_URL = "https://www.nexusmc.cn/resources/";
    private final JavaPlugin plugin;
    private final String resourceId;
    private final Component adminJoinMessage;
    private final String platform;
    private final String gameVersion;
    private final String loader;
    private final String versionTag;
    private final String status;
    private final Map<String, String> queryParameters;
    private final HttpClient httpClient;
    private final Gson gson = new Gson();
    private volatile UpdateInfo availableUpdate;

    /**
     * Creates a NexusMC update checker.
     *
     * @param plugin plugin whose current version should be checked
     * @param resourceId NexusMC resource UUID
     */
    public NexusMCUpdateChecker(JavaPlugin plugin, String resourceId) {
        this(plugin, resourceId, null);
    }

    /**
     * Creates a NexusMC update checker with an administrator join notification.
     *
     * @param plugin plugin whose current version should be checked
     * @param resourceId NexusMC resource UUID
     * @param adminJoinMessage message sent to OPs when an update is found
     */
    public NexusMCUpdateChecker(JavaPlugin plugin, String resourceId, Component adminJoinMessage) {
        this(plugin, resourceId, adminJoinMessage, null, null);
    }

    /**
     * Creates a NexusMC update checker with optional resource metadata filters.
     *
     * @param plugin plugin whose current version should be checked
     * @param resourceId NexusMC resource UUID
     * @param adminJoinMessage message sent to OPs when an update is found
     * @param platform resource platform, such as {@code java} or {@code bedrock}
     * @param gameVersion Minecraft version the resource must support
     */
    public NexusMCUpdateChecker(
            JavaPlugin plugin,
            String resourceId,
            Component adminJoinMessage,
            String platform,
            String gameVersion) {
        this(plugin, resourceId, adminJoinMessage, platform, gameVersion, null, null, "approved", Map.of());
    }

    /**
     * Creates a NexusMC update checker with version query filters.
     *
     * @param plugin plugin whose current version should be checked
     * @param resourceId NexusMC resource UUID
     * @param adminJoinMessage message sent to OPs when an update is found
     * @param platform resource platform, such as {@code java}
     * @param gameVersion Minecraft version, such as {@code 1.21.1}
     * @param loader resource loader, such as {@code fabric}
     * @param versionTag version tag, such as {@code releases}
     * @param status version status, such as {@code approved}
     */
    public NexusMCUpdateChecker(
            JavaPlugin plugin,
            String resourceId,
            Component adminJoinMessage,
            String platform,
            String gameVersion,
            String loader,
            String versionTag,
            String status) {
        this(plugin, resourceId, adminJoinMessage, platform, gameVersion, loader, versionTag, status, Map.of());
    }

    /**
     * Creates a NexusMC update checker with additional endpoint query parameters.
     *
     * @param plugin plugin whose current version should be checked
     * @param resourceId NexusMC resource UUID
     * @param adminJoinMessage message sent to OPs when an update is found
     * @param platform resource platform
     * @param gameVersion Minecraft version
     * @param queryParameters additional endpoint query parameters
     */
    public NexusMCUpdateChecker(
            JavaPlugin plugin,
            String resourceId,
            Component adminJoinMessage,
            String platform,
            String gameVersion,
            Map<String, String> queryParameters) {
        this(plugin, resourceId, adminJoinMessage, platform, gameVersion, null, null, "approved", queryParameters);
    }

    /**
     * Creates a checker with additional query parameters for the NexusMC updates endpoint.
     *
     * @param plugin plugin whose current version should be checked
     * @param resourceId NexusMC resource UUID
     * @param adminJoinMessage message sent to OPs when an update is found
     * @param platform optional resource platform filter
     * @param gameVersion optional supported Minecraft version filter
     * @param loader optional loader filter, such as {@code fabric}
     * @param versionTag optional version tag filter, such as {@code releases}
     * @param status optional version status filter, such as {@code approved}
     * @param queryParameters extra version endpoint query parameters, including undocumented filters
     */
    public NexusMCUpdateChecker(
            JavaPlugin plugin,
            String resourceId,
            Component adminJoinMessage,
            String platform,
            String gameVersion,
            String loader,
            String versionTag,
            String status,
            Map<String, String> queryParameters) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.adminJoinMessage = adminJoinMessage;
        this.platform = platform;
        this.gameVersion = gameVersion;
        this.loader = loader;
        this.versionTag = versionTag;
        this.status = status;
        this.queryParameters = queryParameters == null ? Map.of() : Map.copyOf(queryParameters);
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        if (adminJoinMessage != null) {
            plugin.getServer().getPluginManager().registerEvents(new AdminJoinListener(), plugin);
        }
    }

    /**
     * Starts an asynchronous update check.
     */
    public void check() {
        CompletableFuture.runAsync(() -> {
            try {
                ResourceUpdate latest = findResourceUpdate();
                if (latest == null || latest.version == null || latest.version.isBlank()) {
                    plugin.getLogger().info("NexusMC resource update was not found.");
                    return;
                }

                String currentVersion = plugin.getDescription().getVersion();
                if (isNewer(latest.version, currentVersion)) {
                    availableUpdate = new UpdateInfo(
                            currentVersion, latest.version, RESOURCE_URL + resourceId);
                    plugin.getComponentLogger().info(adminJoinMessage == null
                            ? Component.text(plugin.getName() + " has a new version: " + latest.version)
                            : formatMessage(availableUpdate));
                } else {
                    plugin.getComponentLogger()
                            .info(Component.text(plugin.getName() + " is up to date on NexusMC."));
                }
            } catch (Exception exception) {
                plugin.getLogger().warning("NexusMC update check error: " + exception.getMessage());
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

    private ResourceUpdate findResourceUpdate() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildVersionsUrl()))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .header("User-Agent", plugin.getName())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("NexusMC version request failed (HTTP " + response.statusCode() + ")");
        }

        Type listType = new TypeToken<List<ResourceVersion>>() {}.getType();
        List<ResourceVersion> versions = gson.fromJson(response.body(), listType);
        if (versions == null || versions.isEmpty()) {
            return null;
        }

        for (ResourceVersion version : versions) {
            FileInfo file = findMatchingFile(version);
            if (file != null) {
                return new ResourceUpdate(version.version);
            }
        }
        return null;
    }

    private FileInfo findMatchingFile(ResourceVersion version) {
        if (version.files == null) {
            return null;
        }

        List<FileInfo> matchingFiles = version.files.stream()
                .filter(this::matchesFile)
                .toList();
        return matchingFiles.stream()
                .filter(file -> file.isPrimary)
                .findFirst()
                .orElseGet(() -> matchingFiles.stream().findFirst().orElse(null));
    }

    private boolean matchesFile(FileInfo file) {
        boolean matchesVersion = gameVersion == null
                || gameVersion.isBlank()
                || file.gameVersions != null && file.gameVersions.contains(gameVersion);
        boolean matchesLoader = loader == null
                || loader.isBlank()
                || file.subcategoryIds != null
                        && file.subcategoryIds.stream().anyMatch(loader::equalsIgnoreCase);
        return matchesVersion && matchesLoader;
    }

    private String buildVersionsUrl() {
        StringBuilder url = new StringBuilder(API_URL)
                .append(URLEncoder.encode(resourceId, StandardCharsets.UTF_8))
                .append("/versions?");
        appendQueryParameter(url, "platform", platform);
        appendQueryParameter(url, "mcVersion", gameVersion);
        appendQueryParameter(url, "loader", loader);
        appendQueryParameter(url, "versionTag", versionTag);
        appendQueryParameter(url, "status", status);
        queryParameters.forEach((key, value) -> {
            if (key != null && !key.isBlank() && value != null) {
                appendQueryParameter(url, key, value);
            }
        });
        return url.toString();
    }

    private void appendQueryParameter(StringBuilder url, String key, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (url.charAt(url.length() - 1) != '?') {
            url.append('&');
        }
        url.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                .append('=')
                .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
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

    private static class ResourceUpdate {
        String version;

        ResourceUpdate(String version) {
            this.version = version;
        }
    }

    private static class ResourceVersion {
        String version;
        List<FileInfo> files;
    }

    private static class FileInfo {
        String url;
        boolean isPrimary;
        List<String> subcategoryIds;
        List<String> gameVersions;
    }
}
