package NewEvents;

import Main.Main;
import Menus.MainMenu;
import Menus.SearchPage;
import Utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

public class SearchPageClickEvent implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e){

        if (e.getCurrentItem() == null){
            return;
        }

        Player p = (Player) e.getWhoClicked();

        if (!p.getPersistentDataContainer().has(Main.ecoGuiMenu, PersistentDataType.STRING)){
            return;
        }
        else if (!p.getPersistentDataContainer().get(Main.ecoGuiMenu, PersistentDataType.STRING).equalsIgnoreCase(SearchPage.name)){
            return;
        }

        e.setCancelled(true);
        try {

            switch (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(Main.buttonKey, PersistentDataType.STRING)){
                case("EXIT"):
                    p.closeInventory();
                    break;
                case("BACK"):
                    p.openInventory(new MainMenu().getMenu());
                    p.getPersistentDataContainer().set(Main.ecoGuiMenu, PersistentDataType.STRING, MainMenu.name);
                    break;
                case("NEXTPAGE"):
                case("PREVPAGE"):
                    int page = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(Main.page, PersistentDataType.INTEGER);
                    if (page > 0){
                        String query = p.getPersistentDataContainer().get(Main.search, PersistentDataType.STRING);
                        p.openInventory(new SearchPage(query).getMenu(page));
                        p.getPersistentDataContainer().set(Main.search, PersistentDataType.STRING, query);
                        p.getPersistentDataContainer().set(Main.ecoGuiMenu, PersistentDataType.STRING, SearchPage.name);
                    }
                    break;
                case("SEARCH"):
                    p.closeInventory();
                    String message = Main.getConfigString("messages.awaiting-query");
                    MessageUtils.sendMessage(message, p);
                    SearchPage.awaiting.add(p);
                    break;
                default:
                    break;
            }
        } catch (NullPointerException ignored){}
    }

}