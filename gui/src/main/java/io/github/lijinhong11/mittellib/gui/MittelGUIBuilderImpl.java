package io.github.lijinhong11.mittellib.gui;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.value.qual.ArrayLenRange;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

class MittelGUIBuilderImpl implements MittelGUI.Builder {
    private @Nullable Component title;

    @Override
    public MittelGUI.Builder title(@Nullable Component title) {
        this.title = title;
        return this;
    }

    @Override
    public MittelGUI.InvBuilder inventory(int size) {
        Preconditions.checkArgument(9 <= size && size <= 54, "inventory size is not valid");
        return new InvBuilderImpl(title, size);
    }

    @Override
    public MittelGUI.AnvilInvBuilder anvil() {
        return null;
    }

    @RequiredArgsConstructor
    static class InvBuilderImpl implements MittelGUI.InvBuilder {
        private final Component title;
        private final int size;

        private final Map<Character, ItemStack> binds = new HashMap<>();
        private String[] layout = new String[6];

        @Override
        public MittelGUI.InvBuilder bind(char bind, @NonNull ItemStack item) {
            binds.put(bind, item);
            return this;
        }

        @Override
        public MittelGUI.InvBuilder layout(@ArrayLenRange(from = 1, to = 6) String... layout) {
            Preconditions.checkArgument(1 <= layout.length && layout.length <= 6, "layout size is not valid");
            this.layout = layout;
            return this;
        }

        @Override
        public MittelGUI build() {
            return null;
        }
    }
}
