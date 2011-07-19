import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class AntiBreakAndBuild extends BlockListener{
	Main plugin;
	
	public AntiBreakAndBuild(Main instance){
		plugin = instance;
	}
	
	public void onBlockPlace(BlockPlaceEvent event){
		if(plugin.blackjack.getPlayers().contains(event.getPlayer())){
			event.setCancelled(true);
		}
	}
	public void onBlockBreak(BlockBreakEvent event){
		if(plugin.blackjack.getPlayers().contains(event.getPlayer()) && plugin.block){
			event.setCancelled(true);
		}
	}
}
