package co.ignitus.liteeconomy.commands;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.entities.SubCommand;
import co.ignitus.liteeconomy.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SubSet implements SubCommand {

    private final LiteEconomy liteEconomy = LiteEconomy.getInstance();

    @Override
    public boolean consoleUse() {
        return true;
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getUsage() {
        return "(player) (amount)";
    }

    @Override
    public String getPermission() {
        return "liteeconomy.set";
    }

    @Override
    public String getDescription() {
        return "Set a player's balance.";
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
                    "%command%", "/liteeconomy set",
                    "%usage%", getUsage()));
            return;
        }
        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (!player.hasPlayedBefore()) {
            sender.sendMessage(MessageUtil.getMessage("commands.set.invalid-player"));
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException nfe) {
            sender.sendMessage(MessageUtil.getMessage("commands.set.invalid-number"));
            return;
        }
        boolean success;
        if (liteEconomy.vaultEnabled()) {
            final Economy economy = liteEconomy.getEconomy();
            final double currentBalance = economy.getBalance(player);
            if (currentBalance > amount)
                success = economy.withdrawPlayer(player, currentBalance - amount).transactionSuccess();
            else
                success = economy.depositPlayer(player, currentBalance - amount).transactionSuccess();
        } else
            success = liteEconomy.getDataSource().setBalance(player.getUniqueId(), amount);

        if (success) {
            sender.sendMessage(MessageUtil.getMessage("commands.set.success",
                    "%player%", args[0],
                    "%balance%", MessageUtil.format(amount)));
            return;
        }
        sender.sendMessage(MessageUtil.getMessage("commands.set.error",
                "%player%", args[0]));
    }
}
