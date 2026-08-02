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

import io.github.lijinhong11.mittellib.gui.dialog.impl.AbstractDialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractInputDialog extends AbstractDialog {
    static final String INPUT_KEY = "input";

    private static final Component YES = Component.translatable("gui.yes");
    private static final Component CANCEL = Component.translatable("gui.cancel");

    private final Component title;
    protected final Consumer<String> callback;

    AbstractInputDialog(Component title, Consumer<String> callback) {
        this.title = title;
        this.callback = callback;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public @Nullable Component externalTitle() {
        return null;
    }

    @Override
    public @NotNull List<? extends DialogBody> getBody() {
        return List.of();
    }

    @Override
    public @NotNull DialogType getDialogType() {
        return DialogType.confirmation(
                ActionButton.builder(YES)
                        .action(DialogAction.customClick(
                                (res, a) -> callback.accept(res.getText(INPUT_KEY)),
                                ClickCallback.Options.builder().build()))
                        .build(),
                ActionButton.builder(CANCEL)
                        .action(DialogAction.staticAction(ClickEvent.callback(Audience::closeDialog)))
                        .build());
    }
}
