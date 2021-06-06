package Events;

import Main.Main;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class InventoryClickEvent implements Listener {

    private Main plugin;

    public InventoryClickEvent(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(org.bukkit.event.inventory.InventoryClickEvent e){
        if (plugin.players.contains(e.getWhoClicked())){
            switch (e.getSlot()){
                case(10):
                    e.getWhoClicked().openInventory(plugin.createPage(1,"curse"));
                    plugin.sessions.add((Player) e.getWhoClicked());
                    plugin.players.remove((Player) e.getWhoClicked());
                    break;
                case(11):
                    e.getWhoClicked().openInventory(plugin.createPage(1,"normal"));
                    plugin.sessions.add((Player) e.getWhoClicked());
                    plugin.players.remove((Player) e.getWhoClicked());
                    break;
                case(13):
                    e.getWhoClicked().openInventory(plugin.createPage(1,"special"));
                    plugin.sessions.add((Player) e.getWhoClicked());
                    plugin.players.remove((Player) e.getWhoClicked());
                    break;
                case(15):
                    e.getWhoClicked().openInventory(plugin.createPage(1,"artifact"));
                    plugin.sessions.add((Player) e.getWhoClicked());
                    plugin.players.remove((Player) e.getWhoClicked());
                    break;
                case(16):
                    e.getWhoClicked().openInventory(plugin.createPage(1,"spell"));
                    plugin.sessions.add((Player) e.getWhoClicked());
                    plugin.players.remove((Player) e.getWhoClicked());
                    break;
                default:
                    break;

            }
            e.setCancelled(true);
        }

        else if (plugin.sessions.contains(e.getWhoClicked())){
            if (e.getClickedInventory().getHolder() != null){
                return;
            }
            int i;
            switch (e.getSlot()){
                case (48):
                    try{
                        i = Objects.requireNonNull(Objects.requireNonNull(e
                                .getCurrentItem())
                                .getItemMeta())
                                .getPersistentDataContainer()
                                .get(plugin.prevpage, PersistentDataType.INTEGER);
                        e.getWhoClicked().openInventory(plugin.createPage(i, Objects.requireNonNull(e
                                .getCurrentItem()
                                .getItemMeta())
                                .getPersistentDataContainer()
                                .get(new NamespacedKey(plugin,"Type"), PersistentDataType.STRING)));
                    }
                    catch (Exception ex){
                        e.setCancelled(true);
                        return;
                    }
                    plugin.sessions.add((Player) e.getWhoClicked());
                    break;
                case (49):
                    e.getWhoClicked().openInventory(plugin.mainMenu());
                    plugin.sessions.remove((Player) e.getWhoClicked());
                    plugin.players.add((Player) e.getWhoClicked());
                    break;
                case (50):
                    try{
                        i = Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem())
                                .getItemMeta())
                                .getPersistentDataContainer()
                                .get(plugin.nextpage, PersistentDataType.INTEGER);
                        e.getWhoClicked().openInventory(plugin.createPage(i,e
                                .getCurrentItem()
                                .getItemMeta()
                                .getPersistentDataContainer()
                                .get(new NamespacedKey(plugin,"Type"), PersistentDataType.STRING)));
                    }
                    catch (Exception ex){
                        e.setCancelled(true);
                        return;
                    }
                    plugin.sessions.add((Player) e.getWhoClicked());
                    break;
                default:
                    try{
                        EcoEnchant en = EcoEnchants.getByKey(NamespacedKey.fromString(e
                                .getCurrentItem()
                                .getItemMeta()
                                .getPersistentDataContainer()
                                .get(new NamespacedKey(plugin,"Eco"),PersistentDataType.STRING)));
                        if (e.getWhoClicked().hasPermission("ecogui.take") && plugin.getConfig().getBoolean("settings.allow-taking-books-from-menu")){
                            ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) it.getItemMeta();
                            assert meta != null;
                            meta.addStoredEnchant(en.getEnchantment(),en.getMaxLevel(),true);
                            PersistentDataContainer container = meta.getPersistentDataContainer();
                            container.set(new NamespacedKey(plugin,"isEcoGuiBook"),PersistentDataType.INTEGER,1);
                            it.setItemMeta(meta);
                            e.getWhoClicked().setItemOnCursor(it);
                        }
                    }
                    catch(Exception ex){
                        e.setCancelled(true);
                        return;
                    }
                    break;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(org.bukkit.event.inventory.InventoryClickEvent e){

        System.out.println("event");

        if (e.isCancelled()){
            return;
        }
        if (!e.isLeftClick()){
            return;
        }
        if (!e.getCursor().getType().equals(Material.ENCHANTED_BOOK)){
            return;
        }
        if (e.getCurrentItem().getType().equals(Material.AIR)){
            return;
        }
        if (e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)){
            return;
        }

        System.out.println("try");
        try {
            ItemStack backup = e.getCursor();
            if (!e.getCursor().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"isEcoGuiBook"),PersistentDataType.INTEGER)){
                return;
            }
            e.setCancelled(true);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) e.getCursor().getItemMeta();
            ItemMeta imeta = e.getCurrentItem().getItemMeta();
            for (Enchantment ench: meta.getStoredEnchants().keySet()){
                System.out.println(e.getCurrentItem());
                System.out.println(ench);
                EcoEnchant ecoench = EcoEnchants.getFromEnchantment(ench);
                System.out.println(ecoench.conflictsWithAny(e.getCurrentItem().getEnchantments().keySet()));
                if (!ecoench.conflictsWithAny(e.getCurrentItem().getEnchantments().keySet())
                        && e.getCurrentItem().getEnchantments().keySet().stream().allMatch(enchantment -> ecoench.isEnabled())
                        && ecoench.canEnchantItem(e.getCurrentItem())){
                    System.out.println(2);
                    if (imeta instanceof EnchantmentStorageMeta){
                        System.out.println(3.1);
                        ((EnchantmentStorageMeta) imeta).addStoredEnchant(ecoench, meta.getStoredEnchants().get(ench), true);
                        e.getCurrentItem().setItemMeta(imeta);
                    }
                    else {
                        System.out.println(3.2);
                        imeta.addEnchant(ecoench, meta.getStoredEnchants().get(ench), true);
                        e.getCurrentItem().setItemMeta(imeta);
                    }
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
                        }
                    }.runTask(plugin);
                }
            }
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return;
        }
    }

}
