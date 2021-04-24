package co.ignitus.liteeconomy.util;

import co.ignitus.liteeconomy.LiteEconomy;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class MessageUtil {

    private static LiteEconomy liteEconomy = LiteEconomy.getInstance();
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String format(String input, String... strings) {
        String message = format(input);
        for (int i = 0; i < strings.length; i += 2)
            message = message.replace(strings[i], strings[i + 1]);
        return message;
    }

    public static String format(double amount) {
        return decimalFormat.format(amount);
    }

    public static double trimDouble(double balance) {
        if (!liteEconomy.getConfig().getBoolean("balance.negative"))
            balance = Math.max(0, balance);
        balance = Math.round(balance * 100.0) / 100.0;
        return balance;
    }

    public static List<String> format(List<String> input, String... replace) {
        return input.stream().map(line -> format(line, replace)).collect(Collectors.toList());
    }

    public static String getMessage(String path, String... replace) {
        return format(LiteEconomy.getInstance().getMessagesFile().getFileConfiguration().getString(path, "&cUnknown Message"), replace);
    }

    public static String getListMessage(String path, String... replace) {
        final List<String> messages = LiteEconomy.getInstance().getMessagesFile().getFileConfiguration().getStringList(path);
        return messages.stream().map(message -> format(message, replace)).collect(Collectors.joining("\n"));
    }


}
