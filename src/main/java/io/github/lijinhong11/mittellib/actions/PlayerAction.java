package io.github.lijinhong11.mittellib.actions;

import java.util.function.Consumer;
import org.bukkit.entity.Player;

public interface PlayerAction extends Consumer<Player> {
    void revert(Player player);
}
