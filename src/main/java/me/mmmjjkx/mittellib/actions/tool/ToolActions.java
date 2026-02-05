package me.mmmjjkx.mittellib.actions.tool;

import lombok.experimental.UtilityClass;
import me.mmmjjkx.mittellib.actions.ToolAction;

@UtilityClass
public class ToolActions {
    public static ToolAction dropMultiple(int multi) {
        return new DropMultiple(multi);
    }
}
