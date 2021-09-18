package Menus;

import Main.Main;
import Utils.ColorUtils;
import Utils.HeadUtils;
import Utils.MessageUtils;
import Utils.PageUtils;
import com.willfp.ecoenchants.display.EnchantmentCache;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Main.Main.getConfigBoolean;
import static Main.Main.getConfigStringList;
import static Utils.PageUtils.replaceItemVars;

public class SearchPage {

    public List<EcoEnchant> enchantments;
    public static ArrayList<Player> awaiting = new ArrayList<>();
    private int pageCount;
    private int page;
    private boolean nextActive;
    private boolean prevActive;
    private final String query;
    public static final String name = "Search";

    public SearchPage(String searchString){
        this.query = searchString;
        boolean ignore = getConfigBoolean("settings.ignore-disabled-enchantments");
        List<String> searchTo = getConfigStringList("settings.search-in");
        enchantments = EcoEnchants.values().stream().filter(ecoEnchant -> {
            try {
                if (!ignore || ecoEnchant.isEnabled()){
                    return (searchTo.contains("NAME") && EnchantmentCache.getEntry(ecoEnchant).getRawName().toLowerCase().contains(MessageUtils.toCheckMessage(searchString))) ||
                            (searchTo.contains("DESCRIPTION") && ecoEnchant.getDescription().toLowerCase().contains(MessageUtils.toCheckMessage(searchString))) ||
                            (searchTo.contains("APPLICATION") && ecoEnchant.getTargets().stream().anyMatch(target -> Main.getConfigString("messages.enchantment-targets."+target.getName()).toLowerCase().contains(searchString.toLowerCase()))) ||
                            (searchTo.contains("CONFLICTS") && ecoEnchant.getConflicts().stream().anyMatch(enchantment -> EnchantmentCache.getEntry(enchantment).getRawName().toLowerCase().contains(MessageUtils.toCheckMessage(searchString)))) ||
                            (searchTo.contains("RARITY") && MessageUtils.toCheckMessage(Main.getConfigString("messages.rarity."+ecoEnchant.getEnchantmentRarity().getName())).contains(MessageUtils.toCheckMessage(searchString)))
                            ;
                }
            } catch (NullPointerException ignored){}
            
            return false;
        }).collect(Collectors.toList());

    }

    public int getMaxPages(){
        int result = BigDecimal.valueOf((double) enchantments.size() / (double) pageCount).setScale(0, RoundingMode.UP).intValue();
        if (result == 0){
            return 1;
        }
        return result;
    }

    public Inventory getMenu(int page){
        this.page = page;

        int guiSize = Main.getConfigInt("menus.search.size");

        Inventory mainMenu = null;

        for (String button: Main.getConfigKeys("menus.search.buttons", false)){
            List<Integer> intSlots = Main.getConfigIntList("menus.search.buttons."+button+".slots");
            String type = Main.getConfigString("menus.search.buttons."+button+".type");
            if (type.equalsIgnoreCase("ENCHANTMENT")){
                pageCount = intSlots.size();
                nextActive = page<getMaxPages();
                prevActive = page>1;
                String guiName = ColorUtils.colorMessage(Main.getConfigString("menus.search.title")
                        .replace("{page}", Integer.toString(page))
                        .replace("{query}", query)
                        .replace("{maxpages}", Integer.toString(getMaxPages()))
                );
                mainMenu = Bukkit.createInventory(null, guiSize, guiName);
                int k = (page-1)*pageCount;
                //int left = normal.size() - (pageCount*page);
                for (int i: intSlots){
                    if (k>=enchantments.size()){
                        break;
                    }
                    mainMenu.setItem(i, getEnchantmentItem(button, enchantments.get(k)));
                    k++;
                }
            }
        }

        for (String button: Main.getConfigKeys("menus.search.buttons", false)){
            List<Integer> intSlots = Main.getConfigIntList("menus.search.buttons."+button+".slots");
            String type = Main.getConfigString("menus.search.buttons."+button+".type");
            if (!type.equalsIgnoreCase("ENCHANTMENT")) {
                for (int i: intSlots){
                    mainMenu.setItem(i, getItem(button));
                }
            }
        }

        return mainMenu;

    }

