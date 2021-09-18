package NewEvents;

import Main.Main;
import Menus.*;
import Utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

public class MainMenuClickEvent implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e){

        if (e.getCurrentItem() == null){
            return;
        }

        Player p = (Player) e.getWhoClicked();

        if (!p.getPersistentDataContainer().has(Main.ecoGuiMenu, PersistentDataType.STRING)){
            return;
        }
        else if (!p.getPersistentDataContainer().get(Main.ecoGuiMenu, PersistentDataType.STRING).equalsIgnoreCase(MainMenu.name)){
            return;
        }

        e.setCancelled(true);

        if (e.getClickedInventory().getHolder() != null){
            if (e.getClickedInventory().getHolder().equals(e.getWhoClicked())) return;
        }

        switch (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(Main.buttonKey, PersistentDataType.STRING)){
            case("EXIT"):
                p.closeInventory();
                break;
            case("MENU_NORMAL"):
                p.openInventory(new NormalPage().getMenu(1));
                p.getPersistentDataContainer().set(Main.ecoGuiMenu, PersistentDataType.STRING, NormalPage.name);
                break;
            case("MENU_CURSE"):
                p.openInventory(new CursePage().getMenu(1));
                p.getPersistentDataContainer().set(Main.ecoGuiMenu, PersistentDataType.STRING, CursePage.name);
                break;
            case("MENU_SPECIAL"):
                p.openInventory(new SpecialPage().getMenu(1));
                p.getPersistentDataContainer().set(Main.ecoGuiMenu, PersistentDataType.STRING, SpecialPage.name);
                break;
            case("MENU_ARTIFACT"):
                p.openInventory(new ArtifactPage().getMenu(1));
                p.getPersistentDataContainer().set(Main.ecoGuiMenu, PersistentDataType.STRING, ArtifactPage.name);
                break;
            case("MENU_SPELL"):
                p.openInventory(new SpellPage().getMenu(1));
                p.getPersistentDataContainer().set(Main.ecoGuiMenu, PersistentDataType.STRING, SpellPage.name);
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
    }
}
