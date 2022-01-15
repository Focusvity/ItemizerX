package me.focusvity.itemizerx;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CoreProtectBridge {

    private static CoreProtect cp = null;
    private static CoreProtectAPI api = null;
    private static boolean logged = false;

    public static CoreProtect getCoreProtect() {
        try {
            final Plugin pl = Bukkit.getPluginManager().getPlugin("CoreProtect");
            if (pl instanceof CoreProtect) {
                cp = (CoreProtect) pl;
            } else {
                if (!logged) { // To stop the console log spam - not persistent on server restarts
                    Bukkit.getLogger().info("CoreProtect not detected, some features will not be logged!");
                    logged = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cp;
    }

    public static CoreProtectAPI getAPI() {
        final CoreProtect cp = getCoreProtect();

        if (cp != null && api == null) {
            try {
                api = cp.getAPI();
                if (!cp.isEnabled() || !api.isEnabled()) {
                    Bukkit.getLogger().info("CoreProtect is disabled, some features will not be logged!");
                    return null;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return api;
    }
}
