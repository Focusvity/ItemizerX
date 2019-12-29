package me.focusvity.itemizerx;

import org.bukkit.plugin.java.JavaPlugin;

public class ItemizerX extends JavaPlugin
{

    public static ItemizerX plugin;

    @Override
    public void onLoad()
    {
        this.plugin = this;
    }

    @Override
    public void onEnable()
    {
        this.plugin = this;
        getCommand("itemizer").setExecutor(new ItemizerXCommand());
    }

    @Override
    public void onDisable()
    {
        this.plugin = null;
    }
}
