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
package io.github.lijinhong11.mittellib.gui.dialog.impl.input;

import io.github.lijinhong11.mittellib.MittelLib;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BooleanInputDialog extends AbstractInputDialog {
    private final boolean initial;
    private final String onTrue;
    private final String onFalse;
    private final BooleanConsumer callback;

    BooleanInputDialog(
            Component title,
            Component label,
            BooleanConsumer callback,
            boolean initial,
            String onTrue,
            String onFalse) {
        this(title, label, callback, initial, onTrue, onFalse, null);
    }

    BooleanInputDialog(
            Component title,
            Component label,
            BooleanConsumer callback,
            boolean initial,
            String onTrue,
            String onFalse,
            @Nullable Runnable cancelCallback) {
        super(title, label, cancelCallback);

        this.callback = callback;
        this.initial = initial;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
    }

    BooleanInputDialog(Component title, Component label, BooleanConsumer callback, boolean initial) {
        this(title, label, callback, initial, null);
    }

    BooleanInputDialog(
            Component title,
            Component label,
            BooleanConsumer callback,
            boolean initial,
            @Nullable Runnable cancelCallback) {
        super(title, label, cancelCallback);

        this.callback = callback;
        this.initial = initial;
        this.onTrue = MittelLib.getInstance().getLanguageManager().getMsg(null, "common.enabled");
        this.onFalse = MittelLib.getInstance().getLanguageManager().getMsg(null, "common.disabled");
    }

    public static BooleanInputDialog create(
            @NotNull Component title, @NotNull Component label, @NotNull BooleanConsumer callback, boolean initial) {
        return new BooleanInputDialog(title, label, callback, initial);
    }

    public static BooleanInputDialog create(
            @NotNull Component title,
            @NotNull Component label,
            @NotNull BooleanConsumer callback,
            boolean initial,
            @NotNull String onTrue,
            @NotNull String onFalse) {
        return new BooleanInputDialog(title, label, callback, initial, onTrue, onFalse);
    }

    public static BooleanInputDialog create(
            @NotNull Component title,
            @NotNull Component label,
            @NotNull BooleanConsumer callback,
            boolean initial,
            @Nullable Runnable cancelCallback) {
        return new BooleanInputDialog(title, label, callback, initial, cancelCallback);
    }

    public static BooleanInputDialog create(
            @NotNull Component title,
            @NotNull Component label,
            @NotNull BooleanConsumer callback,
            boolean initial,
            @NotNull String onTrue,
            @NotNull String onFalse,
            @Nullable Runnable cancelCallback) {
        return new BooleanInputDialog(title, label, callback, initial, onTrue, onFalse, cancelCallback);
    }

    @Override
    public @NotNull List<? extends DialogInput> getInputs() {
        return List.of(DialogInput.bool(INPUT_KEY, label, initial, onTrue, onFalse));
    }

    @Override
    void executeCallback(DialogResponseView drv) {
        callback.accept(drv.getBoolean(INPUT_KEY).booleanValue());
    }
}
