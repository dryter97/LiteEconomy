package co.ignitus.liteeconomy.entities;

import co.ignitus.liteeconomy.util.MessageUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public interface SubCommand {

    boolean consoleUse();

    String getName();

    String getUsage();

    String getPermission();

    String getDescription();

    String[] getAliases();

    void onCommandByPlayer(Player player, String[] args);

    default void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        if (!consoleUse()) sender.sendMessage(MessageUtil.getMessage("commands.no-console"));
    }
}