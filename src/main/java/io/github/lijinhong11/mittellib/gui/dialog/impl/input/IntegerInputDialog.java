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
import java.util.List;
import java.util.function.IntConsumer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class IntegerInputDialog extends AbstractInputDialog {
    private final int min;
    private final int max;
    private final int initial;
    private final IntConsumer callback;

    IntegerInputDialog(Component title, Component label, int initial, int min, int max, IntConsumer callback) {
        super(title, label);

        this.initial = initial;
        this.min = min;
        this.max = max;
        this.callback = callback;
    }

    public static IntegerInputDialog create(Component title, Component label, int min, int max, IntConsumer callback) {
        return new IntegerInputDialog(title, label, min, min, max, callback);
    }

    public static IntegerInputDialog create(
            Component title, Component label, int initial, int min, int max, IntConsumer callback) {
        return new IntegerInputDialog(title, label, initial, min, max, callback);
    }

    @Override
    void executeCallback(DialogResponseView drv) {
        callback.accept(drv.getFloat(INPUT_KEY).intValue());
    }

    @Override
    public @NotNull List<? extends DialogInput> getInputs() {
        return List.of(DialogInput.numberRange(INPUT_KEY, label, min, max)
                .step(1f)
                .initial((float) initial)
                .build());
    }
}
