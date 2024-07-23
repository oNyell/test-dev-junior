package com.onyell.listener;

import com.onyell.Main;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WindChargeListener implements Listener {

    private final Main plugin;

    public WindChargeListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUseWindCharge(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getInventory().getItemInMainHand().getType() == Material.WIND_CHARGE) {
            event.setCancelled(true);

            double projectileSpeed = plugin.getConfig().getDouble("windCharge.projectileSpeed", 1.0);

            // WIND_CHARGE ainda está em testes
            WindCharge windCharge = (WindCharge) player.getWorld().spawnEntity(player.getLocation(), EntityType.WIND_CHARGE);
            windCharge.setIsIncendiary(false);
            Vector direction = player.getLocation().getDirection().multiply(projectileSpeed);
            windCharge.setVelocity(direction);

            // Adiciona as partículas seguindo o WindCharge
            addTrailingParticles(windCharge);
        }
    }

    @EventHandler
    public void onWindChargeHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof WindCharge) {
            WindCharge windCharge = (WindCharge) event.getEntity();
            double explosionForce = plugin.getConfig().getDouble("windCharge.explosionForce", 2.0);
            boolean addParticles = plugin.getConfig().getBoolean("windCharge.addParticles", true);
            String particleName = plugin.getConfig().getString("windCharge.projectileParticle", "SMOKE_NORMAL").toUpperCase();
            Particle projectileParticle = Particle.valueOf(particleName);

            windCharge.getWorld().createExplosion(windCharge.getLocation(), (float) explosionForce, false, true);
            windCharge.getWorld().playSound(windCharge.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

            if (addParticles) {
                windCharge.getWorld().spawnParticle(projectileParticle, windCharge.getLocation(), 20, 0, 0, 0, 0.1);
            }

            windCharge.remove();
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof WindCharge) {
            event.blockList().clear();
        }
    }

    private void addTrailingParticles(WindCharge windCharge) {
        String particleName = plugin.getConfig().getString("windCharge.projectileParticle", "SMOKE_NORMAL").toUpperCase();
        Particle trailParticle = Particle.valueOf(particleName);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (windCharge.isDead()) {
                    cancel();
                    return;
                }
                windCharge.getWorld().spawnParticle(trailParticle, windCharge.getLocation(), 1, 0, 0, 0, 0);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
