package hu.rivalsnetwork.rivalsvanish;

import hu.rivalsnetwork.rivalsapi.config.Config;
import hu.rivalsnetwork.rivalsapi.config.ConfigType;
import hu.rivalsnetwork.rivalsapi.config.Configuration;
import hu.rivalsnetwork.rivalsapi.plugin.RivalsPluginImpl;
import hu.rivalsnetwork.rivalsapi.utils.StringUtils;
import hu.rivalsnetwork.rivalsvanish.listeners.PlayerListener;
import hu.rivalsnetwork.rivalsvanish.storage.Executor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RivalsVanishPlugin extends RivalsPluginImpl {
    private static RivalsVanishPlugin plugin;
    @Configuration(configType = ConfigType.YAML, name = "lang")
    public static Config LANG;

    @Override
    public void enable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        scheduler().runTimer(() -> {
            for (Player vanishedPlayer : Executor.vanishedPlayers) {
                vanishedPlayer.sendActionBar(StringUtils.formatToComponent(LANG.getString("messages.actionbar")));
            }
        }, 0, 10);
    }

    @Override
    public void reload() {
        LANG.reload();
    }

    public static RivalsVanishPlugin getInstance() {
        return plugin;
    }

}