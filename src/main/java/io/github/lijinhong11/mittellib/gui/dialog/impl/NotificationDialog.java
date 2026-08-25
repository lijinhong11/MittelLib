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
package io.github.lijinhong11.mittellib.gui.dialog.impl;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.util.List;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple notice dialog with a message and a close button.
 */
public final class NotificationDialog extends AbstractDialog {
    private static final Component CLOSE = Component.translatable("gui.close");

    private final Component message;
    private final Component title;

    private NotificationDialog(@NotNull Component title, @NotNull Component message) {
        this.title = title;
        this.message = message;
    }

    public static @NotNull NotificationDialog create(
            @NotNull Component title, @NotNull Component message) {
        return new NotificationDialog(title, message);
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public @Nullable Component getExternalTitle() {
        return null;
    }

    @Override
    public @NotNull DialogType getDialogType() {
        ActionButton close = ActionButton.builder(CLOSE)
                .action(DialogAction.staticAction(ClickEvent.callback(Audience::closeDialog)))
                .build();
        return DialogType.notice(close);
    }

    @Override
    public @NotNull List<? extends DialogBody> getBody() {
        return List.of(DialogBody.plainMessage(message));
    }

    @Override
    public @NotNull List<? extends DialogInput> getInputs() {
        return List.of();
    }
}
