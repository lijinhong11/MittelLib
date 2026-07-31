package io.github.lijinhong11.mittellib.gui.dialog.impl.input;

import io.github.lijinhong11.mittellib.MittelLib;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class BooleanInputDialog extends AbstractInputDialog {
    private final boolean initial;
    private final String onTrue;
    private final String onFalse;

    BooleanInputDialog(Component title, Consumer<String> callback, boolean initial, String onTrue, String onFalse) {
        super(title, callback);

        this.initial = initial;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
    }

    BooleanInputDialog(Component title, Consumer<String> callback, boolean initial) {
        super(title, callback);

        this.initial = initial;
        this.onTrue = MittelLib.getInstance().getLanguageManager().getMsg(null, "common.enabled");
        this.onFalse = MittelLib.getInstance().getLanguageManager().getMsg(null, "common.disabled");
    }

    public static BooleanInputDialog create(Component title, Consumer<String> callback, boolean initial) {
        return new BooleanInputDialog(title, callback, initial);
    }

    public static BooleanInputDialog create(
            Component title, Consumer<String> callback, boolean initial, String onTrue, String onFalse) {
        return new BooleanInputDialog(title, callback, initial, onTrue, onFalse);
    }

    @Override
    public @NotNull List<? extends DialogInput> getInputs() {
        return List.of(DialogInput.bool(INPUT_KEY, getTitle(), initial, onTrue, onFalse));
    }
}
