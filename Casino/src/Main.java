import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.iConomy.iConomy;
import Base.CardPlayer;
import Games.BlackJack.*;

public class Main extends JavaPlugin{
	BlackJack blackjack = new BlackJack();
	CardPlayer turn;
	CardPlayer better;
	ArrayList<Player> joinQueue = new ArrayList<Player>();
	ArrayList<CardPlayer> leaveQueue = new ArrayList<CardPlayer>();
	ArrayList<Player> joined = new ArrayList<Player>();
	boolean betting = false;
	PluginManager pm;
	iConomy iconomy = null;
	boolean usingIconomy = false;


	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable() {
		pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, new ChatListener(this), Priority.Normal, this);
		pm.registerEvent(Event.Type.PLUGIN_DISABLE, new PluginListener(this), Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, new PluginListener(this), Priority.Monitor, this);
		System.out.println("Cards is working properly!");
		
	}

	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String [] args){
		Player player = (Player) sender;
		if(cmdLabel.equalsIgnoreCase("blackjack")){
			if(blackjack.getPlayers().contains(player)){
				player.sendMessage(ChatColor.RED + "You are already in that game!");
				return true;
			}
			if(blackjack.getPlayers().size()>0){
				joinQueue.add(player);
				player.sendMessage("There is a game in progress, you will be added to the game at the end of the next hand.");
			}
			else{
				addPlayer(player);
			}

		}
		return true;
	}

	public void useIconomy(){
		usingIconomy = true;
		blackjack.usingIconomy = true;
	}
	
	public void unuseIconomy(){
		usingIconomy = false;
		blackjack.usingIconomy = false;
	}
	
	public void addPlayer(Player player){
		boolean allow;
		if(usingIconomy){
			allow = blackjack.addPlayer(player,iconomy);			
		}
		else {
			allow = blackjack.addPlayer(player);
		}
		if(allow){
			player.sendMessage("You have been added to the game.");
			prepBets();
			betting();
		}
	}

	public void addPlayers(){
		for(Player player : joinQueue){
			boolean allow;
			if(usingIconomy){
				allow = blackjack.addPlayer(player,iconomy);
			}
			else{
				allow = blackjack.addPlayer(player);
			}
			if(allow){
				joined.add(player);
				player.sendMessage("You have been added to the game.");
			}
		}
		for(Player player : joined){
			joinQueue.remove(player);
		}
		joined.clear();
	}

	public void removePlayers(){
		for(CardPlayer player : leaveQueue){
			blackjack.removePlayer(player);
		}
	}

	public void prepBets(){
		blackjack.prepBets();
	}

	public void betting(){
		betting = true;
		better = blackjack.next();
		if(better == null){
			betting = false;
			startGame();
			return;
		}
		blackjack.removeWaiting(better);
		better.getPlayer().sendMessage(ChatColor.YELLOW + "Please place your bet. (say \"bet <amount>\")");
	}

	public boolean bet(CardPlayer player, int bet){
		if(player.getCash() >= bet){
			player.takeCash(bet);
			blackjack.bets.put(player, bet);
			return true;
		}
		else{
			return false;
		}
	}

	public void startGame(){
		blackjack.startHand();
		game();
	}

	public void game(){
		turn = blackjack.next();
		if(turn == null){
			blackjack.dealerTurn();
			endGame();
			return;
		}
		blackjack.removeWaiting(turn);
		turn.getPlayer().sendMessage(ChatColor.GREEN + "It is now your turn. Please say \"hit\" or \"stay\".");
		turn.getPlayer().sendMessage(ChatColor.YELLOW + "The dealer has " + blackjack.dealer.getHand().get(0) + " showing." + " ("+ (blackjack.dealer.getHand().get(0).getValue() + blackjack.aceBonus(blackjack.dealer)) + ")");
		playerTurn(turn);
	}

	public void endGame(){
		blackjack.payout();
		blackjack.finish();
		blackjack.endHand();
		restart();
	}

	public void restart(){
		removeBroke();
		removePlayers();
		addPlayers();
		if(blackjack.getPlayers().size()>0){
			prepBets();
			betting();
		}
		else{
			System.out.println("NO PLAYERS");
		}
	}

	public void playerTurn(CardPlayer player){
		boolean good = blackjack.validate(player);
		if(!good){
			game();
		}
	}
	
	public void removeBroke(){
		for(CardPlayer player : blackjack.getCardPlayers()){
			if(player.getCash() == 0){
				leaveQueue.add(player);
				player.getPlayer().sendMessage(ChatColor.RED + "You have no money and are being removed from the game!");
			}
		}
	}

}
