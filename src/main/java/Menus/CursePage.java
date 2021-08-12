package Menus;

import Main.Main;
import Utils.ColorUtils;
import Utils.HeadUtils;
import Utils.PageUtils;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static Utils.PageUtils.replaceItemVars;

public class CursePage {

    public static ArrayList<EcoEnchant> enchantments = new ArrayList<>();
    private int pageCount;
    private boolean nextActive;
    private boolean prevActive;
    private int page;
    public static final String name = "Curse";

    public static void addEnchantment(EcoEnchant e){
        enchantments.add(e);
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

        int guiSize = Main.getConfigInt("menus.page-curse.size");

        Inventory mainMenu = null;

        for (String button: Main.getConfigKeys("menus.page-curse.buttons", false)){
            List<Integer> intSlots = Main.getConfigIntList("menus.page-curse.buttons."+button+".slots");
            String type = Main.getConfigString("menus.page-curse.buttons."+button+".type");
            if (type.equalsIgnoreCase("ENCHANTMENT")){
                pageCount = intSlots.size();
                nextActive = page<getMaxPages();
                prevActive = page>1;
                String guiName = ColorUtils.colorMessage(Main.getConfigString("menus.page-curse.title")
                        .replace("{page}", Integer.toString(page))
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

        for (String button: Main.getConfigKeys("menus.page-curse.buttons", false)){
            List<Integer> intSlots = Main.getConfigIntList("menus.page-curse.buttons."+button+".slots");
            String type = Main.getConfigString("menus.page-curse.buttons."+button+".type");
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
        String materialString = Main.getConfigString("menus.page-curse.buttons."+name+".material");
        String itemName = replaceItemVars(Main.getConfigString("menus.page-curse.buttons."+name+".name"), e);
        List<String> itemLore = new ArrayList<>();
        boolean isGlowing = Main.getConfigBoolean("menus.page-curse.buttons."+name+".glowing");
        for (String line: Main.getConfigStringList("menus.page-curse.buttons."+name+".lore")){
            if (line.contains("{description}")){
                itemLore.addAll(PageUtils.buildDescription(e, line));
            }
            else if (line.contains("{apply-on}")){
                for (String s: PageUtils.wrapString(PageUtils.buildAppliements(e))){
                    itemLore.add(ColorUtils.colorMessage(line.replace("{apply-on}", s)));
                }
            }
            else if (line.contains("{conflicts}")){
                for (String s: PageUtils.wrapString(PageUtils.buildConflicts(e))){
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
        int modelData = Main.getConfigInt("menus.page-curse.buttons."+name+".custom-model-data");
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
        String type = Main.getConfigString("menus.page-curse.buttons."+name+".type").toUpperCase();
        String materialString;
        String itemName;
        List<String> itemLore = new ArrayList<>();
        boolean isGlowing;
        if (type.equalsIgnoreCase("NEXTPAGE")){
            if (nextActive){
                materialString = Main.getConfigString("menus.page-curse.buttons."+name+".material-active");
                itemName = ColorUtils.colorMessage(Main.getConfigString("menus.page-curse.buttons."+name+".name-active"));
                isGlowing = Main.getConfigBoolean("menus.page-curse.buttons."+name+".glowing-active");
                for (String line: Main.getConfigStringList("menus.page-curse.buttons."+name+".lore-active")){
                    itemLore.add(ColorUtils.colorMessage(line));
                }
            }
            else {
                materialString = Main.getConfigString("menus.page-curse.buttons."+name+".material-inactive");
                itemName = ColorUtils.colorMessage(Main.getConfigString("menus.page-curse.buttons."+name+".name-inactive"));
                isGlowing = Main.getConfigBoolean("menus.page-curse.buttons."+name+".glowing-inactive");
                for (String line: Main.getConfigStringList("menus.page-curse.buttons."+name+".lore-inactive")){
                    itemLore.add(ColorUtils.colorMessage(line));
                }
            }
        }
        else if (type.equalsIgnoreCase("PREVPAGE")){
            if (prevActive){
                materialString = Main.getConfigString("menus.page-curse.buttons."+name+".material-active");
                itemName = ColorUtils.colorMessage(Main.getConfigString("menus.page-curse.buttons."+name+".name-active"));
                isGlowing = Main.getConfigBoolean("menus.page-curse.buttons."+name+".glowing-active");
                for (String line: Main.getConfigStringList("menus.page-curse.buttons."+name+".lore-active")){
                    itemLore.add(ColorUtils.colorMessage(line));
                }
            }
            else {
                materialString = Main.getConfigString("menus.page-curse.buttons."+name+".material-inactive");
                itemName = ColorUtils.colorMessage(Main.getConfigString("menus.page-curse.buttons."+name+".name-inactive"));
                isGlowing = Main.getConfigBoolean("menus.page-curse.buttons."+name+".glowing-inactive");
                for (String line: Main.getConfigStringList("menus.page-curse.buttons."+name+".lore-inactive")){
                    itemLore.add(ColorUtils.colorMessage(line));
                }
            }
        }
        else {
            materialString = Main.getConfigString("menus.page-curse.buttons."+name+".material");
            itemName = ColorUtils.colorMessage(Main.getConfigString("menus.page-curse.buttons."+name+".name"));
            isGlowing = Main.getConfigBoolean("menus.page-curse.buttons."+name+".glowing");
            for (String line: Main.getConfigStringList("menus.page-curse.buttons."+name+".lore")){
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
        int modelData = Main.getConfigInt("menus.page-curse.buttons."+name+".custom-model-data");
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