    public ItemStack getEnchantmentItem(String name, EcoEnchant e){

        ItemStack button;
        String materialString = Main.getConfigString("menus.search.buttons."+name+".material");
        String itemName = replaceItemVars(Main.getConfigString("menus.search.buttons."+name+".name"), e);
        List<String> itemLore = new ArrayList<>();
        boolean isGlowing = getConfigBoolean("menus.search.buttons."+name+".glowing");
        for (String line: Main.getConfigStringList("menus.search.buttons."+name+".lore")){
            if (line.contains("{description}")){
                itemLore.addAll(PageUtils.buildDescription(e, line));
            }
            else if (line.contains("{apply-on}")){
                List<String> toAdd = PageUtils.wrapString(PageUtils.buildAppliements(e));
                for (String s : toAdd) {
                    itemLore.add(ColorUtils.colorMessage(line.replace("{apply-on}", s)));
                }
            }
            else if (line.contains("{conflicts}")){
                List<String> toAdd = PageUtils.wrapString(PageUtils.buildConflicts(e));
                for (String s : toAdd) {
                    itemLore.add(ColorUtils.colorMessage(line.replace("{conflicts}", s)));
                }
            }
            else {
                itemLore.add(replaceItemVars(line, e));
            }
        }
        if (materialString.contains("head:")){
            String b64 = materialString.replace("head:","").replace(" ", "");
            button = HeadUtils.itemFromBase64(b64);
        }
        else if (materialString.contains("hdb:")){
            String id = materialString.replace("hdb:", "").replace(" ", "");
            button = new HeadDatabaseAPI().getItemHead(id);
        }
        else {
            Material itemMaterial = Material.getMaterial(materialString);
            button = new ItemStack(itemMaterial, 1);
        }
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(itemLore);
        int modelData = Main.getConfigInt("menus.search.buttons."+name+".custom-model-data");
        meta.setCustomModelData(modelData);
        if (isGlowing){
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.getPersistentDataContainer().set(Main.bookKey, PersistentDataType.STRING, e.getKey().toString());
        button.setItemMeta(meta);

        return button;

    }

    public ItemStack getItem(String name){
        ItemStack button;
        String type = Main.getConfigString("menus.search.buttons."+name+".type").toUpperCase();
        String materialString;
        String itemName;
        List<String> itemLore = new ArrayList<>();
        boolean isGlowing;
        if (type.equalsIgnoreCase("NEXTPAGE")){
            if (nextActive){
                materialString = Main.getConfigString("menus.search.buttons."+name+".material-active");
                itemName = ColorUtils.colorMessage(Main.getConfigString("menus.search.buttons."+name+".name-active"));
                isGlowing = getConfigBoolean("menus.search.buttons."+name+".glowing-active");
                for (String line: Main.getConfigStringList("menus.search.buttons."+name+".lore-active")){
                    itemLore.add(ColorUtils.colorMessage(line));
                }
            }
            else {
                materialString = Main.getConfigString("menus.search.buttons."+name+".material-inactive");
                itemName = ColorUtils.colorMessage(Main.getConfigString("menus.search.buttons."+name+".name-inactive"));
                isGlowing = getConfigBoolean("menus.search.buttons."+name+".glowing-inactive");
                for (String line: Main.getConfigStringList("menus.search.buttons."+name+".lore-inactive")){
                    itemLore.add(ColorUtils.colorMessage(line));
                }
            }
        }
        else if (type.equalsIgnoreCase("PREVPAGE")){
            if (prevActive){
                materialString = Main.getConfigString("menus.search.buttons."+name+".material-active");
                itemName = ColorUtils.colorMessage(Main.getConfigString("menus.search.buttons."+name+".name-active"));
                isGlowing = getConfigBoolean("menus.search.buttons."+name+".glowing-active");
                for (String line: Main.getConfigStringList("menus.search.buttons."+name+".lore-active")){
                    itemLore.add(ColorUtils.colorMessage(line));
                }
            }
            else {
                materialString = Main.getConfigString("menus.search.buttons."+name+".material-inactive");
                itemName = ColorUtils.colorMessage(Main.getConfigString("menus.search.buttons."+name+".name-inactive"));
                isGlowing = getConfigBoolean("menus.search.buttons."+name+".glowing-inactive");
                for (String line: Main.getConfigStringList("menus.search.buttons."+name+".lore-inactive")){
                    itemLore.add(ColorUtils.colorMessage(line));
                }
            }
        }
        else {
            materialString = Main.getConfigString("menus.search.buttons."+name+".material");
            itemName = ColorUtils.colorMessage(Main.getConfigString("menus.search.buttons."+name+".name"));
            isGlowing = getConfigBoolean("menus.search.buttons."+name+".glowing");
            for (String line: Main.getConfigStringList("menus.search.buttons."+name+".lore")){
                itemLore.add(ColorUtils.colorMessage(line));
            }
        }

        if (materialString.contains("head:")){
            String b64 = materialString.replace("head:","").replace(" ", "");
            button = HeadUtils.itemFromBase64(b64);
        }
        else if (materialString.contains("hdb:")){
            String id = materialString.replace("hdb:", "").replace(" ", "");
            button = new HeadDatabaseAPI().getItemHead(id);
        }
        else {
            Material itemMaterial = Material.getMaterial(materialString);
            button = new ItemStack(itemMaterial, 1);
        }

        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(itemLore);
        int modelData = Main.getConfigInt("menus.search.buttons."+name+".custom-model-data");
        meta.setCustomModelData(modelData);
        if (isGlowing){
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (type.equalsIgnoreCase("NEXTPAGE")){
            if (nextActive){
                meta.getPersistentDataContainer().set(Main.page, PersistentDataType.INTEGER, page+1);
            }
            else {
                meta.getPersistentDataContainer().set(Main.page, PersistentDataType.INTEGER, -1);
            }
        }
        else if (type.equalsIgnoreCase("PREVPAGE")){
            if (prevActive){
                meta.getPersistentDataContainer().set(Main.page, PersistentDataType.INTEGER, page-1);
            }
            else {
                meta.getPersistentDataContainer().set(Main.page, PersistentDataType.INTEGER, -1);
            }
        }

        meta.getPersistentDataContainer().set(Main.buttonKey, PersistentDataType.STRING, type);
        button.setItemMeta(meta);

        return button;

    }

}
