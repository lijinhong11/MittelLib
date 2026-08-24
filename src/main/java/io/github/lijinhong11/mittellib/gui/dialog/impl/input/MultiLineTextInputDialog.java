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
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class MultiLineTextInputDialog extends AbstractInputDialog {
    private final int maxLength;
    private final int maxLines;
    private final int height;
    private final List<String> initial;
    private final Consumer<List<String>> callback;

    MultiLineTextInputDialog(
            Component title,
            Component label,
            Consumer<List<String>> callback,
            int maxLength,
            int maxLines,
            int height,
            List<String> initial) {
        super(title, label);

        this.callback = callback;
        this.maxLength = maxLength;
        this.maxLines = maxLines;
        this.height = height;
        this.initial = initial;
    }

    public static MultiLineTextInputDialog create(
            @NotNull Component title, @NotNull Component label, @NotNull Consumer<List<String>> callback) {
        return new MultiLineTextInputDialog(title, label, callback, 7500, 50, 0, new ArrayList<>());
    }

    public static MultiLineTextInputDialog create(
            @NotNull Component title,
            @NotNull Component label,
            @NotNull Consumer<List<String>> callback,
            int maxLength,
            int maxLines) {
        return new MultiLineTextInputDialog(title, label, callback, maxLength, maxLines, 0, new ArrayList<>());
    }

    public static MultiLineTextInputDialog create(
            @NotNull Component title,
            @NotNull Component label,
            @NotNull Consumer<List<String>> callback,
            int maxLength,
            int maxLines,
            int height,
            @NotNull List<String> initial) {
        return new MultiLineTextInputDialog(title, label, callback, maxLength, maxLines, height, initial);
    }

    @Override
    public @NotNull List<? extends DialogInput> getInputs() {
        String init = "";
        for (String s : initial) {
            init = init.concat(s);
            if (!s.equals(initial.getLast())) {
                init = init.concat("\n");
            }
        }

        return List.of(DialogInput.text(INPUT_KEY, label)
                .width(calculateInputWidth(maxLength, maxLines))
                .maxLength(maxLength)
                .initial(init)
                .multiline(TextDialogInput.MultilineOptions.create(maxLines, height > 0 ? height : null))
                .build());
    }

    @Override
    void executeCallback(DialogResponseView drv) {
        callback.accept(Arrays.asList(drv.getText(INPUT_KEY).split("\\R", -1)));
    }
}
