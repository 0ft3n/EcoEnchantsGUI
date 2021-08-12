package NewEvents;

import Main.Main;
import Menus.SearchPage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerChatSearchEvent implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e){

        if (!SearchPage.awaiting.contains(e.getPlayer())){
            return;
        }

        e.setCancelled(true);
        String query = e.getMessage();
        new BukkitRunnable() {
            @Override
            public void run() {
                e.getPlayer().openInventory(new SearchPage(query).getMenu(1));
                e.getPlayer().getPersistentDataContainer().set(Main.ecoGuiMenu, PersistentDataType.STRING, SearchPage.name);
                e.getPlayer().getPersistentDataContainer().set(Main.search, PersistentDataType.STRING, query);
                SearchPage.awaiting.remove(e.getPlayer());
            }
        }.runTask(Main.getInstance());
    }

}
