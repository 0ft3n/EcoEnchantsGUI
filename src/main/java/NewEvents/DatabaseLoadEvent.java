package NewEvents;

import Main.Main;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DatabaseLoadEvent implements Listener {

    @EventHandler
    public void onDatabaseLoad(me.arcaniax.hdb.api.DatabaseLoadEvent e) {
        Main.headApi = new HeadDatabaseAPI();
    }

}
