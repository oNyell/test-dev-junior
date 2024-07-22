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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class WindChargeListener implements Listener {

    private final Main plugin;
    private final Random random = new Random();

    public WindChargeListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUseWindCharge(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getInventory().getItemInMainHand().getType() == Material.WIND_CHARGE) {
            event.setCancelled(true);

            double explosionForce = plugin.getConfig().getDouble("windCharge.explosionForce", 2.0);
            double projectileSpeed = plugin.getConfig().getDouble("windCharge.projectileSpeed", 1.0);
            //Está amarelado pois a função WIND_CHARGE ainda está em testes
            WindCharge windCharge = (WindCharge) player.getWorld().spawnEntity(player.getLocation(), EntityType.WIND_CHARGE);
            windCharge.setIsIncendiary(false);
            Vector direction = player.getLocation().getDirection().multiply(projectileSpeed);
            windCharge.setVelocity(direction);
        }
    }

    @EventHandler
    public void onWindChargeHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof WindCharge) {
            WindCharge windCharge = (WindCharge) event.getEntity();

            double explosionForce = plugin.getConfig().getDouble("windCharge.explosionForce", 2.0);
            boolean addParticles = plugin.getConfig().getBoolean("windCharge.addParticles", true);
            double maxParticle = plugin.getConfig().getDouble("windCharge.max-particle", 20.0);
            String particleName = plugin.getConfig().getString("windCharge.projectileParticle", "EXPLOSION_LARGE");
            Particle projectileParticle = Particle.valueOf(particleName.toUpperCase());

            event.setCancelled(true);

            windCharge.getWorld().createExplosion(windCharge.getLocation(), (float) explosionForce, false, true);

            windCharge.getWorld().playSound(windCharge.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

            if (addParticles) {
                int particleCount = random.nextInt((int) maxParticle + 1);
                windCharge.getWorld().spawnParticle(projectileParticle, windCharge.getLocation(), particleCount, 0, 0, 0, 0.1);
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
}
