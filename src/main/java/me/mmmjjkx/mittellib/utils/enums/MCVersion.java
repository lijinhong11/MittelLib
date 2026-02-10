package me.mmmjjkx.mittellib.utils.enums;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

@Getter
public enum MCVersion {
    V1_20(763),
    V1_20_1(763),
    V1_20_2(764),
    V1_20_3(765),
    V1_20_4(765),
    V1_20_5(766),
    V1_20_6(766),
    V1_21(767),
    V1_21_1(767),
    V1_21_2(768),
    V1_21_3(768),
    V1_21_4(769),
    V1_21_5(770),
    V1_21_6(771),
    V1_21_7(772),
    V1_21_8(772),
    V1_21_9(773),
    V1_21_10(773),
    V1_21_11(774),
    /**
    <b>NOTE: this is not a final version</b>
     */
    V26_1(0x40000127)
    ;

    @NotNull
    public static MCVersion getCurrent() {
        int current = Bukkit.getUnsafe().getProtocolVersion();

        for (MCVersion version : values()) {
            if (version.protocolVersion == current) {
                return version;
            }
        }

        throw new RuntimeException(new UnsupportedOperationException("This version of Minecraft is not supported"));
    }

    private final @NonNegative int protocolVersion;

    MCVersion(@NonNegative int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public boolean isAtLeast(MCVersion mcVersion) {
        return protocolVersion >= mcVersion.protocolVersion;
    }
}
