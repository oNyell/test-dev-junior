package com.onyell;

import com.onyell.commands.CommandManager;
import com.onyell.commands.collections.*;
import com.onyell.listener.WindChargeListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    private static Main instance;
    private Connection connection;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupDatabase();

        getServer().getPluginManager().registerEvents(new WindChargeListener(this), this);
        CommandManager commandManager = new CommandManager();
        commandManager.registerCommand("reloadconfig", new ReloadCommand(this));
        commandManager.registerCommand("home", new HomeCommand(this));
        commandManager.registerCommand("sethome", new SetHomeCommand(this));
        commandManager.registerCommand("homelist", new ListHomeCommand(this));
        commandManager.registerCommand("homeremove", new RemoveHomeCommand(this));
        commandManager.setupCommands(this);

        getLogger().log(Level.FINE, "Plugin ativo com sucesso.");
    }


    @Override
    public void onDisable() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        getLogger().log(Level.SEVERE, "Plugin desativado com sucesso.");
    }

    public static Main getInstance() {
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String host = getConfig().getString("home.database.host");
            int port = getConfig().getInt("home.database.port");
            String database = getConfig().getString("home.database.database");
            String username = getConfig().getString("home.database.username");
            String password = getConfig().getString("home.database.password");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    private void setupDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS homes (" +
                    "uuid VARCHAR(36) NOT NULL," +
                    "home_name VARCHAR(255) NOT NULL," +
                    "x DOUBLE NOT NULL," +
                    "y DOUBLE NOT NULL," +
                    "z DOUBLE NOT NULL," +
                    "world VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY (uuid, home_name))");
            getLogger().log(Level.INFO, "Database setup completed.");
        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().log(Level.SEVERE, "Error setting up the database.");
        }
    }
}
