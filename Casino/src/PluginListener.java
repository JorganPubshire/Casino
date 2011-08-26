

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.iConomy.iConomy;


public class PluginListener extends ServerListener {
	Main plugin;
	
	public PluginListener(Main instance){
		plugin = instance;
	}

	/**
	 * Utilizes iConomy if it is enabled
	 */
	public void onPluginEnable(PluginEnableEvent event){
		if (plugin.iconomy == null) {
            Plugin iConomy = plugin.getServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if (iConomy.isEnabled() && iConomy.getClass().getName().equals("com.iConomy.iConomy")) {
                    plugin.iconomy = (iConomy)iConomy;
                    plugin.useIconomy();
                    plugin.usingIconomy = true;
                    System.out.println("[Cards] Hooked into iConomy.");
                }
            }
        }
	}
	
	/**
	 * Forces the plugin to stop using iConomy if it is disabled
	 */
	public void onPluginDisable(PluginDisableEvent event){
		if(event.getPlugin().getDescription().getName().equals("iConomy")){
			plugin.iconomy = null;
			plugin.unuseIconomy();
			plugin.usingIconomy = false;
			System.out.println("[Cards] Unhooked from iConomy!");
		}
	}
}
