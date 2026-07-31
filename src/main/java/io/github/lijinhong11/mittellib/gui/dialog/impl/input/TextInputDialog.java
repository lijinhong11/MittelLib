package io.github.lijinhong11.mittellib.gui.dialog.impl.input;

import io.papermc.paper.registry.data.dialog.input.DialogInput;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class TextInputDialog extends AbstractInputDialog {
    private final int maxLength;
    private final String initial;

    TextInputDialog(Component title, Consumer<String> callback, int maxLength, String initial) {
        super(title, callback);

        this.maxLength = maxLength;
        this.initial = initial;
    }

    public static TextInputDialog create(Component title, Consumer<String> callback) {
        return new TextInputDialog(title, callback, 512, "");
    }

    public static TextInputDialog create(Component title, Consumer<String> callback, int maxLength) {
        return new TextInputDialog(title, callback, maxLength, "");
    }

    public static TextInputDialog create(Component title, Consumer<String> callback, int maxLength, String initial) {
        return new TextInputDialog(title, callback, maxLength, initial);
    }

    @Override
    public @NotNull List<? extends DialogInput> getInputs() {
        return List.of(DialogInput.text(INPUT_KEY, getTitle())
                .maxLength(maxLength)
                .initial(initial)
                .build());
    }
}
