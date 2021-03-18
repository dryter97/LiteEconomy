package co.ignitus.liteeconomy.commands;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.data.DataSource;
import co.ignitus.liteeconomy.entities.SubCommand;
import co.ignitus.liteeconomy.hooks.EssentialsHook;
import co.ignitus.liteeconomy.util.MessageUtil;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubConvert implements SubCommand {

    private final LiteEconomy liteEconomy = LiteEconomy.getInstance();

    @Override
    public boolean consoleUse() {
        return true;
    }

    @Override
    public String getName() {
        return "convert";
    }

    @Override
    public String getUsage() {
        return "(thebaseeconomyplugin) (theneweconomyplugin)";
    }

    @Override
    public String getPermission() {
        return "liteeconomy.convert";
    }

    @Override
    public String getDescription() {
        return "Convert balances to another economy plugin";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    private void onCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(MessageUtil.getMessage("commands.insufficient-arguments",
                    "%command%", "/liteeconomy convert",
                    "%usage%", getUsage()));
            return;
        }
        final String oldPlugin = args[0];
        final String newPlugin = args[1];
        if (oldPlugin.equalsIgnoreCase(newPlugin)) {
            sender.sendMessage(MessageUtil.getMessage("commands.convert.same-plugin"));
            return;
        }
        if (!oldPlugin.equalsIgnoreCase("essentials") && !oldPlugin.equalsIgnoreCase("liteeconomy")) {
            sender.sendMessage(MessageUtil.getMessage("commands.convert.unknown-plugin",
                    "%plugin%", oldPlugin));
            return;
        }

        if (!newPlugin.equalsIgnoreCase("essentials") && !newPlugin.equalsIgnoreCase("liteeconomy")) {
            sender.sendMessage(MessageUtil.getMessage("commands.convert.unknown-plugin",
                    "%plugin%", newPlugin));
            return;
        }
        if (!EssentialsHook.isEnabled()) {
            sender.sendMessage(MessageUtil.getMessage("commands.convert.missing-plugin",
                    "%plugin%", newPlugin));
            return;
        }
        // For now, I'm going to assume Essentials will be the only supported plugin.
        // As a result, I'm not checking what the old plugin is.

        final DataSource dataSource = liteEconomy.getDataSource();

        // Export to Essentials
        if (newPlugin.equalsIgnoreCase("essentials")) {
            try {
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    if (Economy.playerExists(player.getUniqueId())) Economy.resetBalance(player.getUniqueId());
                }
                for (Map.Entry<UUID, Double> entry : dataSource.getBalances().entrySet()) {
                    Economy.setMoney(entry.getKey(), BigDecimal.valueOf(entry.getValue()));
                }
            } catch (NoLoanPermittedException | UserDoesNotExistException ignored) {
                sender.sendMessage(MessageUtil.getMessage("commands.convert.error",
                        "%old_plugin%", oldPlugin,
                        "%new_plugin%", newPlugin
                ));
                return;
            }
            sender.sendMessage(MessageUtil.getMessage("commands.convert.success",
                    "%old_plugin%", oldPlugin,
                    "%new_plugin%", newPlugin
            ));
            return;
        }

        // Import from Essentials
        if (newPlugin.equalsIgnoreCase("liteeconomy")) {
            if (!dataSource.clearBalances()) {
                sender.sendMessage(MessageUtil.getMessage("commands.convert.error",
                        "%old_plugin%", oldPlugin,
                        "%new_plugin%", newPlugin
                ));
                return;
            }
            final HashMap<UUID, Double> balances = new HashMap<>();
            try {
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    final UUID uuid = player.getUniqueId();
                    if (Economy.playerExists(uuid))
                        balances.put(uuid, Economy.getMoneyExact(uuid).doubleValue());
                }
            } catch (UserDoesNotExistException ignored) {
                sender.sendMessage(MessageUtil.getMessage("commands.convert.error",
                        "%old_plugin%", oldPlugin,
                        "%new_plugin%", newPlugin
                ));
                return;
            }
            if (dataSource.setBalances(balances))
                sender.sendMessage(MessageUtil.getMessage("commands.convert.success",
                        "%old_plugin%", oldPlugin,
                        "%new_plugin%", newPlugin
                ));
            else
                sender.sendMessage(MessageUtil.getMessage("commands.convert.error",
                        "%old_plugin%", oldPlugin,
                        "%new_plugin%", newPlugin
                ));
            return;
        }
        sender.sendMessage(MessageUtil.getMessage("commands.convert.unknown-plugin",
                "%plugin%", newPlugin));
    }
}
