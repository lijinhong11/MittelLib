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
package io.github.lijinhong11.mittellib.utils.enums;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * An enum which contains Minecraft versions to identify current Minecraft version.
 * <p>
 * Versions are identified by their <b>protocol version</b> first, then disambiguated
 * by the <b>version string</b> (e.g. "26.1.2") for minor releases that share the same
 * protocol version. If neither matches exactly, the nearest known lower version is used.
 * As a last resort, the latest known version is returned.
 */
@Getter
public enum MCVersion {
    V26_1_X(775, "26.1"),
    V26_2_X(776, "26.2"),
    V26_3_X(777, "26.3");

    private static MCVersion current;
    private final int protocolVersion;
    private final String versionString;

    MCVersion(int protocolVersion, String versionString) {
        this.protocolVersion = protocolVersion;
        this.versionString = versionString;
    }

    /**
     * Get current Minecraft version.
     * <p>
     * Resolution order:
     * <ol>
     *   <li>Match by protocol version + version string (exact)</li>
     *   <li>Match by protocol version only</li>
     *   <li>Nearest known lower protocol version</li>
     *   <li>Latest known version (fallback)</li>
     * </ol>
     */
    @NotNull public static MCVersion getCurrent() {
        if (current != null) return current;

        int protocol = Bukkit.getUnsafe().getProtocolVersion();
        String mcVersion = Bukkit.getMinecraftVersion();

        MCVersion protocolMatch = null;
        MCVersion lowerMatch = null;

        for (MCVersion v : values()) {
            if (v.protocolVersion == protocol) {
                if (mcVersion.equals(v.versionString) || mcVersion.startsWith(v.versionString + ".")) {
                    current = v;
                    return v;
                }
                if (protocolMatch == null || v.ordinal() > protocolMatch.ordinal()) {
                    protocolMatch = v;
                }
            } else if (v.protocolVersion < protocol) {
                if (lowerMatch == null || v.protocolVersion > lowerMatch.protocolVersion) {
                    lowerMatch = v;
                }
            }
        }

        if (protocolMatch != null) {
            current = protocolMatch;
            return protocolMatch;
        }

        if (lowerMatch != null) {
            current = lowerMatch;
            return lowerMatch;
        }

        current = values()[values().length - 1];
        return current;
    }

    public boolean isAtLeast(MCVersion v) {
        return protocolVersion >= v.protocolVersion;
    }

    public boolean isLowerThan(MCVersion v) {
        return protocolVersion < v.protocolVersion;
    }
}
