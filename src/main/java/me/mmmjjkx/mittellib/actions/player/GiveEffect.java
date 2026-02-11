package me.mmmjjkx.mittellib.actions.player;

import lombok.AllArgsConstructor;
import me.mmmjjkx.mittellib.iface.actions.PlayerAction;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@AllArgsConstructor
class GiveEffect implements PlayerAction {
    private final PotionEffect giveEffect;

    @Override
    public void accept(Player player) {
        player.addPotionEffect(giveEffect);
    }

    @Override
    public void revert(Player player) {
        player.removePotionEffect(giveEffect.getType());
    }
}
