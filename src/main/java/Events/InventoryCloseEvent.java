package Events;

import Main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class InventoryCloseEvent implements Listener {

    private Main plugin;

    public InventoryCloseEvent(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onCloseEvent(org.bukkit.event.inventory.InventoryCloseEvent e){
        if (plugin.players.contains(e.getPlayer())){
            plugin.players.remove(e.getPlayer());
        }
        if (plugin.sessions.contains(e.getPlayer())){
            plugin.sessions.remove(e.getPlayer());
        }
    }

}
