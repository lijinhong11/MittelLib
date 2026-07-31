package io.github.lijinhong11.mittellib.TESTS;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.item.MittelItem;
import io.github.lijinhong11.mittellib.item.components.impl.ChargedProjectilesComponent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ItemSerializationTEST implements Listener {
    @EventHandler
    public void onPlayer(PlayerItemHeldEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (item == null || item.isEmpty()) {
            return;
        }

        MittelItem ml = new MittelItem(item);
        ml.component(new ChargedProjectilesComponent(
                List.of(ItemStack.of(Material.APPLE), ItemStack.of(Material.ARROW, 10))));
        serialize(ml.get());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        deserialize(e.getPlayer());
    }

    public static void serialize(ItemStack item) {
        YamlConfiguration cfg = new YamlConfiguration();
        new MittelItem((item)).write(cfg);
        try {
            cfg.save(new File(MittelLib.getInstance().getDataFolder(), "tests.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deserialize(Player p) {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(
                new File(MittelLib.getInstance().getDataFolder(), "tests.yml"));
        p.give(MittelItem.readFromSection(cfg).get());
    }
}
