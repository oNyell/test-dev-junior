package com.onyell.commands.collections;

import com.onyell.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ListHomeCommand implements CommandExecutor {

    private final Main plugin;

    public ListHomeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        try (Connection connection = plugin.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT home_name FROM homes WHERE uuid = ?")) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    player.sendMessage("§cVocê não tem nenhuma home definida.");
                    return true;
                }

                player.sendMessage("§eSuas homes:");
                do {
                    String homeName = resultSet.getString("home_name");
                    player.sendMessage("§7- " + homeName);
                } while (resultSet.next());

            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage("§cHouve um erro ao listar suas homes.");
        }

        return true;
    }
}
