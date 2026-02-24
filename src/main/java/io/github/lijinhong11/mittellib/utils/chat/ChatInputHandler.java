package io.github.lijinhong11.mittellib.utils.chat;

import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

/**
 * A chat input handler
 * <p>
 * {@link #test(Object)} is to check the chat message matches requirements (invoked asynchronously) <br>
 * {@link #onChat(Player, String)} will be invoked asynchronously when {@link #test(Object)} returns true
 * </p>
 */
public interface ChatInputHandler extends Predicate<String> {
    @ParametersAreNonnullByDefault
    void onChat(Player p, String msg);
}
