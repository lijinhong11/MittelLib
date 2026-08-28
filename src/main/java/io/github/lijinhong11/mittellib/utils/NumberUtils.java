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
package io.github.lijinhong11.mittellib.utils;

import io.github.lijinhong11.mittellib.MittelLib;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class NumberUtils {
    public static int asUnsigned(int i) {
        i = i < 0 ? -i : i;
        return i;
    }

    public static long asUnsigned(long l) {
        l = l < 0 ? -l : l;
        return l;
    }

    public static double asUnsigned(double d) {
        d = d < 0 ? -d : d;
        return d;
    }

    public static float asUnsigned(float f) {
        f = f < 0 ? -f : f;
        return f;
    }

    public static @Nullable Number asNumber(Object obj) {
        return obj instanceof Number n ? n : null;
    }

    public static @NotNull String formatSeconds(@Nullable CommandSender cs, int totalSeconds) {
        String secondText = MittelLib.getInstance().getLanguageManager().getMsg(cs, "time.second");
        String secondsText = MittelLib.getInstance().getLanguageManager().getMsg(cs, "time.seconds");
        String minuteText = MittelLib.getInstance().getLanguageManager().getMsg(cs, "time.minute");
        String minutesText = MittelLib.getInstance().getLanguageManager().getMsg(cs, "time.minutes");
        String hourText = MittelLib.getInstance().getLanguageManager().getMsg(cs, "time.hour");
        String hoursText = MittelLib.getInstance().getLanguageManager().getMsg(cs, "time.hours");
        String dayText = MittelLib.getInstance().getLanguageManager().getMsg(cs, "time.day");
        String daysText = MittelLib.getInstance().getLanguageManager().getMsg(cs, "timedays");

        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append(" ").append(days == 1 ? dayText : daysText).append(" ");
        }
        if (hours > 0) {
            sb.append(hours)
                    .append(" ")
                    .append(hours == 1 ? hourText : hoursText)
                    .append(" ");
        }
        if (minutes > 0) {
            sb.append(minutes)
                    .append(" ")
                    .append(minutes == 1 ? minuteText : minutesText)
                    .append(" ");
        }
        if (seconds > 0 || sb.isEmpty()) {
            sb.append(seconds).append(" ").append(seconds == 1 ? secondText : secondsText);
        }

        return sb.toString().trim();
    }

    public static @NotNull String formatCountdown(int totalSeconds) {
        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return MittelLib.getInstance()
                .getCountdownFormat()
                .replace("%days%", String.valueOf(days))
                .replace("%hours%", String.valueOf(hours))
                .replace("%minutes%", String.valueOf(minutes))
                .replace("%seconds%", String.valueOf(seconds));
    }
}
