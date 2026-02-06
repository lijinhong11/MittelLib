package me.mmmjjkx.mittellib.actions.tool;

import lombok.experimental.UtilityClass;
import me.mmmjjkx.mittellib.actions.ToolAction;
import org.checkerframework.checker.index.qual.NonNegative;

@UtilityClass
public class ToolActions {
    public static ToolAction dropMultiple(int multi) {
        return new DropMultiple(multi);
    }

    public static ToolAction minePlaneBlocks(@NonNegative short range) {
        return new MineRangedPlane();
    }

    public static ToolAction mineCubeBlocks(@NonNegative short range) {
        return new MineRangedCube();
    }
}
