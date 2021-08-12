package Utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static void sendMessage(String message, Player p){

        if (message.startsWith("chat!")){
            sendChatMessage(message.replace("chat! ","").replace("chat!", ""), p);
        }
        else if (message.startsWith("title!")){
            sendTitleMessage(message.replace("title! ","").replace("title!", ""), p);
        }
        else if (message.startsWith("actionbar!")){
            sendActionbarMessage(message.replace("actionbar! ","").replace("actionbar!", ""), p);
        }

    }

    public static void sendTitleMessage(String message, Player p){
        p.sendTitle(ColorUtils.colorMessage(message), "", 20, 80, 20);
    }

    public static void sendActionbarMessage(String message, Player p){
        BaseComponent component = ComponentSerializer.parse(ColorUtils.colorBungee(message))[0];
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
        p.spigot().sendMessage(ChatMessageType.SYSTEM, new TextComponent("System"));
    }

    public static void sendChatMessage(String message, Player p){
        p.sendMessage(ColorUtils.colorMessage(message));
    }

    public static String toCheckMessage(String message){

        return ChatColor.stripColor(message).toLowerCase();

    }

}
