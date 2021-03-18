package co.ignitus.liteeconomy;

import co.ignitus.liteeconomy.commands.LiteEcoCMD;
import co.ignitus.liteeconomy.commands.MoneyCMD;
import co.ignitus.liteeconomy.data.DataSource;
import co.ignitus.liteeconomy.data.Flatfile;
import co.ignitus.liteeconomy.data.MySQL;
import co.ignitus.liteeconomy.events.PlayerEvents;
import co.ignitus.liteeconomy.files.MessagesFile;
import co.ignitus.liteeconomy.hooks.EssentialsHook;
import co.ignitus.liteeconomy.hooks.PlaceholderHook;
import co.ignitus.liteeconomy.hooks.VaultHook;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LiteEconomy extends JavaPlugin {

    private CommandSender cs = Bukkit.getConsoleSender();

    @Getter
    private static LiteEconomy instance;

    private Economy economy;

    private DataSource dataSource;

    private MessagesFile messagesFile;

    @Override
    public void onEnable() {
        instance = this;
        cs.sendMessage(ChatColor.GREEN + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
        cs.sendMessage(ChatColor.GREEN + "  Enabling LiteEconomy");
        cs.sendMessage(ChatColor.GREEN + " Developed by Ignitus Co.");
        cs.sendMessage(ChatColor.GREEN + ChatColor.STRIKETHROUGH.toString() + "---------------------------");

        saveDefaultConfig();
        messagesFile = new MessagesFile();
        if (getConfig().getBoolean("mysql.enabled"))
            dataSource = new MySQL();
        else
            dataSource = new Flatfile();

        if (!dataSource.connect()) {
            getPluginLoader().disablePlugin(this);
            return;
        }

        if (vaultEnabled()) {
            if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                cs.sendMessage(ChatColor.RED + "[LiteEconomy] Vault not found - Disabling Plugin");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            cs.sendMessage(ChatColor.GREEN + "[LiteEconomy] VaultAPI Found. Enabling vault hook.");
            getServer().getServicesManager().register(Economy.class, new VaultHook(this), this, ServicePriority.High);
            if (setupEconomy())
                cs.sendMessage(ChatColor.GREEN + "[LiteEconomy] VaultAPI hook enabled.");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderHook().register();
            cs.sendMessage(ChatColor.GREEN + "[LiteEconomy] PlaceholderAPI Found. Enabling placeholders.");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            EssentialsHook.setEnabled(true);
            cs.sendMessage(ChatColor.GREEN + "[LiteEconomy] Essentials Found. Enabling essentials capabilities.");
        }

        new LiteEcoCMD(this);
        new MoneyCMD(this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
    }

    @Override
    public void onDisable() {
        cs.sendMessage(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
        cs.sendMessage(ChatColor.RED + "   Disabling LiteEconomy");
        cs.sendMessage(ChatColor.RED + "  Developed by Ignitus Co.");
        cs.sendMessage(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + "---------------------------");
        getDataSource().disconnect();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }

    public boolean vaultEnabled() {
        return getConfig().getBoolean("vault");
    }
}
