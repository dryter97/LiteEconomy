package co.ignitus.liteeconomy.data;

import java.util.UUID;

public interface DataSource {

    boolean connect();

    void disconnect();

    boolean hasBalance(UUID uuid);

    double getBalance(UUID uuid);

    boolean withdrawBalance(UUID uuid, double amount);

    boolean depositBalance(UUID uuid, double amount);

    boolean setBalance(UUID uuid, double balance);


}
