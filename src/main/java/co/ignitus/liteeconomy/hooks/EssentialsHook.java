package co.ignitus.liteeconomy.hooks;

public class EssentialsHook {

    private static boolean enabled = false;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean status) {
        enabled = status;
    }
}
