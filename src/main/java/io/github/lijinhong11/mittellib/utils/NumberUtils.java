package io.github.lijinhong11.mittellib.utils;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.utils.random.FastRandom;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
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

    public static boolean matchChance(double chancePercent) {
        return (chancePercent / 100) >= 1 || FastRandom.nextDouble(1) < (chancePercent / 100);
    }

    public static String formatSeconds(CommandSender cs, int totalSeconds) {
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
}
