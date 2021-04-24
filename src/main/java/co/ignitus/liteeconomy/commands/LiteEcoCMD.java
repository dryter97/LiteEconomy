package co.ignitus.liteeconomy.commands;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.entities.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LiteEcoCMD extends Command {

    public LiteEcoCMD(LiteEconomy liteEconomy) {
        super(liteEconomy, "liteeconomy");
        this
                .registerSubCommand(new SubBaltop())
                .registerSubCommand(new SubConvert())
                .registerSubCommand(new SubGive())
                .registerSubCommand(new SubPay())
                .registerSubCommand(new SubSet())
                .registerSubCommand(new SubTake())
        ;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 1)
            return super.onTabComplete(sender, cmd, label, args);
        if (args.length == 2)
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        return new ArrayList<>();
    }
}
