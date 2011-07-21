import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;


public class AntiLogout extends PlayerListener{
	Main plugin;
	
	public AntiLogout(Main instance){
		plugin = instance;
	}
	
	public void onPlayerQuit(PlayerQuitEvent event){
		if(plugin.blackjack.getPlayers().contains(event.getPlayer())){
			event.getPlayer().chat("leave");
		}
	}
}
