package NewEvents;

import Main.Main;
import Menus.*;
import com.willfp.eco.core.items.builder.EnchantedBookBuilder;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class BookTakeEvent implements Listener {

    @EventHandler ()
    public void onClick(InventoryClickEvent e){

        try{
            if (!e.getWhoClicked().getItemOnCursor().getType().equals(Material.AIR)){
                return;
            }
            else if (!e.getWhoClicked().getPersistentDataContainer().has(Main.ecoGuiMenu, PersistentDataType.STRING)){
                return;
            }
            else if (!e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(Main.bookKey, PersistentDataType.STRING)){
                return;
            }
            if (!e.getWhoClicked().hasPermission("ecogui.take")){
                return;
            }
        } catch (NullPointerException ignored){
            return;
        }
        switch (e.getWhoClicked().getPersistentDataContainer().get(Main.ecoGuiMenu, PersistentDataType.STRING)){
            case(NormalPage.name):
            case(CursePage.name):
            case(SpecialPage.name):
            case(ArtifactPage.name):
            case(SpellPage.name):
            case(SearchPage.name):
                break;
            default:
                return;
        }
        e.setCancelled(true);
        String value = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(Main.bookKey, PersistentDataType.STRING);
        NamespacedKey target = NamespacedKey.fromString(value);
        EcoEnchant ecoEnchant = EcoEnchants.getByKey(target);
        EnchantedBookBuilder builder = new EnchantedBookBuilder();
        //builder.writeMetaKey(Main.bookKey, PersistentDataType.STRING, value);
        builder.addStoredEnchantment(ecoEnchant, ecoEnchant.getMaxLevel());
        ItemStack item = builder.build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(Main.bookKey, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        e.getWhoClicked().setItemOnCursor(item);

    }

}
