package Main;

import Commands.EcoGuiCommand;
import Commands.TabComplete;
import Menus.*;
import NewEvents.*;
import Utils.ColorUtils;
import Utils.Metrics;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public class Main extends JavaPlugin implements Listener {

    public static NamespacedKey buttonKey;
    public static NamespacedKey bookKey;
    public static NamespacedKey ecoGuiMenu;
    public static NamespacedKey page;
    public static NamespacedKey search;
    public static FileConfiguration config;
    public static HeadDatabaseAPI headApi;
    private static Main instance;
    private static Metrics metrics;
    private static final int bstatsId = 12145;

    @Override
    public void onEnable(){
        printASCII();
        this.saveDefaultConfig();
        config = getConfig();
        loadEnchantments();
        initCommands();
        initKeys();
        initListeners();
        initLangs();
        initMetrics();
        instance = this;
    }

    @Override
    public void onDisable(){
        for (Player p: getServer().getOnlinePlayers()){
            if (p.getPersistentDataContainer().has(ecoGuiMenu, PersistentDataType.STRING)){
                p.getPersistentDataContainer().remove(ecoGuiMenu);
                p.closeInventory();
            }
        }
    }

    public void reloadConfiguration(){
        reloadConfig();
        config = getConfig();
    }

    public static Main getInstance(){
        return instance;
    }

    public static String getConfigString(String path){
        return config.getString(path);
    }

    public static int getConfigInt(String path){
        return config.getInt(path);
    }

    public static List<String> getConfigStringList(String path){
        return config.getStringList(path);
    }

    public static boolean getConfigBoolean(String path){
        return config.getBoolean(path);
    }

    public static Set<String> getConfigKeys(String path, boolean deep){
        return config.getConfigurationSection(path).getKeys(deep);
    }

    public static List<Integer> getConfigIntList(String path){
        return config.getIntegerList(path);
    }

    public void initMetrics(){
        metrics = new Metrics(this, bstatsId);
    }

    public void initKeys(){
        buttonKey = new NamespacedKey(this, "ecoGuiButton");
        bookKey = new NamespacedKey(this, "ecoGuiBook");
        ecoGuiMenu = new NamespacedKey(this, "ecoGuiMenu");
        page = new NamespacedKey(this, "ecoGuiPage");
        search = new NamespacedKey(this, "ecoGuiSearch");
    }

    public void initLangs(){
        File file = new File(getDataFolder(),"config_ru.yml");
        if (!file.exists()){
            saveResource("config_ru.yml", false);
            getLogger().info("Created russian translation config");
        }
        file = new File(getDataFolder(),"config_zh.yml");
        if (!file.exists()){
            saveResource("config_zh.yml", false);
            getLogger().info("Created chinese translation config");
        }
        file = new File(getDataFolder(),"config_zh_simplified.yml");
        if (!file.exists()){
            saveResource("config_zh_simplified.yml", false);
            getLogger().info("Created simplified chinese translation config");
        }
    }

    public void loadEnchantments(){
        boolean ignore = getConfigBoolean("settings.ignore-disabled-enchantments");
        NormalPage.enchantments.clear();
        CursePage.enchantments.clear();
        SearchPage.awaiting.clear();
        SpellPage.enchantments.clear();
        SpecialPage.enchantments.clear();
        ArtifactPage.enchantments.clear();
        for (EcoEnchant e: EcoEnchants.values()){
            if (!ignore || e.isEnabled()){
                switch (e.getType().getName().toLowerCase()){
                    case("normal"):
                        NormalPage.addEnchantment(e);
                        break;
                    case("curse"):
                        CursePage.addEnchantment(e);
                        break;
                    case("special"):
                        SpecialPage.addEnchantment(e);
                        break;
                    case("artifact"):
                        ArtifactPage.addEnchantment(e);
                        break;
                    case("spell"):
                        SpellPage.addEnchantment(e);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void initListeners(){
        getServer().getPluginManager().registerEvents(new MainMenuClickEvent(), this);
        getServer().getPluginManager().registerEvents(new NormalPageClickEvent(), this);
        getServer().getPluginManager().registerEvents(new CursePageClickEvent(), this);
        getServer().getPluginManager().registerEvents(new SpecialPageClickEvent(), this);
        getServer().getPluginManager().registerEvents(new ArtifactPageClickEvent(), this);
        getServer().getPluginManager().registerEvents(new SpellPageClickEvent(), this);
        getServer().getPluginManager().registerEvents(new GuiCloseEvent(), this);
        getServer().getPluginManager().registerEvents(new SearchPageClickEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatSearchEvent(), this);
        getServer().getPluginManager().registerEvents(this, this);
        if (getServer().getPluginManager().getPlugin("HeadDatabase") != null){
            getServer().getPluginManager().registerEvents(new DatabaseLoadEvent(), this);
        }
        if (getConfigBoolean("settings.allow-taking-books-from-menu")){
            getServer().getPluginManager().registerEvents(new BookTakeEvent(), this);
            getServer().getPluginManager().registerEvents(new BookDragEvent(), this);
        }

    }

    public void initCommands(){

        getCommand("ecogui").setExecutor(new EcoGuiCommand(this));
        getCommand("ecogui").setTabCompleter(new TabComplete());

    }

    public void printASCII(){

        getLogger().info(ColorUtils.colorMessage(" &a ______          ______            _                 _        &2_____ _    _ _____ "));
        getLogger().info(ColorUtils.colorMessage(" &a|  ____|        |  ____|          | |               | |      &2/ ____| |  | |_   _|"));
        getLogger().info(ColorUtils.colorMessage(" &a| |__   ___ ___ | |__   _ __   ___| |__   __ _ _ __ | |_ ___&2| &2|  __| |  | | | |  "));
        getLogger().info(ColorUtils.colorMessage(" &a|  __| / __/ _ \\|  __| | '_ \\ / __| '_ \\ / _` | '_ \\| __/ __&2| | |_ | |  | | | |  "));
        getLogger().info(ColorUtils.colorMessage(" &a| |___| (_| (_) | |____| | | | (__| | | | (_| | | | | |_\\__ \\ &2|__| | |__| |_| |_ "));
        getLogger().info(ColorUtils.colorMessage(" &a|______\\___\\___/|______|_| |_|\\___|_| |_|\\__,_|_| |_|\\__|___/&2\\_____|\\____/|_____|"));
        getLogger().info(ColorUtils.colorMessage("                                                                                  "));
        getLogger().info(ColorUtils.colorMessage("                                                                &6by _OfTeN_        "));
        getLogger().info(ColorUtils.colorMessage("                                                                                  "));

    }

}
