package hu.rivalsnetwork.rivalsvanish.storage;

import hu.rivalsnetwork.rivalsapi.users.Key;
import hu.rivalsnetwork.rivalsapi.users.User;
import hu.rivalsnetwork.rivalsapi.utils.StringUtils;
import hu.rivalsnetwork.rivalsvanish.RivalsVanishPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Executor {
    public static final ArrayList<Player> vanishedPlayers = new ArrayList<>();

    public static void hide(@NotNull final Player player) {
        hideFromPlayers(player);
        vanishedPlayers.add(player);

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!RivalsVanishPlugin.LANG.getString("messages.fakeleave").equals("")) {
                p.sendMessage(StringUtils.format(RivalsVanishPlugin.LANG.getString("messages.fakeleave").replace("%player%", player.getName())));
            }
        });

        player.sendMessage(StringUtils.format(RivalsVanishPlugin.LANG.getString("messages.vanish").replace("%player%", player.getName())));

        RivalsVanishPlugin.getInstance().executor().execute(() -> {
            User user = RivalsVanishPlugin.getInstance().getUser(player);
            user.write("vanished-players", User.DataType.MONGODB, Key.of("vanished", true));
        });
    }

    public static void show(@NotNull final Player player) {
        showToPlayers(player);
        vanishedPlayers.remove(player);

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!RivalsVanishPlugin.LANG.getString("messages.fakejoin").equals("")) {
                p.sendMessage(StringUtils.format(RivalsVanishPlugin.LANG.getString("messages.fakejoin").replace("%player%", player.getName())));
            }
        });

        player.sendMessage(StringUtils.format(RivalsVanishPlugin.LANG.getString("messages.unvanish").replace("%player%", player.getName())));

        RivalsVanishPlugin.getInstance().executor().execute(() -> {
            User user = RivalsVanishPlugin.getInstance().getUser(player);
            user.write("vanished-players", User.DataType.MONGODB, Key.of("vanished", false));
        });
    }

    public static boolean isHidden(@NotNull final Player player) {
        boolean hidden;
        User user = RivalsVanishPlugin.getInstance().getUser(player);
        Object h = user.read(Key.of("vanished-players", "vanished"), User.DataType.MONGODB);
        if (h != null) {
            hidden = (boolean) h;
        } else hidden = false;

        return hidden;
    }

    public static void hideFromPlayers(@NotNull final Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("rivalsvanish.see")) continue;

            p.hidePlayer(RivalsVanishPlugin.getInstance(), player);
        }

        player.setMetadata("vanished", new FixedMetadataValue(RivalsVanishPlugin.getInstance(), true));
        vanishedPlayers.add(player);
    }

    public static void showToPlayers(@NotNull final Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(RivalsVanishPlugin.getInstance(), player);
        }

        player.removeMetadata("vanished", RivalsVanishPlugin.getInstance());
        vanishedPlayers.remove(player);
    }
}
