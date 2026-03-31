package io.github.lijinhong11.mittellib.gui;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.value.qual.ArrayLenRange;
import org.jetbrains.annotations.NotNull;

public interface MittelGUI {
    void open(Player player);

    void closeForAllPlayers();

    static Builder builder() {
        return new MittelGUIBuilderImpl();
    }

    interface Builder {
        Builder title(@NotNull Component title);

        InvBuilder inventory(int size);

        AnvilInvBuilder anvil();
    }

    interface AnvilInvBuilder {
        AnvilInvBuilder nameInputHandler();

        MittelGUI build();
    }

    interface InvBuilder {
        InvBuilder bind(char bind, @NotNull ItemStack item);

        InvBuilder layout(@ArrayLenRange(from = 1, to = 6) String... layout);

        MittelGUI build();
    }
}
