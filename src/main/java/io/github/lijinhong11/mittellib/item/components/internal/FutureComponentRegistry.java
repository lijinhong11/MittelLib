package io.github.lijinhong11.mittellib.item.components.internal;

import io.github.lijinhong11.mittellib.utils.enums.MCVersion;

@SuppressWarnings({"unchecked", ""})
class FutureComponentRegistry {
    static {
    }

    static void register() {
        MCVersion mcv = MCVersion.getCurrent();
        if (mcv.isAtLeast(MCVersion.V26_2_X)) {}
    }
}
