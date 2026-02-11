package me.mmmjjkx.mittellib.iface.actions;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface PlayerAction extends Consumer<Player> {
    void revert(Player player);
}
