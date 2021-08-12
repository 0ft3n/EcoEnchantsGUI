package NewEvents;

import Main.Main;
import com.willfp.eco.core.Eco;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import com.willfp.ecoenchants.enchantments.support.merging.anvil.AnvilMerge;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class BookDragEvent implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryClickEvent e){
        if (e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)){
            return;
        }
        if (e.getCurrentItem() == null){
            return;
        }
        if (e.getCurrentItem().getType().equals(Material.AIR)
                || e.getCurrentItem().getType().equals(Material.BOOK)
                || e.getCurrentItem().getType().equals(Material.ENCHANTED_BOOK)){
            return;
        }
        if (e.getCursor() == null) return;
        if (e.getCursor().getType().equals(Material.AIR)){
            return;
        }
        if (!e.getCursor().getItemMeta().getPersistentDataContainer().has(Main.bookKey, PersistentDataType.STRING)){
            return;
        }
        e.setCancelled(true);
        NamespacedKey target = NamespacedKey.fromString(e.getCursor()
                .getItemMeta()
                .getPersistentDataContainer()
                .get(Main.bookKey, PersistentDataType.STRING));
        EcoEnchant ecoEnchant = EcoEnchants.getByKey(target);
        if (!ecoEnchant.canEnchantItem(e.getCurrentItem())){
            return;
        }
        if (e.getCurrentItem().getItemMeta().getEnchants().keySet().stream().anyMatch(enchantment -> ecoEnchant.conflictsWith(enchantment) || ecoEnchant.equals(enchantment))){
            return;
        }
        ItemMeta meta = e.getCurrentItem().getItemMeta();
        if (meta instanceof EnchantmentStorageMeta) {
            ((EnchantmentStorageMeta) meta).addStoredEnchant(ecoEnchant, ecoEnchant.getMaxLevel(), true);
        } else {
            meta.addEnchant(ecoEnchant, ecoEnchant.getMaxLevel(), true);
        }
        e.getCurrentItem().setItemMeta(meta);
        new BukkitRunnable() {
            @Override
            public void run() {
                e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
            }
        }.runTaskLaterAsynchronously(Main.getInstance(), 1);

    }

}
