import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class AntiBreakAndBuild extends BlockListener{
	Main plugin;
	Block diamond;
	
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
			return;
		}
		if(event.getBlock().getType().equals(Material.DIAMOND_BLOCK)){
			if(event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.DISPENSER)){
				event.setCancelled(true);
				return;
			}
		}
		if(event.getBlock().getRelative(BlockFace.EAST).getType().equals(Material.DIAMOND_BLOCK)){
			diamond = event.getBlock().getRelative(BlockFace.EAST);
		}
		else if(event.getBlock().getRelative(BlockFace.WEST).getType().equals(Material.DIAMOND_BLOCK)){
			diamond = event.getBlock().getRelative(BlockFace.WEST);
		}
		else if(event.getBlock().getRelative(BlockFace.NORTH).getType().equals(Material.DIAMOND_BLOCK)){
			diamond = event.getBlock().getRelative(BlockFace.NORTH);
		}
		else if(event.getBlock().getRelative(BlockFace.SOUTH).getType().equals(Material.DIAMOND_BLOCK)){
			diamond = event.getBlock().getRelative(BlockFace.SOUTH);
		}
		else if(event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.DIAMOND_BLOCK)){
			diamond = event.getBlock().getRelative(BlockFace.DOWN);
		}
		else{
			return;
		}
		if(diamond.getRelative(BlockFace.DOWN).getType().equals(Material.DISPENSER)){
			event.setCancelled(true);
			return;
		}
	}
}
