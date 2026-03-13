package io.github.lijinhong11.mittellib.actions.player;

import io.github.lijinhong11.mittellib.actions.PlayerAction;
import lombok.experimental.UtilityClass;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class PlayerActions {
    public static PlayerAction giveEffect(@NotNull PotionEffect potionEffect) {
        return new GiveEffect(potionEffect);
    }
}
