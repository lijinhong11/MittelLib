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
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class TextInputDialog extends AbstractInputDialog {
    private final int maxLength;
    private final String initial;
    private final Consumer<String> callback;

    TextInputDialog(Component title, Component label, Consumer<String> callback, int maxLength, String initial) {
        super(title, label);

        this.callback = callback;
        this.maxLength = maxLength;
        this.initial = initial;
    }

    public static TextInputDialog create(
            @NotNull Component title, @NotNull Component label, @NotNull Consumer<String> callback) {
        return new TextInputDialog(title, label, callback, 512, "");
    }

    public static TextInputDialog create(
            @NotNull Component title, @NotNull Component label, @NotNull Consumer<String> callback, int maxLength) {
        return new TextInputDialog(title, label, callback, maxLength, "");
    }

    public static TextInputDialog create(
            @NotNull Component title,
            @NotNull Component label,
            @NotNull Consumer<String> callback,
            int maxLength,
            @NotNull String initial) {
        return new TextInputDialog(title, label, callback, maxLength, initial);
    }

    @Override
    public @NotNull List<? extends DialogInput> getInputs() {
        return List.of(DialogInput.text(INPUT_KEY, getTitle())
                .maxLength(maxLength)
                .initial(initial)
                .build());
    }

    @Override
    void executeCallback(DialogResponseView drv) {
        callback.accept(drv.getText(INPUT_KEY));
    }
}
