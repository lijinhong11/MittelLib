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

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FloatInputDialog extends AbstractInputDialog {
    private final float min;
    private final float max;
    private final float initial;
    private final float step;
    private final FloatConsumer callback;

    FloatInputDialog(
            Component title, Component label, float initial, float min, float max, float step, FloatConsumer callback) {
        this(title, label, initial, min, max, step, callback, null);
    }

    FloatInputDialog(
            Component title,
            Component label,
            float initial,
            float min,
            float max,
            float step,
            FloatConsumer callback,
            @Nullable Runnable cancelCallback) {
        super(title, label, cancelCallback);

        this.initial = initial;
        this.min = min;
        this.max = max;
        this.step = step;
        this.callback = callback;
    }

    public static FloatInputDialog create(
            Component title, Component label, float min, float max, float step, FloatConsumer callback) {
        return new FloatInputDialog(title, label, min, min, max, step, callback);
    }

    public static FloatInputDialog create(
            @NotNull Component title,
            @NotNull Component label,
            float min,
            float max,
            float step,
            @NotNull FloatConsumer callback,
            @Nullable Runnable cancelCallback) {
        return new FloatInputDialog(title, label, min, min, max, step, callback, cancelCallback);
    }

    public static FloatInputDialog create(
            Component title, Component label, float initial, float min, float max, float step, FloatConsumer callback) {
        return new FloatInputDialog(title, label, initial, min, max, step, callback);
    }

    public static FloatInputDialog create(
            @NotNull Component title,
            @NotNull Component label,
            float initial,
            float min,
            float max,
            float step,
            @NotNull FloatConsumer callback,
            @Nullable Runnable cancelCallback) {
        return new FloatInputDialog(title, label, initial, min, max, step, callback, cancelCallback);
    }

    public static FloatInputDialog createWithDefaultStep(
            Component title, Component label, float initial, float min, float max, FloatConsumer callback) {
        return new FloatInputDialog(title, label, initial, min, max, 0.1f, callback);
    }

    public static FloatInputDialog createWithDefaultStep(
            @NotNull Component title,
            @NotNull Component label,
            float initial,
            float min,
            float max,
            @NotNull FloatConsumer callback,
            @Nullable Runnable cancelCallback) {
        return new FloatInputDialog(title, label, initial, min, max, 0.1f, callback, cancelCallback);
    }

    @Override
    void executeCallback(DialogResponseView drv) {
        callback.accept(drv.getFloat(INPUT_KEY).floatValue());
    }

    @Override
    public @NotNull List<? extends DialogInput> getInputs() {
        return List.of(DialogInput.numberRange(INPUT_KEY, label, min, max)
                .step(step)
                .initial(initial)
                .build());
    }
}
