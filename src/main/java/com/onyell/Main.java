package com.onyell;

import com.onyell.commands.CommandManager;
import com.onyell.commands.ReloadCommand;
import com.onyell.listener.WindChargeListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new WindChargeListener(this), this);
        CommandManager commandManager = new CommandManager();
        commandManager.registerCommand("reload", new ReloadCommand(this));
        commandManager.setupCommands(this);

        getLogger().log(Level.FINE, "Plugin ativo com sucesso.");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.SEVERE, "§cPlugin desativado com sucesso.");
    }

    public static Main getInstance() {
        return instance;
    }
}
