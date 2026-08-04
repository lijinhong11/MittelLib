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
package io.github.lijinhong11.mittellib.utils.components;

import io.github.lijinhong11.mittellib.message.ILanguageManager;
import io.github.lijinhong11.mittellib.message.MessageReplacement;
import java.text.MessageFormat;
import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MittelLibTranslator implements Translator {
    private final ILanguageManager languageManager;
    private final Key name;
    private final String keyPrefix;

    public MittelLibTranslator(Plugin plugin, ILanguageManager languageManager) {
        this(new NamespacedKey(plugin, "translator"), languageManager);
    }

    public MittelLibTranslator(Key name, ILanguageManager languageManager) {
        this.languageManager = languageManager;
        this.name = name;
        this.keyPrefix = name.namespace() + ".";
    }

    @Override
    public @NotNull Key name() {
        return name;
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        String localKey = localKey(key);
        if (localKey == null || !languageManager.getTranslationKeys().contains(localKey)) {
            return null;
        }
        return new MessageFormat(languageManager.getMsgByLanguage(locale.toLanguageTag(), localKey));
    }

    @Override
    public @Nullable Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        String localKey = localKey(component.key());
        if (localKey == null || !languageManager.getTranslationKeys().contains(localKey)) {
            return null;
        }

        Component raw = languageManager.getMsgComponentByLanguage(locale.toLanguageTag(), localKey);

        for (TranslationArgument arg : component.arguments()) {
            Object value = arg.value();
            if (!(value instanceof ComponentLike componentLike)) {
                continue;
            }

            Component argument = componentLike.asComponent();
            Replacement replacement = getReplacement(argument);
            if (replacement == null) {
                continue;
            }
            TextReplacementConfig cfg = TextReplacementConfig.builder()
                    .matchLiteral(replacement.placeholder())
                    .replacement(replacement.value())
                    .build();

            raw = raw.replaceText(cfg);
        }

        raw = raw.append(component.children().stream()
                .map(c -> GlobalTranslator.render(c, locale))
                .toList());

        return raw;
    }

    private @Nullable String localKey(String key) {
        if (!key.startsWith(keyPrefix) || key.length() == keyPrefix.length()) {
            return null;
        }
        return key.substring(keyPrefix.length());
    }

    private static @Nullable Replacement getReplacement(Component argument) {
        if (argument instanceof VirtualComponent virtual
                && virtual.renderer() instanceof MessageReplacement replacement) {
            return new Replacement(replacement.left(), children(argument));
        }

        if (!(argument instanceof TextComponent text) || !text.content().startsWith(MessageReplacement.PARSE_HEADER)) {
            return null;
        }

        String placeholder = text.content().substring(MessageReplacement.PARSE_HEADER.length());
        return new Replacement(placeholder, children(text));
    }

    private static Component children(Component component) {
        return component.children().isEmpty()
                ? Component.empty()
                : Component.empty().children(component.children());
    }

    private record Replacement(String placeholder, Component value) {}
}
