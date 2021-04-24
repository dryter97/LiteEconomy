package co.ignitus.liteeconomy.commands;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.entities.SubCommand;
import co.ignitus.liteeconomy.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class SubBaltop implements SubCommand {

    private final LiteEconomy liteEconomy = LiteEconomy.getInstance();

    @Override
    public boolean consoleUse() {
        return true;
    }

    @Override
    public String getName() {
        return "baltop";
    }

    @Override
    public String getUsage() {
        return "[page]";
    }

    @Override
    public String getPermission() {
        return "liteeconomy.baltop";
    }

    @Override
    public String getDescription() {
        return "Show the richest players on the server";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ecolist"};
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
        final HashMap<UUID, Double> balances = liteEconomy.getDataSource().getBalances();
        final LinkedHashMap<UUID, Double> sortedBalances = balances.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        int page = 1;
        if (args.length != 0) {
            try {
                page = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException ignored) {
            }
        }

        int start = Math.max(0, (page - 1) * 10);
        int finish = Math.min(sortedBalances.size(), (page * 10) - 1);
        final String formatMessage = MessageUtil.getMessage("commands.baltop.balance-format");
        final String message = MessageUtil.getListMessage("commands.baltop.message");
        final String balanceMessage = new ArrayList<>(sortedBalances.keySet()).subList(start, finish).stream()
                .map(key -> {
                    final OfflinePlayer player = Bukkit.getOfflinePlayer(key);
                    String name = player.getName();
                    if (name == null)
                        name = key.toString();
                    return formatMessage.replace("%player%", name).replace("%balance%", MessageUtil.format(sortedBalances.get(key)));
                }).collect(Collectors.joining("\n"));
        sender.sendMessage(message.replace("%balances%", balanceMessage));
    }
}
