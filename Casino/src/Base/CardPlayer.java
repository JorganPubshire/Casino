package Base;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.iConomy.iConomy;

/**
 * A CardPlayer object to hold a player's card game data
 * 
 * @author JorganPubshire
 *
 */
public class CardPlayer {
	public ArrayList<Card> hand = new ArrayList<Card>();
	int cash = 0;
	String game = "";
	Player player;
	Location loc;
	double initialAmt = 0;

	public  CardPlayer(int money, String cardgame, Player $player){
		player = $player;
		cash = money;
		game = cardgame;
		if(player != null){
			loc = player.getLocation();
			initialAmt = iConomy.getAccount(player.getName()).getHoldings().balance();
		}
	}

	/*
	 * getter and setter methods
	 */
	public Location getOrigin(){
		return loc;
	}

	public int getCash(){
		return cash;
	}

	public void setCash(int amt){
		cash = amt;
	}

	public String getGame(){
		return game;
	}

	public Player getPlayer(){
		return player;
	}

	public ArrayList<Card> getHand(){
		return hand;
	}

	public void clearHand(){
		hand.clear();
	}

	public void setHand(ArrayList<Card> cards){
		hand = cards;
	}

	public void giveCard(Card card){
		hand.add(card);
	}

	public void giveCash(int money){
		cash += money;
	}

	public void takeCash(int money) {
		cash -= money;
	}
	
	public double getInitial(){
		return initialAmt;
	}
}
