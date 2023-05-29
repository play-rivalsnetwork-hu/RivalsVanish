package hu.rivalsnetwork.rivalsvanish.listeners;

import hu.rivalsnetwork.rivalsapi.utils.StringUtils;
import hu.rivalsnetwork.rivalsvanish.RivalsVanishPlugin;
import hu.rivalsnetwork.rivalsvanish.storage.Executor;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(@NotNull final PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("rivalsvanish.see")) {
            for (Player player : Executor.vanishedPlayers) {
                event.getPlayer().hidePlayer(RivalsVanishPlugin.getInstance(), player);
            }
        }

        if (event.getPlayer().hasPermission("rivalsvanish.vanish")) {
            if (Executor.isHidden(event.getPlayer())) {
                Executor.hideFromPlayers(event.getPlayer());
                event.joinMessage(Component.empty());
                Executor.vanishedPlayers.forEach(player -> player.sendMessage(StringUtils.formatToComponent(RivalsVanishPlugin.LANG.getString("messages.join-vanished").replace("%player%", event.getPlayer().getName()))));
            }
        } else {
            if (Executor.isHidden(event.getPlayer())) {
                Executor.show(event.getPlayer());

                Executor.vanishedPlayers.forEach(player -> player.sendMessage(StringUtils.formatToComponent(RivalsVanishPlugin.LANG.getString("messages.error").replace("%player%", event.getPlayer().getName()))));
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(@NotNull final PlayerQuitEvent event) {
        if (Executor.vanishedPlayers.contains(event.getPlayer())) {
            event.quitMessage(Component.empty());
            Executor.vanishedPlayers.remove(event.getPlayer());
            Executor.vanishedPlayers.forEach(player -> player.sendMessage(StringUtils.formatToComponent(RivalsVanishPlugin.LANG.getString("messages.left-vanished").replace("%player%", event.getPlayer().getName()))));
        }
    }

    @EventHandler
    public void onItemPickupEvent(@NotNull final PlayerAttemptPickupItemEvent event) {
        if (Executor.vanishedPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(@NotNull final PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        if (Executor.vanishedPlayers.contains(event.getPlayer())) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(@NotNull final InventoryCloseEvent event) {
        if (Executor.vanishedPlayers.contains(event.getPlayer())) {
            event.getPlayer().setGameMode(GameMode.CREATIVE);
        }
    }
}
