package co.ignitus.liteeconomy.hooks;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.data.DataSource;
import co.ignitus.liteeconomy.util.MessageUtil;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class VaultHook extends AbstractEconomy {

    private LiteEconomy liteEconomy;
    private DataSource dataSource;

    public VaultHook(LiteEconomy liteEconomy) {
        this.liteEconomy = liteEconomy;
        this.dataSource = liteEconomy.getDataSource();
    }

    @Override
    public boolean isEnabled() {
        return liteEconomy.isEnabled();
    }

    @Override
    public String getName() {
        return "LiteEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return MessageUtil.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return hasAccount(playerName, null);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return dataSource.hasBalance(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    @Override
    public double getBalance(String playerName) {
        return getBalance(playerName, null);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return dataSource.getBalance(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    @Override
    public boolean has(String playerName, double amount) {
        return has(playerName, null, amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(playerName, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        if (dataSource.withdrawBalance(Bukkit.getOfflinePlayer(playerName).getUniqueId(), amount))
            return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "");
        else
            return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "LiteEconomy - An error occurred while trying to store data.");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(playerName, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        if (dataSource.depositBalance(Bukkit.getOfflinePlayer(playerName).getUniqueId(), amount))
            return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "");
        else
            return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "LiteEconomy - An error occurred while trying to store data.");
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return createPlayerAccount(playerName, null);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return dataSource.setBalance(Bukkit.getOfflinePlayer(playerName).getUniqueId(), liteEconomy.getConfig().getDouble("balance.default"));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return hasAccount(player, null);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return dataSource.hasBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getBalance(player, null);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return dataSource.getBalance(player.getUniqueId());
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return has(player, null, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawPlayer(player, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        if (dataSource.withdrawBalance(player.getUniqueId(), amount))
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "");
        else
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.FAILURE, "LiteEconomy - An error occurred while trying to store data.");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositPlayer(player, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        if (dataSource.depositBalance(player.getUniqueId(), amount))
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "");
        else
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.FAILURE, "LiteEconomy - An error occurred while trying to store data.");

    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "LiteEconomy does not support bank accounts!");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return createPlayerAccount(player, null);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return dataSource.setBalance(player.getUniqueId(), liteEconomy.getConfig().getDouble("balance.default"));
    }
}
