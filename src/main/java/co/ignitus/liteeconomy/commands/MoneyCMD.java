package co.ignitus.liteeconomy.commands;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.entities.Command;
import co.ignitus.liteeconomy.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCMD extends Command {

    private static final LiteEconomy liteEconomy = LiteEconomy.getInstance();

    public MoneyCMD(LiteEconomy liteEconomy) {
        super(liteEconomy, "money");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!sender.hasPermission("liteeconomy.money")) {
            sender.sendMessage(MessageUtil.getMessage("commands.no-permission"));
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtil.getMessage("commands.no-console"));
                return true;
            }
            final Player player = (Player) sender;
            final double balance = liteEconomy.getDataSource().getBalance(player.getUniqueId());
            player.sendMessage(MessageUtil.getMessage("commands.money.message",
                    "%balance%", MessageUtil.format(balance)));
            return true;
        }
        if (!sender.hasPermission("liteeconomy.money.others")) {
            sender.sendMessage(MessageUtil.getMessage("commands.no-permission"));
            return true;
        }
        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.getMessage("commands.money.invalid-player"));
            return true;
        }
        final double balance = liteEconomy.getDataSource().getBalance(target.getUniqueId());
        sender.sendMessage(MessageUtil.getMessage("commands.money.others-message",
                "%player%", target.getName(),
                "%balance%", MessageUtil.format(balance)));
        return true;
    }

}
