package hu.rivalsnetwork.rivalsvanish.storage;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import hu.rivalsnetwork.rivalsapi.storage.Storage;
import hu.rivalsnetwork.rivalsapi.utils.StringUtils;
import hu.rivalsnetwork.rivalsvanish.RivalsVanishPlugin;
import org.bson.Document;
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

        Storage.mongo(database -> {
            MongoCollection<Document> collection = database.getCollection("vanished-players");
            Document document = new Document();
            document.put("uuid", player.getUniqueId().toString());
            document.put("name", player.getName());
            document.put("vanished", true);
            Document filter = new Document();
            Document updateDocument = new Document();
            updateDocument.put("$set", document);
            filter.put("uuid", player.getUniqueId().toString());

            collection.updateOne(filter, updateDocument, new UpdateOptions().upsert(true));
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

        Storage.mongo(database -> {
            MongoCollection<Document> collection = database.getCollection("vanished-players");
            Document document = new Document();
            document.put("uuid", player.getUniqueId().toString());
            document.put("name", player.getName());
            document.put("vanished", false);
            Document filter = new Document();
            Document updateDocument = new Document();
            updateDocument.put("$set", document);
            filter.put("uuid", player.getUniqueId().toString());

            collection.updateOne(filter, updateDocument, new UpdateOptions().upsert(true));
        });
    }

    public static boolean isHidden(@NotNull final Player player) {
        boolean[] hidden = {false};
        Storage.mongo(database -> {
            MongoCollection<Document> collection = database.getCollection("vanished-players");
            Document searchQuery = new Document();
            searchQuery.put("uuid", player.getUniqueId().toString());
            FindIterable<Document> cursor = collection.find(searchQuery);

            try (final MongoCursor<Document> cursorIterator = cursor.cursor()) {
                if (cursorIterator.hasNext()) {
                    Document doc = cursorIterator.next();
                    hidden[0] = doc.getBoolean("vanished");
                }
            }
        });
        return hidden[0];
    }

    public static void hideFromPlayers(@NotNull final Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("rivalsvanish.see")) continue;

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
