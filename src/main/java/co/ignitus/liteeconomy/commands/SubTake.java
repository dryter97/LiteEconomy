package co.ignitus.liteeconomy.commands;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.entities.SubCommand;
import co.ignitus.liteeconomy.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SubTake implements SubCommand {

    private final LiteEconomy liteEconomy = LiteEconomy.getInstance();

    @Override
    public boolean consoleUse() {
        return true;
    }

    @Override
    public String getName() {
        return "take";
    }

    @Override
    public String getUsage() {
        return "(player) (amount)";
    }

    @Override
    public String getPermission() {
        return "liteeconomy.take";
    }

    @Override
    public String getDescription() {
        return "Reduce a player's balance.";
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
                    "%command%", "/liteeconomy take",
                    "%usage%", getUsage()));
            return;
        }
        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (!player.hasPlayedBefore()) {
            sender.sendMessage(MessageUtil.getMessage("commands.take.invalid-player"));
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException nfe) {
            sender.sendMessage(MessageUtil.getMessage("commands.take.invalid-number"));
            return;
        }
        if (amount <= 0) {
            sender.sendMessage(MessageUtil.getMessage("commands.take.invalid-number"));
            return;
        }
        boolean success;
        if (liteEconomy.vaultEnabled())
            success = liteEconomy.getEconomy().withdrawPlayer(player, amount).transactionSuccess();
        else
            success = liteEconomy.getDataSource().withdrawBalance(player.getUniqueId(), amount);
        if (success) {
            double newBalance = liteEconomy.getDataSource().getBalance(player.getUniqueId());
            sender.sendMessage(MessageUtil.getMessage("commands.take.success",
                    "%player%", args[0],
                    "%amount%", MessageUtil.format(amount),
                    "%balance%", MessageUtil.format(newBalance)));
            return;
        }
        sender.sendMessage(MessageUtil.getMessage("commands.take.error",
                "%player%", args[0]));
    }
}
