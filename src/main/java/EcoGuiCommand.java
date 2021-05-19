import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

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
                if(sender instanceof Player){
                    ((Player) sender).openInventory(m.mainMenu());
                    m.players.add((Player) sender);
                    return true;
                }
            }

            else if (args.length == 1){
                if (args[0].equalsIgnoreCase("reload")){
                    if (sender.hasPermission("ecogui.reload")){
                        m.reloadConfig();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(m.getConfig().getString("translations.messages.reload-success"))));
                    }
                    else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(m.getConfig().getString("translations.messages.no-perm"))));
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
