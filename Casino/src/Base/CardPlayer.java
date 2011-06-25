package Base;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class CardPlayer {
	public ArrayList<Card> hand = new ArrayList<Card>();
	int cash = 0;
	String game = "";
	Player player;
	
	public  CardPlayer(int money, String cardgame, Player $player){
		player = $player;
		cash = money;
		game = cardgame;
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
}
