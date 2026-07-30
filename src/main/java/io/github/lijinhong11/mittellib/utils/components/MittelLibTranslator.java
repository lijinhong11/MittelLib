package io.github.lijinhong11.mittellib.utils.components;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.message.ILanguageManager;
import io.github.lijinhong11.mittellib.message.MessageReplacement;
import java.text.MessageFormat;
import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.*;
import net.kyori.adventure.translation.Translator;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MittelLibTranslator implements Translator {
    private final ILanguageManager languageManager;

    public MittelLibTranslator(ILanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    @Override
    public @NotNull Key name() {
        return new NamespacedKey(MittelLib.getInstance(), "translator");
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return new MessageFormat(languageManager.getMsgByLanguage(locale.toLanguageTag(), key));
    }

    @Override
    public @Nullable Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        String key = component.key();
        if (!languageManager.getTranslationKeys().contains(key)) {
            return null;
        }

        Component raw = languageManager.getMsgComponentByLanguage(locale.toLanguageTag(), key);

        for (TranslationArgument arg : component.arguments()) {
            MutablePair<String, Component> pair = new MutablePair<>();

            if (arg instanceof TextComponent tc && tc.content().contains(MessageReplacement.PARSE_HEADER)) {
                String placeholder = tc.content().replaceFirst(MessageReplacement.PARSE_HEADER, "");
                Component value = tc.children().getFirst();
                if (value == null) {
                    value = Component.empty();
                }

                pair.setLeft(placeholder);
                pair.setValue(value);
            }

            if (!(arg instanceof VirtualComponent vc)) {
                continue;
            }

            if (!(vc.renderer() instanceof MessageReplacement)) {
                continue;
            }

            TextReplacementConfig cfg = TextReplacementConfig.builder()
                    .match(pair.left)
                    .replacement(pair.right)
                    .build();

            raw = raw.replaceText(cfg);
        }

        raw = raw.append(component.children());

        return raw;
    }
}
