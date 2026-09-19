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
package io.github.lijinhong11.mittellib.message;

import io.github.lijinhong11.mittellib.utils.components.ComponentUtils;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.TranslationArgumentLike;
import net.kyori.adventure.text.VirtualComponentRenderer;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

public final class MessageReplacement
        implements Pair<String, String>, TranslationArgumentLike, VirtualComponentRenderer<Void> {
    @ApiStatus.Internal
    public static final String PARSE_HEADER = "__ml:";

    @RegExp
    private final String placeholder;

    private final String replacement;

    private MessageReplacement(@RegExp @NotNull String placeholder, @NotNull String replacement) {
        this.placeholder = placeholder;
        this.replacement = replacement;
    }

    public static @NotNull MessageReplacement replace(
            @RegExp @NotNull String placeholder, @NotNull String replacement) {
        return new MessageReplacement(placeholder, replacement);
    }

    public @NotNull String parse(@NotNull String message) {
        return message.replace(left(), right());
    }

    public @NotNull Component parse(@NotNull Component component) {
        return component.replaceText(b -> b.match(placeholder).replacement(replacement));
    }

    public @NotNull String left() {
        return placeholder;
    }

    public @NotNull String right() {
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
