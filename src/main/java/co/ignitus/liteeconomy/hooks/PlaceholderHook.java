package co.ignitus.liteeconomy.hooks;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.util.MessageUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderHook extends PlaceholderExpansion {

    final private LiteEconomy liteEconomy = LiteEconomy.getInstance();

    @Override
    public boolean persist() {
        return false;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return liteEconomy.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "liteeco";
    }

    @Override
    public String getVersion() {
        return liteEconomy.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        return onPlaceholder(player, identifier);
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        return onPlaceholder(player, identifier);
    }

    private String onPlaceholder(OfflinePlayer player, String identifier) {
        if (identifier.equalsIgnoreCase("balance_short")) {
            return String.format("%.2fM", liteEconomy.getDataSource().getBalance(player.getUniqueId()) / 1000000.0);
        }
        if (identifier.equalsIgnoreCase("balance_complete")) {
            return MessageUtil.format(liteEconomy.getDataSource().getBalance(player.getUniqueId()));
        }
        if (identifier.equalsIgnoreCase("balance_fullshort")) {
            return String.format("%.2fK", liteEconomy.getDataSource().getBalance(player.getUniqueId()) / 1000.0);
        }
        if (identifier.equalsIgnoreCase("balance_rounded")) {
            final double balance = liteEconomy.getDataSource().getBalance(player.getUniqueId());
            if (balance >= 1000000000)
                return String.format("%.2fB", balance / 1000000000.0);
            if (balance >= 1000000)
                return String.format("%.2fM", balance / 1000000.0);
            if (balance >= 100000)
                return String.format("%.2fL", balance / 100000.0);
            if (balance >= 1000)
                return String.format("%.2fK", balance / 1000.0);
            return String.valueOf(balance);
        }
        return null;
    }
}
