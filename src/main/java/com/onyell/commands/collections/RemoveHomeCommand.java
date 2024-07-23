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

public class RemoveHomeCommand implements CommandExecutor {

    private final Main plugin;

    public RemoveHomeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cVocê deve fornecer o nome da home que deseja remover.");
            return true;
        }

        String homeName = args[0];
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        try (Connection connection = plugin.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM homes WHERE uuid = ? AND home_name = ?")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, homeName);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                player.sendMessage("§aHome '" + homeName + "' removida com sucesso!");
            } else {
                player.sendMessage("§cVocê não tem uma home definida com esse nome.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage("§cHouve um erro ao remover sua home.");
        }

        return true;
    }
}
