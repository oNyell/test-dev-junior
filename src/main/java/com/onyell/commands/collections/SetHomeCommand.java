package com.onyell.commands.collections;

import com.onyell.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SetHomeCommand implements CommandExecutor {

    private final Main plugin;

    public SetHomeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cVocê deve fornecer um nome para sua home.");
            return true;
        }

        String homeName = args[0];
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        String world = player.getWorld().getName();

        try (Connection connection = plugin.getConnection();
             PreparedStatement statement = connection.prepareStatement("REPLACE INTO homes (uuid, home_name, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, homeName);
            statement.setDouble(3, x);
            statement.setDouble(4, y);
            statement.setDouble(5, z);
            statement.setString(6, world);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage("§cHouve um erro ao salvar sua home.");
            return true;
        }

        player.sendMessage("§aHome '" + homeName + "' definida com sucesso!");
        return true;
    }
}
