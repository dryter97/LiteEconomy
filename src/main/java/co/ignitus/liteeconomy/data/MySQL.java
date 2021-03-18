package co.ignitus.liteeconomy.data;

import co.ignitus.liteeconomy.LiteEconomy;
import co.ignitus.liteeconomy.util.MessageUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static co.ignitus.liteeconomy.util.MessageUtil.trimDouble;

public class MySQL implements DataSource {

    final LiteEconomy liteEconomy = LiteEconomy.getInstance();

    @Getter
    final private HikariDataSource dataSource;

    public MySQL() {
        this.dataSource = setupDataSource();
    }

    private HikariDataSource setupDataSource() {
        final FileConfiguration config = liteEconomy.getConfig();
        final HikariDataSource dataSource = new HikariDataSource();
        final String host = config.getString("mysql.host");
        final int port = config.getInt("mysql.port");
        final String database = config.getString("mysql.database");
        dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dataSource.setUsername(config.getString("mysql.username"));
        dataSource.setPassword(config.getString("mysql.password"));
        dataSource.addDataSourceProperty("autoReconnect", "true");
        dataSource.addDataSourceProperty("autoReconnectForPools", "true");
        dataSource.addDataSourceProperty("interactiveClient", "true");
        dataSource.addDataSourceProperty("characterEncoding", "UTF-8");
        dataSource.setAutoCommit(true);
        return dataSource;
    }

    public boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(MessageUtil.format("&4[LiteEconomy] Unable to connect to database. Disabling plugin..."));
            return false;
        }
        try (Connection connection = getDataSource().getConnection()) {
            connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS lite_economy(" +
                            "`uuid` VARCHAR(255) UNIQUE NOT NULL," +
                            "`balance` DOUBLE DEFAULT NULL," +
                            "PRIMARY KEY (`uuid`))"
            ).execute();
            Bukkit.getConsoleSender().sendMessage(MessageUtil.format("&2[LiteEconomy] Successfully established a connection with the database."));
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(MessageUtil.format("&4[LiteEconomy] Unable to connect to database. Disabling plugin..."));
            return false;
        }
    }

    @Override
    public void disconnect() {
        getDataSource().close();
    }

    @Override
    public boolean hasBalance(UUID uuid) {
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `lite_economy` WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException ignored) {
        }
        return false;
    }

    @Override
    public double getBalance(UUID uuid) {
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `lite_economy` WHERE `uuid` = ?");
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return resultSet.getDouble("balance");
        } catch (SQLException ignored) {
        }
        return liteEconomy.getConfig().getDouble("balance.default", 0);
    }

    @Override
    public HashMap<UUID, Double> getBalances() {
        final HashMap<UUID, Double> balances = new HashMap<>();
        try (Connection connection = getDataSource().getConnection()) {
            final PreparedStatement statement = connection.prepareStatement("SELECT * FROM `lite_economy`");
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                final double balance = resultSet.getDouble("balance");
                balances.put(uuid, balance);
            }
        } catch (SQLException ignored) {
        }
        return balances;
    }

    @Override
    public boolean setBalance(UUID uuid, double balance) {
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `lite_economy`(`uuid`, `balance`) VALUES(?, ?)" +
                            "ON DUPLICATE KEY UPDATE " +
                            "`balance` = ?"
            );
            balance = trimDouble(balance);
            statement.setString(1, uuid.toString());
            statement.setDouble(2, balance);
            statement.setDouble(3, balance);
            statement.executeUpdate();
            return true;
        } catch (SQLException ignored) {
            return false;
        }
    }

    @Override
    public boolean setBalances(HashMap<UUID, Double> balances) {
        if (balances.isEmpty())
            return true;
        if (!clearBalances())
            return false;
        try (Connection connection = getDataSource().getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `lite_economy`(`uuid`, `balance`) VALUES(?, ?)" +
                            "ON DUPLICATE KEY UPDATE " +
                            "`balance` = ?"
            );
            for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
                final double balance = trimDouble(entry.getValue());
                final UUID uuid = entry.getKey();
                statement.setString(1, uuid.toString());
                statement.setDouble(2, balance);
                statement.setDouble(3, balance);
                statement.addBatch();
            }
            statement.executeBatch();
            return true;
        } catch (SQLException ignored) {
            return false;
        }
    }

    @Override
    public boolean withdrawBalance(UUID uuid, double amount) {
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `lite_economy`(`uuid`, `balance`) VALUES(?, ?)" +
                            "ON DUPLICATE KEY UPDATE " +
                            "`balance` = `balance` - ?"
            );
            amount = trimDouble(amount);
            statement.setString(1, uuid.toString());
            statement.setDouble(2, -amount);
            statement.setDouble(3, amount);
            statement.executeUpdate();
            return true;
        } catch (SQLException ignored) {
            return false;
        }
    }

    @Override
    public boolean depositBalance(UUID uuid, double amount) {
        try (Connection connection = getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `lite_economy`(`uuid`, `balance`) VALUES(?, ?)" +
                            "ON DUPLICATE KEY UPDATE " +
                            "`balance` = `balance` + ?"
            );
            amount = trimDouble(amount);
            statement.setString(1, uuid.toString());
            statement.setDouble(2, amount);
            statement.setDouble(3, amount);
            statement.executeUpdate();
            return true;
        } catch (SQLException ignored) {
            return false;
        }
    }

    @Override
    public boolean clearBalances() {
        try (Connection connection = getDataSource().getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(
                    "TRUNCATE `lite_economy`"
            );
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
}
