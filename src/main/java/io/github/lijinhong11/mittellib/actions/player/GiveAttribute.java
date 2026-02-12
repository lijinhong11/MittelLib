package io.github.lijinhong11.mittellib.actions.player;

import lombok.AllArgsConstructor;
import io.github.lijinhong11.mittellib.iface.actions.PlayerAction;
import io.github.lijinhong11.mittellib.utils.enums.PlayerAttributeType;
import org.bukkit.entity.Player;

@AllArgsConstructor
class GiveAttribute implements PlayerAction {
    private final PlayerAttributeType playerAttributeType;
    private final double num;

    @Override
    public void accept(Player player) {
        switch (playerAttributeType) {
            case MONEY -> {

            }
            case POINTS -> {

            }
            case EXP -> player.giveExp((int) Math.floor(num));
            case HEALTH -> player.heal(num);
        }
    }

    @Override
    public void revert(Player player) {
        switch (playerAttributeType) {

        }
    }
}
