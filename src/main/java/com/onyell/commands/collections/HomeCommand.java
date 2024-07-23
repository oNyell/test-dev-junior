package com.onyell.commands.collections;

import com.onyell.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HomeCommand implements CommandExecutor {

    private final Main plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public HomeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cVocê deve fornecer o nome da home.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        String homeName = args[0];
        long currentTime = System.currentTimeMillis();
        long cooldown = plugin.getConfig().getLong("home.cooldown");

        if (isCooldownActive(playerId, currentTime, cooldown)) {
            long remainingTime = getRemainingCooldownTime(playerId, currentTime, cooldown);
            player.sendMessage("§cVocê precisa esperar mais " + remainingTime + " segundos para usar o /home novamente.");
            return true;
        }

        Location homeLocation = getHomeLocation(playerId, homeName);
        if (homeLocation == null) {
            player.sendMessage("§cVocê não tem uma home definida com esse nome.");
            return true;
        }

        teleportPlayer(player, homeLocation);
        cooldowns.put(playerId, currentTime);
        return true;
    }

    private boolean isCooldownActive(UUID playerId, long currentTime, long cooldown) {
        return cooldowns.containsKey(playerId) && (currentTime - cooldowns.get(playerId)) < TimeUnit.SECONDS.toMillis(cooldown);
    }

    private long getRemainingCooldownTime(UUID playerId, long currentTime, long cooldown) {
        return TimeUnit.MILLISECONDS.toSeconds((cooldowns.get(playerId) + TimeUnit.SECONDS.toMillis(cooldown)) - currentTime);
    }

    private Location getHomeLocation(UUID playerId, String homeName) {
        try (Connection connection = plugin.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT x, y, z, world FROM homes WHERE uuid = ? AND home_name = ?")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, homeName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double x = resultSet.getDouble("x");
                    double y = resultSet.getDouble("y");
                    double z = resultSet.getDouble("z");
                    String world = resultSet.getString("world");
                    return new Location(Bukkit.getWorld(world), x, y, z);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void teleportPlayer(Player player, Location location) {
        player.sendMessage("§eTeleportando para sua home...");
        boolean teleportParticles = plugin.getConfig().getBoolean("home.teleport-particles");
        Particle particle = getParticle();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.teleport(location);
            if (teleportParticles) {
                playParticleEffect(location, particle);
            }
            player.sendMessage("§aVocê foi teletransportado para sua home.");
        }, 20L);
    }

    private Particle getParticle() {
        String particleName = plugin.getConfig().getString("home.particle-name", "PORTAL").toUpperCase();
        try {
            return Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            return Particle.PORTAL;
        }
    }

    private void playParticleEffect(Location location, Particle particle) {
        String particleType = plugin.getConfig().getString("home.particle-type", "CIRCLE").toUpperCase();
        switch (particleType) {
            case "CIRCLE":
                drawCircle(location, particle);
                break;
            case "SPIRAL":
                drawSpiral(location, particle);
                break;
            default:
                break;
        }
    }

    private void drawCircle(Location location, Particle particle) {
        double radius = 1.5;
        int points = 50;
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Objects.requireNonNull(location.getWorld()).spawnParticle(particle, location.clone().add(x, 0, z), 1, 0, 0, 0, 0);
        }
    }

    private void drawSpiral(Location location, Particle particle) {
        double radius = 1.3;
        double heightStep = 0.1;
        double maxHeight = 3;
        int points = 150;

        new BukkitRunnable() {
            private double currentHeight = 0;
            private int count = 0;

            @Override
            public void run() {
                if (count >= points || currentHeight >= maxHeight) {
                    cancel();
                    return;
                }

                double angle = 2 * Math.PI * (count / (double) points * 10);
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);
                Objects.requireNonNull(location.getWorld()).spawnParticle(particle, location.clone().add(x, currentHeight, z), 1, 0, 0, 0, 0);

                currentHeight += heightStep;
                count++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
