package me.focusvity.itemizerx;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CoreProtectBridge
{

    private static CoreProtect cp = null;
    private static CoreProtectAPI api = null;

    public static CoreProtect getCoreProtect()
    {
        try
        {
            final Plugin pl = Bukkit.getPluginManager().getPlugin("CoreProtect");
            if (pl != null && pl instanceof CoreProtect)
            {
                cp = (CoreProtect) pl;
            }
            else
            {
                Bukkit.getLogger().info("CoreProtect not detected, expect the plugin to not function properly!");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return cp;
    }

    public static CoreProtectAPI getAPI()
    {
        if (api == null)
        {
            try
            {
                final CoreProtect cp = getCoreProtect();
                api = cp.getAPI();
                if (!cp.isEnabled() || !api.isEnabled())
                {
                    Bukkit.getLogger().info("CoreProtect is disabled, expect the plugin to not function properly!");
                    return null;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return api;
    }
}
