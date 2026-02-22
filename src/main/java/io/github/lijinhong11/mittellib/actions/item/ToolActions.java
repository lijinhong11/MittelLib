package io.github.lijinhong11.mittellib.actions.item;

import io.github.lijinhong11.mittellib.iface.actions.ItemAction;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.index.qual.NonNegative;

@UtilityClass
public class ToolActions {
    public static ItemAction dropMultiple(int multi) {
        return new DropMultiple(multi);
    }

    public static ItemAction minePlaneBlocks(@NonNegative short range) {
        return new MineRangedPlane();
    }

    public static ItemAction mineCubeBlocks(@NonNegative short range) {
        return new MineRangedCube();
    }
}
