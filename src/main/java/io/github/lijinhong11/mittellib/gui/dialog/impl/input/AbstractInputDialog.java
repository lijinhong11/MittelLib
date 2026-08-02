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
