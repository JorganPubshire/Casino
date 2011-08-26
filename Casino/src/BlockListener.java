

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import couk.Adamki11s.Extras.Extras.Extras;
import couk.Adamki11s.Extras.Scheduler.ExtrasScheduler;

import Base.CardPlayer;
import Base.Slot;


public class BlockListener extends PlayerListener{
	Main plugin;
	Extras e = new Extras("Casino");
	ExtrasScheduler s = new ExtrasScheduler();
	PlayerInteractEvent ev;

	public BlockListener(Main instance){
		plugin = instance;
		s.InitialiseScheduler(plugin.getServer(), plugin.getServer().getPluginManager().getPlugin("Casino"));
	}

	public void onPlayerInteract(PlayerInteractEvent event){
		ev = event;
		if(!plugin.block){
			return;
		}
		Action action = event.getAction();
		Player player = event.getPlayer();
		CardPlayer cardplayer = plugin.blackjack.match(player);
		Block block = event.getClickedBlock();
		Sign sign = null;
		if(block == null){
			return;
		}
		if(block.getType().equals(Material.LEVER)){
//			s.scheduleAsyncDelayedTasked(100, this.getClass(), cancel(), "Cancelation");
			new Slots(block, event, plugin);
		}
		if(cardplayer == null){
			return;
		}
		if(block.getType().equals(Material.FENCE)){
			player.chat("leave");
		}
		if(block.getState()  instanceof Sign){
			sign = (Sign) block.getState();
		}
		else{return;}
		if(plugin.blackjack.getPlayers().contains(player)){
			if(!plugin.betting && plugin.turn != null && cardplayer.equals(plugin.turn)){
				if(action.equals(Action.LEFT_CLICK_BLOCK)){

					if(sign.getLine(1).equalsIgnoreCase("[HIT]")){
						player.sendMessage(ChatColor.GOLD + "hit");
						cardplayer.giveCard(plugin.blackjack.hit());
						plugin.playerTurn(cardplayer);
					}
					else if(sign.getLine(1).equalsIgnoreCase("[STAY]")){
						player.sendMessage(ChatColor.GOLD + "stay");
						plugin.blackjack.stay(cardplayer);
						plugin.game();
					}
				}
				else if(action.equals(Action.RIGHT_CLICK_BLOCK)){
					if(sign.getLine(1).equalsIgnoreCase("[HIT]")){
						int bet = plugin.blackjack.bets.get(cardplayer);
						if(cardplayer.getCash() >= bet){
							player.sendMessage(ChatColor.GOLD + "double down");
							cardplayer.takeCash(bet);
							plugin.blackjack.bets.put(cardplayer,bet*2);
							cardplayer.giveCard(plugin.blackjack.hit());
							plugin.blackjack.stay(cardplayer);
							plugin.game();
						}
						else{
							plugin.turn.getPlayer().sendMessage(ChatColor.RED + "You don't have enough money to do that!");
						}
					}
				}
			}
			else if(plugin.betting && plugin.better != null && plugin.better.equals(cardplayer)){
				if(sign.getLine(1).equalsIgnoreCase("[BET]")){
					Slot slot = plugin.blackjack.matchSlot(player);
					Sign money = slot.getSign(2);
					int bet = Integer.parseInt(money.getLine(1));
					if(bet < plugin.min){
						event.getPlayer().sendMessage(ChatColor.RED + "You must bet at least " + plugin.min + " dollar(s)");
						return;
					}
					plugin.better.getPlayer().sendMessage(ChatColor.GOLD + "You bet " + bet);
					boolean betted = plugin.bet(plugin.better,bet);
					if(betted){
						plugin.betting();
					}
					else{
						event.getPlayer().sendMessage(ChatColor.RED + "You don't have enough money for that!");
					}
				}
				else if(sign.getLine(0).equalsIgnoreCase("Current Bet:")){
					if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
						int current = Integer.parseInt(sign.getLine(1));
						if(current < cardplayer.getCash()){
							if(current + 1 > plugin.max){
								sign.setLine(1, plugin.min + "");
							}
							else{
								sign.setLine(1,current + 1 + "");
							}
							sign.update();
						}
					}
					else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
						int current = Integer.parseInt(sign.getLine(1));
						if(current >= plugin.min){
							if(current - 1 < plugin.min){
								sign.setLine(1, plugin.max + "");
							}
							else{
								sign.setLine(1,current - 1 + "");
							}
							sign.update();
						}
					}
				}
			}
		}
	}

//	private String cancel() {
//		ev.setCancelled(true);
//		return "Canceled";
//	}
}
