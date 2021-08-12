package NewEvents;

import Main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.persistence.PersistentDataType;

public class GuiCloseEvent implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if (e.getPlayer().getPersistentDataContainer().has(Main.ecoGuiMenu, PersistentDataType.STRING)){
            e.getPlayer().getPersistentDataContainer().remove(Main.ecoGuiMenu);
        }
        if (e.getPlayer().getPersistentDataContainer().has(Main.search, PersistentDataType.STRING)){
            e.getPlayer().getPersistentDataContainer().remove(Main.search);
        }
    }

}
