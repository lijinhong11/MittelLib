package io.github.lijinhong11.mittellib.hook.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private static Economy economy;

    public static void init() {
        if (economy != null) {
            return;
        }

        RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (service != null) {
            economy = service.getProvider();
        }
    }

    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public void setBalance(OfflinePlayer player, double amount) {
        double delta = economy.getBalance(player) - amount;
        if (delta > 0) {
            economy.withdrawPlayer(player, delta);
        } else {
            economy.depositPlayer(player, (-delta));
        }
    }

    public void addBalance(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public void depositBalance(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public void subtractBalance(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public void withdrawBalance(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public boolean has(OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }
}
