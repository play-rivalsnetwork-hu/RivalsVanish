package hu.rivalsnetwork.rivalsvanish.command;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import hu.rivalsnetwork.rivalsapi.commands.Command;
import hu.rivalsnetwork.rivalsapi.utils.StringUtils;
import hu.rivalsnetwork.rivalsvanish.RivalsVanishPlugin;
import hu.rivalsnetwork.rivalsvanish.storage.Executor;
import org.bukkit.entity.Player;

public class RivalsVanishCommand {

    @Command
    public static void register() {
        new CommandTree("vanish")
                .withAliases("v", "rv", "rivalsvanish")
                .withPermission("rivalsvanish.vanish")
                .executesPlayer(info -> {
                    if (Executor.isHidden(info.sender())) {
                        Executor.show(info.sender());
                    } else {
                        Executor.hide(info.sender());
                    }
                })
                .then(new PlayerArgument("player")
                        .withPermission("rivalsvanish.other.vanish")
                        .executes(info -> {
                            Player player = (Player) info.args().get("player");
                            if (!info.sender().hasPermission("rivalsvanish.other.vanish") && player != info.sender()) {
                                info.sender().sendMessage(StringUtils.format(RivalsVanishPlugin.LANG.getString("messages.no-permission").replace("%player%", player.getName())));
                                return;
                            }

                            if (Executor.isHidden(player)) {
                                Executor.show(player);
                                info.sender().sendMessage(StringUtils.formatToComponent(RivalsVanishPlugin.LANG.getString("messages.unvanish-other").replace("%player%", player.getName())));
                            } else {
                                Executor.hide(player);
                                info.sender().sendMessage(StringUtils.formatToComponent(RivalsVanishPlugin.LANG.getString("messages.vanish-other").replace("%player%", player.getName())));
                            }
                        })
                )
                .then(new LiteralArgument("reload")
                        .executes(info -> {
                            info.sender().sendMessage(StringUtils.format(RivalsVanishPlugin.LANG.getString("reload").replace("%time%", RivalsVanishPlugin.getInstance().reloadTime())));
                        })
                )
                .register();
    }
}
