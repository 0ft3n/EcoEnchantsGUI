package Commands;

import Main.Main;
import Menus.MainMenu;
import Menus.SearchPage;
import Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class EcoGuiCommand implements CommandExecutor {

    private Main m;

    public EcoGuiCommand(Main m){
        super();
        this.m = m;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("ecogui")){

            if (args.length == 0){
                if (sender instanceof Player p){
                    p.openInventory(new MainMenu().getMenu());
                    p.getPersistentDataContainer().set(Main.ecoGuiMenu, PersistentDataType.STRING, MainMenu.name);
                    return true;
                }
            }

            else if (args.length == 1){
                if (args[0].equalsIgnoreCase("reload")){
                    if (sender.hasPermission("ecogui.reload")){
                        Main.getInstance().reloadConfig();
                        Main.getInstance().onDisable();
                        Main.getInstance().onEnable();
                        MessageUtils.sendMessage(Main.getConfigString("messages.plugin-reloaded"), sender);
                    }
                    else {
                        MessageUtils.sendMessage(Main.getConfigString("messages.no-permission"),  sender);
                    }
                    return true;
                }
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("search")){
                    if (sender.hasPermission("ecogui.search") && sender instanceof Player player){
                        player.openInventory(new SearchPage(args[1]).getMenu(1));
                        player.getPersistentDataContainer().set(Main.ecoGuiMenu, PersistentDataType.STRING, SearchPage.name);
                        player.getPersistentDataContainer().set(Main.search, PersistentDataType.STRING, args[1]);
                    }
                    else {
                        MessageUtils.sendMessage(Main.getConfigString("messages.no-permission"),  sender);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
