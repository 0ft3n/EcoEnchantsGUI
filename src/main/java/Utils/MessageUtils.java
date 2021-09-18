package Utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {



    public static void sendMessage(String message, CommandSender p){

        if (!(p instanceof Player player)){
            p.sendMessage(ColorUtils.colorMessage(message
                    .replace("chat! ","").replace("chat!", "")
                    .replace("title! ","").replace("title!", "")
                    .replace("actionbar! ","").replace("actionbar!", "")
            ));
        }
        else {
            if (message.startsWith("chat!")){
                sendChatMessage(message.replace("chat! ","").replace("chat!", ""), player);
            }
            else if (message.startsWith("title!")){
                sendTitleMessage(message.replace("title! ","").replace("title!", ""), player);
            }
            else if (message.startsWith("actionbar!")){
                sendActionbarMessage(message.replace("actionbar! ","").replace("actionbar!", ""), player);
            }
            else {
                sendChatMessage(message.replace("chat! ","").replace("chat!", ""), player);
            }
        }

    }

    public static void sendTitleMessage(String message, Player p){
        p.sendTitle(ColorUtils.colorMessage(message), "", 20, 80, 20);
    }

    public static void sendActionbarMessage(String message, Player p){
        BaseComponent component = ComponentSerializer.parse(ColorUtils.colorBungee(message))[0];
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    public static void sendChatMessage(String message, Player p){
        p.sendMessage(ColorUtils.colorMessage(message));
    }

    public static String toCheckMessage(String message){

        return ChatColor.stripColor(message).toLowerCase();

    }

}
