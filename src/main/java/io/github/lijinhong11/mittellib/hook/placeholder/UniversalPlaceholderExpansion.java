package io.github.lijinhong11.mittellib.hook.placeholder;

import io.github.lijinhong11.mittellib.utils.ComponentUtils;
import io.github.miniplaceholders.api.Expansion;
import java.util.*;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class UniversalPlaceholderExpansion {
    private final Map<String, PlaceholderEntry> placeholders = new HashMap<>();
    private boolean registered = false;

    public abstract @NotNull String identifier();

    public abstract @NotNull String author();

    public abstract @NotNull String version();

    protected void registerPlaceholder(
            @NotNull String placeholder, @NotNull PlaceholderType type, @NotNull PlaceholderHandle handle) {
        if (registered) {
            throw new IllegalStateException("Cannot register placeholder after expansion registered");
        }

        placeholders.put(placeholder.toLowerCase(Locale.ROOT), new PlaceholderEntry(type, handle));
    }

    public final void register() {
        registered = true;

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPI().register();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            registerMiniPlaceholders();
        }
    }

    @FunctionalInterface
    public interface PlaceholderHandle {
        String parse(@Nullable OfflinePlayer viewer, @Nullable OfflinePlayer target, String[] args);
    }

    public enum PlaceholderType {
        GLOBAL,
        AUDIENCE,
        RELATIONAL
    }

    private record PlaceholderEntry(PlaceholderType type, PlaceholderHandle handle) {}

    private class PAPI extends PlaceholderExpansion implements Relational {
        @Override
        public @NotNull String getIdentifier() {
            return identifier();
        }

        @Override
        public @NotNull String getAuthor() {
            return author();
        }

        @Override
        public @NotNull String getVersion() {
            return version();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
            if (params.isEmpty()) return null;

            String[] split = params.split("_");
            String key = split[0].toLowerCase(Locale.ROOT);

            PlaceholderEntry entry = placeholders.get(key);
            if (entry == null) return null;

            if (entry.type == PlaceholderType.RELATIONAL) return null;

            String[] args = Arrays.copyOfRange(split, 1, split.length);

            return entry.handle.parse(player, null, args);
        }

        @Override
        public @Nullable String onPlaceholderRequest(Player one, Player two, String identifier) {
            if (identifier.isEmpty()) return null;

            String[] split = identifier.split("_");
            String key = split[0].toLowerCase(Locale.ROOT);

            PlaceholderEntry entry = placeholders.get(key);
            if (entry == null) return null;

            if (entry.type != PlaceholderType.RELATIONAL) return null;

            String[] args = Arrays.copyOfRange(split, 1, split.length);

            return entry.handle.parse(one, two, args);
        }
    }

    private void registerMiniPlaceholders() {
        Expansion.Builder builder =
                Expansion.builder(identifier()).author(author()).version(version());

        for (Map.Entry<String, PlaceholderEntry> entry : placeholders.entrySet()) {
            String placeholder = entry.getKey();
            PlaceholderEntry data = entry.getValue();

            switch (data.type) {
                case GLOBAL ->
                    builder.globalPlaceholder(placeholder, (q, ctx) -> {
                        List<String> args = new ArrayList<>();
                        while (q.hasNext()) {
                            args.add(q.pop().value());
                        }

                        String result = data.handle.parse(null, null, args.toArray(new String[0]));
                        if (result == null) return Tag.inserting(Component.empty());

                        return Tag.inserting(ComponentUtils.deserialize(result));
                    });
                case AUDIENCE ->
                    builder.audiencePlaceholder(Player.class, placeholder, (player, q, ctx) -> {
                        List<String> args = new ArrayList<>();
                        while (q.hasNext()) {
                            args.add(q.pop().value());
                        }

                        String result = data.handle.parse(player, null, args.toArray(new String[0]));
                        if (result == null) return Tag.inserting(Component.empty());

                        return Tag.inserting(ComponentUtils.deserialize(result));
                    });
                case RELATIONAL ->
                    builder.relationalPlaceholder(Player.class, placeholder, (a1, a2, q, ctx) -> {
                        List<String> args = new ArrayList<>();
                        while (q.hasNext()) {
                            args.add(q.pop().value());
                        }

                        String result = data.handle.parse(a1, a2, args.toArray(new String[0]));
                        if (result == null) return Tag.inserting(Component.empty());

                        return Tag.inserting(ComponentUtils.deserialize(result));
                    });
            }
        }

        builder.build().register();
    }
}
