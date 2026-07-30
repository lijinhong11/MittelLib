package io.github.lijinhong11.mittellib.message;

import io.github.lijinhong11.mittellib.utils.components.ComponentUtils;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.TranslationArgumentLike;
import net.kyori.adventure.text.VirtualComponentRenderer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

public final class MessageReplacement
        implements Pair<String, String>, TranslationArgumentLike, VirtualComponentRenderer<Void> {
    @ApiStatus.Internal
    public static final String PARSE_HEADER = "__ml:";

    private final String placeholder;
    private final String replacement;

    private MessageReplacement(String placeholder, String replacement) {
        this.placeholder = placeholder;
        this.replacement = replacement;
    }

    public static MessageReplacement replace(String placeholder, String replacement) {
        return new MessageReplacement(placeholder, replacement);
    }

    public String parse(String message) {
        return message.replace(left(), right());
    }

    public String left() {
        return placeholder;
    }

    public String right() {
        return replacement;
    }

    @Override
    public @NotNull TranslationArgument asTranslationArgument() {
        return TranslationArgument.component(this);
    }

    @Override
    public @NotNull Component asComponent() {
        return Component.virtual(Void.class, this).append(ComponentUtils.deserialize(right()));
    }

    @Override
    public @UnknownNullability ComponentLike apply(@NonNull Void context) {
        return ComponentUtils.deserialize(right());
    }

    @Override
    public @NotNull String fallbackString() {
        return PARSE_HEADER + left();
    }
}
