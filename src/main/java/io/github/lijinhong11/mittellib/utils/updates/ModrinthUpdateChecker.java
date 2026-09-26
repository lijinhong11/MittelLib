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

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A modrinth update checker
 */
public final class ModrinthUpdateChecker extends AbstractUpdateChecker {
    private final String projectId;

    /**
     * Create a modrinth update checker
     * @param plugin the plugin
     * @param projectId the project id
     */
    public ModrinthUpdateChecker(JavaPlugin plugin, String projectId) {
        this(plugin, projectId, null);
    }

    /**
     * Create a modrinth update checker with a player-specific administrator join notification.
     *
     * @param plugin the plugin
     * @param projectId the project id
     * @param adminJoinMessage message generated for OPs when they join after an update is found
     */
    public ModrinthUpdateChecker(JavaPlugin plugin, String projectId, Function<Player, Component> adminJoinMessage) {
        super(plugin, adminJoinMessage);
        this.projectId = projectId;
    }

    @Override
    protected LatestUpdate findLatestUpdate() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildUrl()))
                .header("User-Agent", userAgent())
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Modrinth version request failed (HTTP " + response.statusCode() + ")");
        }

        Type listType = new TypeToken<List<ModrinthVersion>>() {}.getType();
        List<ModrinthVersion> versions = gson.fromJson(response.body(), listType);
        if (versions == null || versions.isEmpty()) {
            return null;
        }

        ModrinthVersion latest = versions.stream()
                .max(Comparator.comparing(version -> version.datePublished))
                .orElseThrow();
        return new LatestUpdate(latest.versionNumber, "https://modrinth.com/plugin/" + projectId);
    }

    @Override
    protected String serviceName() {
        return "Modrinth";
    }

    private String buildUrl() {
        String loadersJson = "[\"" + loader + "\"]";
        String versionsJson = "[\"" + gameVersion + "\"]";

        return "https://api.modrinth.com/v2/project/" + projectId + "/version"
                + "?include_changelog=false"
                + "&loaders=" + URLEncoder.encode(loadersJson, StandardCharsets.UTF_8)
                + "&game_versions=" + URLEncoder.encode(versionsJson, StandardCharsets.UTF_8);
    }

    private static class ModrinthVersion {
        @SerializedName("version_number")
        String versionNumber;

        @SerializedName("date_published")
        OffsetDateTime datePublished;
    }
}
