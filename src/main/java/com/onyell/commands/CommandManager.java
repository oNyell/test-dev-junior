package com.onyell.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private final Map<String, CommandExecutor> commands = new HashMap<>();

    public void registerCommand(String name, CommandExecutor executor) {
        commands.put(name, executor);
    }

    public void setupCommands(JavaPlugin plugin) {
        CommandMap commandMap = getCommandMap(plugin);

        for (Map.Entry<String, CommandExecutor> entry : commands.entrySet()) {
            PluginCommand command = plugin.getCommand(entry.getKey());
            if (command == null) {
                command = createPluginCommand(entry.getKey(), plugin);
                assert commandMap != null;
                assert command != null;
                commandMap.register(plugin.getDescription().getName(), command);
            }
            command.setExecutor(entry.getValue());
        }
    }

    private CommandMap getCommandMap(JavaPlugin plugin) {
        try {
            if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                return (CommandMap) field.get(plugin.getServer().getPluginManager());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PluginCommand createPluginCommand(String name, JavaPlugin plugin) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, JavaPlugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
