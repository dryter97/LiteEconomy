package co.ignitus.liteeconomy.events;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.data.DataSource;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    private final LiteEconomy liteEconomy = LiteEconomy.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (!liteEconomy.vaultEnabled())
            return;
        final DataSource dataSource = liteEconomy.getDataSource();
        final Economy economy = liteEconomy.getEconomy();
        if (!dataSource.hasBalance(player.getUniqueId())) {
            dataSource.setBalance(player.getUniqueId(), economy.getBalance(player));
            return;
        }
        final double savedBalance = dataSource.getBalance(player.getUniqueId());
        final double currentBalance = economy.getBalance(player);
        if (currentBalance > savedBalance)
            economy.withdrawPlayer(player, currentBalance - savedBalance);
        else
            economy.depositPlayer(player, currentBalance - savedBalance);
    }
}
