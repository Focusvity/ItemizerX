package me.focusvity.itemizerx;

import org.bukkit.plugin.java.JavaPlugin;

public class ItemizerX extends JavaPlugin
{

    public static ItemizerX plugin;

    @Override
    public void onLoad()
    {
        plugin = this;
    }

    @Override
    public void onEnable()
    {
        plugin = this;
        CoreProtectBridge.getCoreProtect();
        getCommand("itemizer").setExecutor(new ItemizerXCommand());
        getCommand("itemizer").setTabCompleter(new ItemizerXTab());
    }

    @Override
    public void onDisable()
    {
        plugin = null;
    }
}
