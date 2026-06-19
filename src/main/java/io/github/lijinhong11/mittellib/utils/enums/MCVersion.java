package io.github.lijinhong11.mittellib.utils.enums;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * An enum which contains Minecraft versions to identify current Minecraft version.
 * <p>
 * Versions are identified by their <b>protocol version</b> first, then disambiguated
 * by the <b>version string</b> (e.g. "1.21.4") for minor releases that share the same
 * protocol version. If neither matches exactly, the nearest known lower version is used.
 * As a last resort, the latest known version is returned.
 */
@Getter
public enum MCVersion {
    V1_20_1(763, "1.20.1"),
    V1_20_2(764, "1.20.2"),
    V1_20_3(765, "1.20.3"),
    V1_20_4(765, "1.20.4"),
    V1_20_5(766, "1.20.5"),
    V1_20_6(766, "1.20.6"),
    V1_21_1(767, "1.21.1"),
    V1_21_2(768, "1.21.2"),
    V1_21_3(768, "1.21.3"),
    V1_21_4(769, "1.21.4"),
    V1_21_5(770, "1.21.5"),
    V1_21_6(771, "1.21.6"),
    V1_21_7(772, "1.21.7"),
    V1_21_8(772, "1.21.8"),
    V1_21_9(773, "1.21.9"),
    V1_21_10(773, "1.21.10"),
    V1_21_11(774, "1.21.11"),
    V26_1_X(775, "26.1"),
    V26_2_X(776, "26.2"),
    ;

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
    @NotNull
    public static MCVersion getCurrent() {
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
