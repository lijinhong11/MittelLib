package io.github.lijinhong11.mittellib.hook.placeholder;

import io.github.lijinhong11.mittellib.utils.ComponentUtils;
import io.github.miniplaceholders.api.Expansion;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class UniversalPlaceholderExpansion {
    private final Map<String, Pair<PlaceholderType, PlaceholderHandle>> placeholderHandleMap = new HashMap<>();

    public abstract @NotNull String identifier();

    public abstract @NotNull String author();

    public abstract @NotNull String version();

    protected void registerPlaceholder(String placeholder, PlaceholderType type, PlaceholderHandle placeholderHandle) {
        placeholderHandleMap.put(placeholder, ImmutablePair.of(type, placeholderHandle));
    }

    public final void register() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPI().register();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            registerMiniPlaceholders();
        }
    }

    @FunctionalInterface
    public interface PlaceholderHandle {
        String parse(@Nullable OfflinePlayer player, @Nullable OfflinePlayer relationalPlayerTwo, String[] args);
    }

    public enum PlaceholderType {
        GLOBAL,
        RELATIONAL
    }

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
        public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
            String[] args = params.split("_");

            if (args.length < 1) {
                return null;
            }

            Pair<PlaceholderType, PlaceholderHandle> pair = placeholderHandleMap.get(args[0]);
            PlaceholderHandle handle = pair.getRight();
            if (handle == null) {
                return null;
            }

            PlaceholderType type = pair.getLeft();
            if (type == PlaceholderType.RELATIONAL) {
                return null;
            }

            String[] copy = Arrays.copyOfRange(args, 1, args.length);
            return handle.parse(player, null, copy);
        }

        @Override
        public String onPlaceholderRequest(Player one, Player two, String identifier) {
            Pair<PlaceholderType, PlaceholderHandle> pair = placeholderHandleMap.get(identifier);
            PlaceholderHandle handle = pair.getRight();
            if (handle == null) {
                return null;
            }

            PlaceholderType type = pair.getLeft();
            if (type != PlaceholderType.RELATIONAL) {
                return null;
            }

            return handle.parse(one, two, new String[]{});
        }
    }

    private void registerMiniPlaceholders() {
        Expansion.Builder expansionBuilder = Expansion.builder(identifier())
                .author(author())
                .version(version());

        for (Map.Entry<String, Pair<PlaceholderType, PlaceholderHandle>> pair : placeholderHandleMap.entrySet()) {
            String placeholder = pair.getKey();
            PlaceholderType type = pair.getValue().getKey();
            PlaceholderHandle handle = pair.getValue().getValue();

            if (type == PlaceholderType.GLOBAL) {
                expansionBuilder.globalPlaceholder(placeholder, (q, ctx) -> {
                    List<String> args = new ArrayList<>();
                    while (q.hasNext()) {
                        args.add(q.pop().value());
                    }

                    return Tag.inserting(ComponentUtils.deserialize(handle.parse(null, null, args.toArray(new String[0]))));
                });
            }

            if (type == PlaceholderType.RELATIONAL) {
                expansionBuilder.relationalPlaceholder(Player.class, placeholder, (a1, a2, q, ctx) -> {
                    List<String> args = new ArrayList<>();
                    while (q.hasNext()) {
                        args.add(q.pop().value());
                    }

                    return Tag.inserting(ComponentUtils.deserialize(handle.parse(a1, a2, args.toArray(new String[0]))));
                });
            }
        }

        Expansion expansion = expansionBuilder.build();
        expansion.register();
    }
}
