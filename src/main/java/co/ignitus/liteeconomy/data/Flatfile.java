package co.ignitus.liteeconomy.data;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.files.FileManager;
import co.ignitus.liteeconomy.util.MessageUtil;
import org.bukkit.Bukkit;

import java.util.UUID;

import static co.ignitus.liteeconomy.util.MessageUtil.trimDouble;

public class Flatfile extends FileManager implements DataSource {

    private final LiteEconomy liteEconomy = LiteEconomy.getInstance();

    public Flatfile() {
        super("data.yml");
    }

    public boolean connect() {
        this.reloadConfig();
        Bukkit.getConsoleSender().sendMessage(MessageUtil.format("&2[LiteEconomy] Currently using flatfile storage. (data.yml)."));
        return true;
    }

    @Override
    public void disconnect() {
        this.saveFileConfiguration();
    }

    @Override
    public boolean hasBalance(UUID uuid) {
        return getFileConfiguration().contains("balances." + uuid.toString());
    }

    @Override
    public double getBalance(UUID uuid) {
        return getFileConfiguration().getDouble("balances." + uuid.toString(),
                liteEconomy.getConfig().getDouble("balance.default", 0));
    }

    @Override
    public boolean setBalance(UUID uuid, double balance) {
        getFileConfiguration().set("balances." + uuid.toString(), trimDouble(balance));
        saveFileConfiguration();
        return true;
    }

    @Override
    public boolean withdrawBalance(UUID uuid, double amount) {
        return setBalance(uuid, getBalance(uuid) - amount);
    }

    @Override
    public boolean depositBalance(UUID uuid, double amount) {
        return setBalance(uuid, getBalance(uuid) + amount);
    }
}
