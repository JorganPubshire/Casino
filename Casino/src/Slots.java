
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import com.iConomy.iConomy;


public class Slots {
	iConomy i;
	Block l;
	Block origin;
	PlayerInteractEvent e;
	int payout = 0;
	int c = 0;
	int [] ores = {14,15,16,21,56,73};
	Random r1 = new Random();
	Random r2 = new Random();
	Random r3 = new Random();
	

	public Slots(Block block, PlayerInteractEvent event, Main main) {
		i = main.iconomy;
		l = block;
		e = event;
		if(l.getRelative(BlockFace.NORTH).getType().equals(Material.DIAMOND_BLOCK)){
			origin = l.getRelative(BlockFace.NORTH);
			if(origin.getRelative(BlockFace.DOWN).getType().equals(Material.DISPENSER)){
				c = 1;
			}
			else{
				c = 0;
			}
		}
		else if(l.getRelative(BlockFace.SOUTH).getType().equals(Material.DIAMOND_BLOCK)){
			origin = l.getRelative(BlockFace.SOUTH);
			if(origin.getRelative(BlockFace.DOWN).getType().equals(Material.DISPENSER)){
				c = 1;
			}
			else{
				c = 0;
			}
		}
		else if(l.getRelative(BlockFace.EAST).getType().equals(Material.DIAMOND_BLOCK)){
			origin = l.getRelative(BlockFace.EAST);
			if(origin.getRelative(BlockFace.DOWN).getType().equals(Material.DISPENSER)){
				c = 2;
			}
			else{
				c = 0;
			}
		}
		else if(l.getRelative(BlockFace.WEST).getType().equals(Material.DIAMOND_BLOCK)){
			origin = l.getRelative(BlockFace.WEST);
			if(origin.getRelative(BlockFace.DOWN).getType().equals(Material.DISPENSER)){
				c = 2;
			}
			else{
				c = 0;
			}
		}
		else{
			c = 0;
		}
		evaluate();
	}
	
	private void evaluate(){
		if(i != null){
			if(iConomy.getAccount(e.getPlayer().getName()).getHoldings().balance() < 10){
				return;
			}
			iConomy.getAccount(e.getPlayer().getName()).getHoldings().subtract(1);
		}
		switch(c){
		case 0: return;
		case 1: changeEW(); return;
		case 2: changeNS(); return;
		}
	}
	private void changeEW(){
		origin.getRelative(BlockFace.EAST).setTypeId(ores[r1.nextInt(ores.length-1)]);
		origin.getRelative(BlockFace.UP).setTypeId(ores[r2.nextInt(ores.length-1)]);
		origin.getRelative(BlockFace.WEST).setTypeId(ores[r3.nextInt(ores.length-1)]);
		if(origin.getRelative(BlockFace.EAST).getTypeId() == (origin.getRelative(BlockFace.WEST).getTypeId())){
			if(origin.getRelative(BlockFace.UP).getTypeId() == origin.getRelative(BlockFace.EAST).getTypeId()){
				switch(origin.getRelative(BlockFace.UP).getType()){
					case COAL_ORE: payout = 1; break;
					case IRON_ORE: payout = 10; break;
					case LAPIS_ORE: payout = 25; break;
					case REDSTONE_ORE: payout = 50; break;
					case GOLD_ORE: payout = 75; break;
					case DIAMOND_ORE: payout = 100; break;
					default: payout = 0; break;
				}
				e.getPlayer().sendMessage(ChatColor.GREEN + "Major win (" + payout + ")");
				iConomy.getAccount(e.getPlayer().getName()).getHoldings().add(payout);
			}
			else{
				switch(origin.getRelative(BlockFace.EAST).getType()){
				case COAL_ORE: payout = 1; break;
				case IRON_ORE: payout = 5; break;
				case LAPIS_ORE: payout = 15; break;
				case REDSTONE_ORE: payout = 25; break;
				case GOLD_ORE: payout = 30; break;
				case DIAMOND_ORE: payout = 50; break;
				default: payout = 0; break;
			}
				e.getPlayer().sendMessage(ChatColor.YELLOW + "Minor Win (" + payout + ")");
				iConomy.getAccount(e.getPlayer().getName()).getHoldings().add(payout);
			}
		}
		else{
			e.getPlayer().sendMessage(ChatColor.RED + "Lose (1)");
		}
		finish();
	}
	private void changeNS(){
		origin.getRelative(BlockFace.NORTH).setTypeId(ores[r1.nextInt(ores.length-1)]);
		origin.getRelative(BlockFace.UP).setTypeId(ores[r2.nextInt(ores.length-1)]);
		origin.getRelative(BlockFace.SOUTH).setTypeId(ores[r3.nextInt(ores.length-1)]);
		if(origin.getRelative(BlockFace.NORTH).getTypeId() == (origin.getRelative(BlockFace.SOUTH).getTypeId())){
			if(origin.getRelative(BlockFace.UP).getTypeId() == origin.getRelative(BlockFace.SOUTH).getTypeId()){
				switch(origin.getRelative(BlockFace.UP).getType()){
					case COAL_ORE: payout = 1; break;
					case IRON_ORE: payout = 10; break;
					case LAPIS_ORE: payout = 25; break;
					case REDSTONE_ORE: payout = 50; break;
					case GOLD_ORE: payout = 75; break;
					case DIAMOND_ORE: payout = 100; break;
				}
				e.getPlayer().sendMessage(ChatColor.GREEN + "Major win (" + payout + ")");
				iConomy.getAccount(e.getPlayer().getName()).getHoldings().add(payout);
			}
			else{
				switch(origin.getRelative(BlockFace.SOUTH).getType()){
				case COAL_ORE: payout = 1; break;
				case IRON_ORE: payout = 5; break;
				case LAPIS_ORE: payout = 15; break;
				case REDSTONE_ORE: payout = 25; break;
				case GOLD_ORE: payout = 30; break;
				case DIAMOND_ORE: payout = 50; break;
			}
				e.getPlayer().sendMessage(ChatColor.YELLOW + "Minor Win (" + payout + ")");
				iConomy.getAccount(e.getPlayer().getName()).getHoldings().add(payout);
			}
		}
		else{
			e.getPlayer().sendMessage(ChatColor.RED + "Lose (1)");
		}
		finish();
	}
	
	public void finish(){
		e.getPlayer().sendMessage("Your new balance is " + iConomy.format(iConomy.getAccount(e.getPlayer().getName()).getName()));
	}

}
