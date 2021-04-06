package co.ignitus.liteeconomy.hooks;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;

public class EssentialsHook {

    private static boolean enabled = false;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean status) {
        enabled = status;
    }

    public static Essentials getPlugin() {
        if (!enabled)
            return null;
        return (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }
}
