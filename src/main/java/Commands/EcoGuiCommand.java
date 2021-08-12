package Commands;

import Main.Main;
import Menus.MainMenu;
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
                if (sender instanceof Player){
                    Player p = (Player) sender;
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
                        MessageUtils.sendMessage(Main.getConfigString("messages.plugin-reloaded"), (Player) sender);
                    }
                    else {
                        MessageUtils.sendMessage(Main.getConfigString("messages.no-permission"), (Player) sender);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
