package co.ignitus.liteeconomy.commands;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.entities.Command;
import co.ignitus.liteeconomy.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCMD extends Command {

    private static LiteEconomy liteEconomy = LiteEconomy.getInstance();

    public MoneyCMD(LiteEconomy liteEconomy) {
        super(liteEconomy, "money");
    }

    @Override
    protected void onUse(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.getMessage("commands.no-console"));
            return;
        }
        final Player player = (Player) sender;
        if (!player.hasPermission("liteeconomy.money")) {
            sender.sendMessage(MessageUtil.getMessage("commands.no-permission"));
            return;
        }
        final double balance = liteEconomy.getDataSource().getBalance(player.getUniqueId());
        player.sendMessage(MessageUtil.getMessage("commands.money.message",
                "%balance%", MessageUtil.format(balance)));
    }
}
