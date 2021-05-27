import com.willfp.ecoenchants.display.EnchantmentCache;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import com.willfp.ecoenchants.enchantments.meta.EnchantmentTarget;
import com.willfp.ecoenchants.enchantments.util.EnchantChecks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main extends JavaPlugin implements Listener {

    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Player> sessions = new ArrayList<>();
    ArrayList<EcoEnchant> curse = new ArrayList<>();
    ArrayList<EcoEnchant> normal = new ArrayList<>();
    ArrayList<EcoEnchant> special = new ArrayList<>();
    ArrayList<EcoEnchant> artifact = new ArrayList<>();
    ArrayList<EcoEnchant> spell = new ArrayList<>();
    int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
    int[] menumisc = {0,1,2,3,4,5,6,7,8,9,12,14,17,18,19,20,21,22,23,24,25,26};
    int[] pageslots = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,51,52,53};
    Map<String,Integer> menuslots = new HashMap<>();
    NamespacedKey nextpage;
    NamespacedKey prevpage;

    @Override
    public void onLoad(){
        System.out.println("Loading EcoEnchantsGUI...");
    }

    @Override
    public void onEnable(){
        getEnchantments();
        this.saveDefaultConfig();
        System.out.println("Im gay");
        this.getServer().getPluginManager().registerEvents(this,this);
        this.getCommand("ecogui").setExecutor(new EcoGuiCommand(this));
        this.getCommand("ecogui").setTabCompleter(new TabComplete());
        nextpage = new NamespacedKey(this, "Nextpage");
        prevpage = new NamespacedKey(this, "Prevpage");
        menuslots.put("curse",this.getConfig().getInt("Buttons.menu_curse.slot"));
        menuslots.put("normal",this.getConfig().getInt("Buttons.menu_normal.slot"));
        menuslots.put("special",this.getConfig().getInt("Buttons.menu_special.slot"));
        menuslots.put("artifact",this.getConfig().getInt("Buttons.menu_artifact.slot"));
        menuslots.put("spell",this.getConfig().getInt("Buttons.menu_spell.slot"));
    }

    @Override
    public void onDisable(){

    }


    public void getEnchantments(){
        curse.clear();
        normal.clear();
        special.clear();
        artifact.clear();
        spell.clear();
        for (EcoEnchant e: EcoEnchants.values()){
            if (e.isEnabled() && this.getConfig().getBoolean("settings.ignore-disabled-enchantments")){
                switch (e.getType().getName()){
                    case "curse":
                        curse.add(e);
                        break;
                    case "normal":
                        normal.add(e);
                        break;
                    case "special":
                        special.add(e);
                        break;
                    case "artifact":
                        artifact.add(e);
                        break;
                    case "spell":
                        spell.add(e);
                        break;

                }
            }
        }
    }


    public String replaceItemVars(String s, EcoEnchant e){

        StringBuilder conflicts = new StringBuilder();
        int length = 0;

        if (e.getConflicts().size()>0 && e.conflictsWithAny(EcoEnchants.values())){
            for (int i = 0; i<e.getConflicts().size()-1;i++){
                conflicts.append(EnchantmentCache.getEntry((Enchantment) e.getConflicts().toArray()[i]).getName()).append(", ");
                length+=(EnchantmentCache.getEntry((Enchantment) e.getConflicts().toArray()[i])+", ").length();
            }
            conflicts.append(EnchantmentCache.getEntry((Enchantment) e.getConflicts().toArray()[e.getConflicts().size() - 1]).getName()).append("\n");
        }
        else {
            conflicts = new StringBuilder(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("translations.messages.no-conflicts")));
        }

        StringBuilder apply = new StringBuilder();
        length=0;

        for (int i = 0; i<e.getTargets().size()-1;i++){
            String app = ((EnchantmentTarget)e.getTargets().toArray()[i]).getName();
            apply.append(this.getConfig().getString("translations.enchantment-targets." + app)).append(",");
            length+=(this.getConfig().getString("translations.enchantment-targets."+app)+",").length();
        }
        String app = ((EnchantmentTarget)e.getTargets().toArray()[e.getTargets().size() - 1]).getName();
        apply.append(this.getConfig().getString("translations.enchantment-targets." + app)).append("\n");
        return s.replace("{name}", EnchantmentCache.getEntry(e).getRawName())
                .replace("{rarity}", Objects.requireNonNull(this.getConfig().getString("translations.rarity." + e.getRarity().getName().toLowerCase())))
                .replace("{max-level}",String.valueOf(e.getMaxLevel()))
                .replace("{conflicts}", conflicts.toString())
                .replace("{apply-on}", apply.toString())
                .replace("{rarity-color}", Objects.requireNonNull(this.getConfig().getString("translations.rarity-colors." + e.getRarity().getName().toLowerCase())));
    }

    public int getMaxPages(String type){

        if (getType(type).size()%slots.length>0){
            return getType(type).size()/slots.length+1;
        }

        return getType(type).size()/slots.length;

    }
    /*
    @EventHandler(priority = EventPriority.NORMAL)
    public void onDrag(InventoryClickEvent e){
        if (e.getWhoClicked().getItemOnCursor().getType().equals(Material.ENCHANTED_BOOK)){
            System.out.println("1");
            if (e.getWhoClicked().getItemOnCursor().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this,"isEcoGuiBook"),PersistentDataType.INTEGER)){
                System.out.println("2");
                if (e.getCurrentItem() != null){
                    System.out.println("3");
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) e.getWhoClicked().getItemOnCursor().getItemMeta();
                    for (Map.Entry<Enchantment,Integer> entry: meta.getStoredEnchants().entrySet()){
                        System.out.println("4");
                        EcoEnchant ench = EcoEnchants.getFromEnchantment(entry.getKey());
                        if (entry.getKey().canEnchantItem(e.getCurrentItem())){
                            System.out.println("5");
                            if (ench.isEnabled()){
                                System.out.println("6");
                                if (!ench.conflictsWithAny(e.getCurrentItem().getEnchantments().keySet())){
                                    System.out.println("7");
                                    if (!e.getCurrentItem().getEnchantments().containsKey(entry.getKey())){
                                        System.out.println("8");
                                        ((EnchantmentStorageMeta) Objects.requireNonNull(e.getCurrentItem().getItemMeta())).addStoredEnchant(ench.getEnchantment(),entry.getValue(),true);
                                        e.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
     */

    @EventHandler
    public void onCloseEvent(InventoryCloseEvent e){
        if (players.contains(e.getPlayer())){
            players.remove(e.getPlayer());
        }
        if (sessions.contains(e.getPlayer())){
            sessions.remove(e.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(InventoryClickEvent e){
        if (players.contains(e.getWhoClicked())){
            switch (e.getSlot()){
                case(10):
                    e.getWhoClicked().openInventory(createPage(1,"curse"));
                    sessions.add((Player) e.getWhoClicked());
                    players.remove((Player) e.getWhoClicked());
                    break;
                case(11):
                    e.getWhoClicked().openInventory(createPage(1,"normal"));
                    sessions.add((Player) e.getWhoClicked());
                    players.remove((Player) e.getWhoClicked());
                    break;
                case(13):
                    e.getWhoClicked().openInventory(createPage(1,"special"));
                    sessions.add((Player) e.getWhoClicked());
                    players.remove((Player) e.getWhoClicked());
                    break;
                case(15):
                    e.getWhoClicked().openInventory(createPage(1,"artifact"));
                    sessions.add((Player) e.getWhoClicked());
                    players.remove((Player) e.getWhoClicked());
                    break;
                case(16):
                    e.getWhoClicked().openInventory(createPage(1,"spell"));
                    sessions.add((Player) e.getWhoClicked());
                    players.remove((Player) e.getWhoClicked());
                    break;
                default:
                    break;

            }
            e.setCancelled(true);
        }

        else if (sessions.contains(e.getWhoClicked())){
            if (e.getClickedInventory().getHolder() != null){
                return;
            }
            int i;
            switch (e.getSlot()){
                case (48):
                    try{
                        i = Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getPersistentDataContainer().get(prevpage, PersistentDataType.INTEGER);
                        e.getWhoClicked().openInventory(createPage(i, Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getPersistentDataContainer().get(new NamespacedKey(this,"Type"), PersistentDataType.STRING)));
                    }
                    catch (Exception ex){
                        e.setCancelled(true);
                        return;
                    }
                    sessions.add((Player) e.getWhoClicked());
                    break;
                case (49):
                    e.getWhoClicked().openInventory(mainMenu());
                    sessions.remove((Player) e.getWhoClicked());
                    players.add((Player) e.getWhoClicked());
                    break;
                case (50):
                    try{
                        i = Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getPersistentDataContainer().get(nextpage, PersistentDataType.INTEGER);
                        e.getWhoClicked().openInventory(createPage(i,e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(this,"Type"), PersistentDataType.STRING)));
                    }
                    catch (Exception ex){
                        e.setCancelled(true);
                        return;
                    }
                    sessions.add((Player) e.getWhoClicked());
                    break;
                default:
                    try{
                        EcoEnchant en = EcoEnchants.getByKey(NamespacedKey.fromString(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(this,"Eco"),PersistentDataType.STRING)));
                        if (e.getWhoClicked().hasPermission("ecogui.take")){
                            ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) it.getItemMeta();
                            assert meta != null;
                            meta.addStoredEnchant(en.getEnchantment(),en.getMaxLevel(),true);
                            PersistentDataContainer container = meta.getPersistentDataContainer();
                            container.set(new NamespacedKey(this,"isEcoGuiBook"),PersistentDataType.INTEGER,1);
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

    public ArrayList<EcoEnchant> getType(String type){

        switch(type){

            case("curse"):
                return curse;
            case("normal"):
                return normal;
            case("special"):
                return special;
            case("artifact"):
                return artifact;
            case("spell"):
                return spell;
            default:
                break;
        }
        return null;
    }

    public Inventory createPage(int num, String type){

        String guiname = Objects.requireNonNull(this.getConfig().getString("GUI-Names." + type)).replace("{page}",String.valueOf(num)).replace("{max}",String.valueOf(getMaxPages(type)));

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&',guiname));

        int start;

        int left = getType(type).size()-slots.length*(num-1);

        if (num<=1){
            start=1;
        }

        else {
            start=(num-1)*slots.length;
        }

        if (this.getConfig().getBoolean("Buttons.misc.enabled")){
            ItemStack misc = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.getConfig().getString("Buttons.misc.material")))),1);
            ItemMeta meta = misc.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.getConfig().getString("Buttons.misc.name"))));
            misc.setItemMeta(meta);
            for (int i: pageslots){
                inv.setItem(i,misc);
            }
        }

        if (left<=slots.length){
            for (int i=0; i<left; i++){


                EcoEnchant e = getType(type).get(i+start-1);

                ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.set(new NamespacedKey(this,"Eco"),PersistentDataType.STRING,e.getEnchantment().getKey().toString());

                ArrayList<String> lore = new ArrayList<>();

                assert meta != null;
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',replaceItemVars(this.getConfig().getString("display-formats."+type+".name"),e)));

                for (String line: this.getConfig().getStringList("display-formats."+type+".lore")){
                    if (line.contains("{description}")){
                        for (String s: e.getWrappedDescription()){
                            lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("{description}",s)));
                        }
                    }
                    else{
                        lore.add(ChatColor.translateAlternateColorCodes('&',replaceItemVars(line,e)));
                    }
                }

                meta.setLore(lore);

                item.setItemMeta(meta);

                inv.setItem(slots[i],item);
            }
        }
        else {
            for (int i=0; i<slots.length; i++){

                EcoEnchant e = getType(type).get(i+start-1);

                ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.set(new NamespacedKey(this,"Eco"),PersistentDataType.STRING,e.getEnchantment().getKey().toString());

                ArrayList<String> lore = new ArrayList<>();

                assert meta != null;

                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',replaceItemVars(this.getConfig().getString("display-formats."+type+".name"),e)));

                for (String line: this.getConfig().getStringList("display-formats."+type+".lore")){
                    if (line.contains("{description}")){
                        for (String s: e.getWrappedDescription()){
                            lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("{description}",s)));
                        }
                    }
                    else{
                        lore.add(ChatColor.translateAlternateColorCodes('&',replaceItemVars(line,e)));
                    }
                }

                meta.setLore(lore);

                item.setItemMeta(meta);

                inv.setItem(slots[i],item);
            }
        }

        int usedslot;

        ItemStack button = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.getConfig().getString("Buttons.menu.material")))),1);
        ItemMeta meta = button.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.getConfig().getString("Buttons.menu.name"))));
        button.setItemMeta(meta);
        usedslot = this.getConfig().getInt("Buttons.menu.slot");
        inv.setItem(usedslot,button);

        if (left>slots.length) {
            button = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.getConfig().getString("Buttons.next_page.material_active")))),1);
            meta = button.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.getConfig().getString("Buttons.next_page.name_active"))));
            meta.getPersistentDataContainer().set(nextpage, PersistentDataType.INTEGER, num+1);
            meta.getPersistentDataContainer().set(new NamespacedKey(this,"Type"),PersistentDataType.STRING,type);
            button.setItemMeta(meta);
            usedslot = this.getConfig().getInt("Buttons.next_page.slot");
            inv.setItem(usedslot, button);
        }

        else{
            button = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.getConfig().getString("Buttons.next_page.material_inactive")))),1);
            meta = button.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.getConfig().getString("Buttons.next_page.name_inactive"))));
            button.setItemMeta(meta);
            usedslot = this.getConfig().getInt("Buttons.next_page.slot");
            inv.setItem(usedslot, button);
        }

        if (num>1) {
            button = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.getConfig().getString("Buttons.prev_page.material_active")))),1);
            meta = button.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.getConfig().getString("Buttons.prev_page.name_active"))));
            meta.getPersistentDataContainer().set(prevpage, PersistentDataType.INTEGER, num-1);
            meta.getPersistentDataContainer().set(new NamespacedKey(this,"Type"),PersistentDataType.STRING,type);
            button.setItemMeta(meta);
            usedslot = this.getConfig().getInt("Buttons.prev_page.slot");
            inv.setItem(usedslot, button);
        }
        else{
            button = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.getConfig().getString("Buttons.prev_page.material_inactive")))),1);
            meta = button.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.getConfig().getString("Buttons.prev_page.name_inactive"))));
            button.setItemMeta(meta);
            usedslot = this.getConfig().getInt("Buttons.prev_page.slot");
            inv.setItem(usedslot, button);
        }


        return inv;
    }

    public Inventory mainMenu(){

        String guiname = this.getConfig().getString("GUI-Names.main");

        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&',guiname));


        if (this.getConfig().getBoolean("Buttons.misc.enabled")){
            ItemStack misc = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.getConfig().getString("Buttons.misc.material")))),1);
            ItemMeta miscmeta = misc.getItemMeta();
            assert miscmeta != null;
            miscmeta.setDisplayName(this.getConfig().getString("Buttons.misc.name"));
            misc.setItemMeta(miscmeta);
            for (int i: menumisc){
                inv.setItem(i,misc);
            }
        }

        ItemStack button = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE,1);
        ItemMeta meta = button.getItemMeta();

        for (Map.Entry e: menuslots.entrySet()){
            assert meta != null;
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.getConfig().getString("Buttons.menu_" + e.getKey() + ".name"))));
            button.setItemMeta(meta);
            button.setType(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.getConfig().getString("Buttons.menu_" + e.getKey() + ".material")))));
            inv.setItem((Integer) e.getValue(),button);
        }

        return inv;

    }

}
