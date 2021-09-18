package Menus;

import Main.Main;
import Utils.ColorUtils;
import Utils.HeadUtils;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {

    public static final String name = "Main";

    public Inventory getMenu(){

        String guiName = ColorUtils.colorMessage(Main.getConfigString("menus.main.title"));

        int guiSize = Main.getConfigInt("menus.main.size");

        Inventory mainMenu = Bukkit.createInventory(null, guiSize, guiName);

        for (String button: Main.getConfigKeys("menus.main.buttons", false)){
            List<Integer> intSlots = Main.getConfigIntList("menus.main.buttons."+button+".slots");
            for (int i: intSlots){
                mainMenu.setItem(i, getItem(button));
            }
        }

        return mainMenu;

    }

    public ItemStack getItem(String name){

        ItemStack button;
        String materialString = Main.getConfigString("menus.main.buttons."+name+".material");
        String itemName = ColorUtils.colorMessage(Main.getConfigString("menus.main.buttons."+name+".name"));
        List<String> itemLore = new ArrayList<>();
        Boolean isGlowing = Main.getConfigBoolean("menus.main.buttons."+name+".glowing");
        String type = Main.getConfigString("menus.main.buttons."+name+".type").toUpperCase();
        for (String line: Main.getConfigStringList("menus.main.buttons."+name+".lore")){
            itemLore.add(ColorUtils.colorMessage(line));
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
        int modelData = Main.getConfigInt("menus.main.buttons."+name+".custom-model-data");
        meta.setCustomModelData(modelData);
        if (isGlowing){
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.getPersistentDataContainer().set(Main.buttonKey, PersistentDataType.STRING, type);
        button.setItemMeta(meta);

        return button;

    }

}
