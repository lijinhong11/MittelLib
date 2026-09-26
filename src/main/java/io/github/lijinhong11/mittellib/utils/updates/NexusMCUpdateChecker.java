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

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Checks a plugin version against its public NexusMC resource.
 *
 * <p>The NexusMC update endpoint is public, so this checker does not require
 * or send a personal API token.
 */
public final class NexusMCUpdateChecker extends AbstractUpdateChecker {
    private static final String API_URL = "https://www.nexusmc.cn/api/resources/";
    private static final String RESOURCE_URL = "https://www.nexusmc.cn/resources/";
    private static final String DEFAULT_VERSION_TAG = "releases";
    private static final String PLATFORM = "java";
    private final String resourceId;
    private final String versionTag;

    /**
     * Creates a NexusMC update checker.
     *
     * @param plugin plugin whose current version should be checked
     * @param resourceId NexusMC resource UUID
     */
    public NexusMCUpdateChecker(JavaPlugin plugin, String resourceId) {
        this(plugin, resourceId, null, DEFAULT_VERSION_TAG);
    }

    /**
     * Creates a NexusMC update checker with an administrator join notification.
     *
     * @param plugin plugin whose current version should be checked
     * @param resourceId NexusMC resource UUID
     * @param adminJoinMessage message sent to OPs when an update is found
     */
    public NexusMCUpdateChecker(JavaPlugin plugin, String resourceId, Function<Player, Component> adminJoinMessage) {
        this(plugin, resourceId, adminJoinMessage, DEFAULT_VERSION_TAG);
    }

    /**
     * Creates a NexusMC update checker with a version tag filter.
     *
     * @param plugin plugin whose current version should be checked
     * @param resourceId NexusMC resource UUID
     * @param adminJoinMessage message sent to OPs when an update is found
     * @param versionTag version tag, such as {@code releases}
     */
    public NexusMCUpdateChecker(
            JavaPlugin plugin, String resourceId, Function<Player, Component> adminJoinMessage, String versionTag) {
        super(plugin, adminJoinMessage);
        this.resourceId = resourceId;
        this.versionTag = versionTag == null || versionTag.isBlank() ? DEFAULT_VERSION_TAG : versionTag;
    }

    @Override
    protected LatestUpdate findLatestUpdate() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildVersionsUrl()))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .header("User-Agent", userAgent())
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
                return new LatestUpdate(version.version, RESOURCE_URL + resourceId);
            }
        }
        return null;
    }

    @Override
    protected String serviceName() {
        return "NexusMC";
    }

    private FileInfo findMatchingFile(ResourceVersion version) {
        if (version.files == null) {
            return null;
        }

        List<FileInfo> matchingFiles =
                version.files.stream().filter(this::matchesFile).toList();
        return matchingFiles.stream().filter(file -> file.isPrimary).findFirst().orElseGet(() -> matchingFiles.stream()
                .findFirst()
                .orElse(null));
    }

    private boolean matchesFile(FileInfo file) {
        boolean matchesVersion = file.gameVersions != null && file.gameVersions.contains(gameVersion);
        boolean matchesLoader =
                file.subcategoryIds != null && file.subcategoryIds.stream().anyMatch(loader::equalsIgnoreCase);
        return matchesVersion && matchesLoader;
    }

    private String buildVersionsUrl() {
        StringBuilder url = new StringBuilder(API_URL)
                .append(URLEncoder.encode(resourceId, StandardCharsets.UTF_8))
                .append("/versions?");
        appendQueryParameter(url, "platform", PLATFORM);
        appendQueryParameter(url, "mcVersion", gameVersion);
        appendQueryParameter(url, "loader", loader);
        appendQueryParameter(url, "versionTag", versionTag);
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
