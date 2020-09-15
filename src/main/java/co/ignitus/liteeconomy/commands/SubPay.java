package co.ignitus.liteeconomy.commands;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.data.DataSource;
import co.ignitus.liteeconomy.entities.SubCommand;
import co.ignitus.liteeconomy.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class SubPay implements SubCommand {

    private final LiteEconomy liteEconomy = LiteEconomy.getInstance();

    @Override
    public boolean consoleUse() {
        return false;
    }

    @Override
    public String getName() {
        return "pay";
    }

    @Override
    public String getUsage() {
        return "(player) (amount)";
    }

    @Override
    public String getPermission() {
        return "liteeconomy.pay";
    }

    @Override
    public String getDescription() {
        return "Pay another player.";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(MessageUtil.getMessage("commands.insufficient-arguments",
                    "%command%", "/liteeconomy pay",
                    "%usage%", getUsage()));
            return;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            player.sendMessage(MessageUtil.getMessage("commands.pay.invalid-player"));
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException nfe) {
            player.sendMessage(MessageUtil.getMessage("commands.pay.invalid-number"));
            return;
        }
        if (amount <= 0) {
            player.sendMessage(MessageUtil.getMessage("commands.pay.invalid-number"));
            return;
        }
        final DataSource dataSource = liteEconomy.getDataSource();
        double playerBalance = dataSource.getBalance(player.getUniqueId());
        if (playerBalance < amount) {
            player.sendMessage(MessageUtil.getMessage("commands.pay.insufficient-funds"));
            return;
        }

        boolean playerTrans, targetTrans;
        if (liteEconomy.vaultEnabled()) {
            final Economy economy = liteEconomy.getEconomy();
            playerTrans = economy.withdrawPlayer(player, amount).transactionSuccess();
            targetTrans = economy.depositPlayer(target, amount).transactionSuccess();
        } else {
            playerTrans = dataSource.withdrawBalance(player.getUniqueId(), amount);
            targetTrans = dataSource.depositBalance(target.getUniqueId(), amount);
        }
        if (playerTrans && targetTrans) {
            double newPlayerBalance = dataSource.getBalance(player.getUniqueId());
            player.sendMessage(MessageUtil.getMessage("commands.pay.success",
                    "%player%", player.getName(),
                    "%target%", args[0],
                    "%amount%", MessageUtil.format(amount),
                    "%balance%", MessageUtil.format(newPlayerBalance)));
            if (target.getPlayer() != null)
                target.getPlayer().sendMessage(MessageUtil.getMessage("commands.pay.received",
                        "%player%", player.getName(),
                        "%amount%", MessageUtil.format(amount)));
            return;
        }
        player.sendMessage(MessageUtil.getMessage("commands.pay.error"));
    }
}
