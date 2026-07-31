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

    public static boolean isEnabled() {
        return economy != null;
    }

    public static double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public static void setBalance(OfflinePlayer player, double amount) {
        double delta = economy.getBalance(player) - amount;
        if (delta > 0) {
            economy.withdrawPlayer(player, delta);
        } else {
            economy.depositPlayer(player, (-delta));
        }
    }

    public static void addBalance(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public static void depositBalance(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public static void subtractBalance(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public static void withdrawBalance(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public static boolean has(OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }
}
