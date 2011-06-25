package Games.BlackJack;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import Base.Card;
import Base.Deck;
import Base.CardPlayer;

import com.iConomy.*;
import com.iConomy.system.Holdings;

public class BlackJack{
	ArrayList<CardPlayer> players = new ArrayList<CardPlayer>();
	public CardPlayer dealer = new CardPlayer(99999,"BlackJack",null);
	ArrayList<Player> playerNames = new ArrayList<Player>();
	Deck deck = new Deck();
	HashMap<CardPlayer,Integer> finals = new HashMap<CardPlayer,Integer>();
	public HashMap<CardPlayer,Integer> bets = new HashMap<CardPlayer, Integer>();
	ArrayList<CardPlayer> winners = new ArrayList<CardPlayer>();
	int goal = 0;
	ArrayList<CardPlayer> waiting;
	public boolean usingIconomy = false;
	
	public boolean containsPlayer(Player player){
		return playerNames.contains(player);
	}

	@SuppressWarnings("unchecked")
	public void prepBets(){
		waiting = (ArrayList<CardPlayer>) players.clone();
	}
	
	@SuppressWarnings("unchecked")
	public void startHand(){
		waiting = (ArrayList<CardPlayer>) players.clone();
		checkDeck();
		deal();
	}
	
	public void endHand(){
		waiting.clear();
		winners.clear();
		bets.clear();
		goal = 0;
		finals.clear();
	}
	
	public void removeWaiting(CardPlayer player){
		waiting.remove(player);
	}
	
	public Card hit(){
		Card card = deck.draw();
		changeCard(card);
		return card;
	}
	
	public void payout(){
		for(CardPlayer player : winners){
			player.giveCash(bets.get(player)*2);
		}
	}
	
	public void stay(CardPlayer player){
		int sum = 0;
		for(Card card : player.getHand()){
			sum += card.getValue();
		}
		sum += aceBonus(player);
		if(sum == 21 && player.getHand().size() == 2){
			finals.put(player,22);
			bets.put(player, bets.get(player)*2);
		}
		if(sum <= 21 && player.getHand().size() == 5){
			finals.put(player, 22);
		}
		if(sum > 21){
			sum = -1;
		}
		finals.put(player,sum);
	}
	
	public boolean addPlayer(Player player){
		if(!playerNames.contains(player)){
			boolean added = addPlayer(new CardPlayer(100,"BlackJack",player));
			if(!added){
				player.sendMessage("Sorry, there is no room in that game for you.");
				return false;
			}
			else{
				playerNames.add(player);
				return true;
			}
		}
		else{
			player.sendMessage(ChatColor.RED + "You are already in that game");
			return false;
		}
	}
	
	public boolean addPlayer(Player player,iConomy iconomy){
		if(!playerNames.contains(player)){
			Holdings money = iConomy.getAccount(player.getName()).getHoldings();
			if(money.balance() == 0){
				player.sendMessage(ChatColor.RED + "You don't have any money!");
				return false;
			}
			boolean added = addPlayer(new CardPlayer((int) money.balance(),"BlackJack",player));
			if(!added){
				player.sendMessage("Sorry, there is no room in that game for you.");
				return false;
			}
			else{
				playerNames.add(player);
				return true;
			}
		}
		else{
			player.sendMessage(ChatColor.RED + "You are already in that game");
			return false;
		}
	}
	
	public boolean addPlayer(CardPlayer name){
		if(!players.contains(name)&&players.size() < 5){
			players.add(name);
			return true;
		}
		else{
			return false;
		}
	}
	
	public void removePlayer(CardPlayer name){
		if(players.contains(name)){
			players.remove(name);
			playerNames.remove(name.getPlayer());
		}
		if(usingIconomy){
			Holdings money = iConomy.getAccount(name.getPlayer().getName()).getHoldings();
			money.set(name.getCash());
		}
	}
	
	public void checkDeck(){
		if(deck.size() < (players.size()+1)*4){
			deck = new Deck();
		}
	}
	public void changeCard(Card card){
		if(card.getValue()>10){
			card.setValue(10);
		}
	}
	
	public void deal(){
		for(CardPlayer player : players){
			ArrayList<Card> temp = deck.draw(2);
			for(Card card : temp){
				changeCard(card);
			}
			player.setHand(temp);
		}
		ArrayList<Card> temp = deck.draw(2);
		for(Card card : temp){
			changeCard(card);
		}
		dealer.setHand(temp);
	}
	
	public void dealerTurn(){
		int sum = 0;
		for(Card card : dealer.getHand()){
			sum += card.getValue();
		}
		while(sum < 17){
			Card card = hit();
			changeCard(card);
			dealer.hand.add(card);
			sum += card.getValue();
		}
		if(sum > 21){
			sum = 0;
		}
		goal = sum;
		
		for(CardPlayer player : finals.keySet()){
			if (finals.get(player) > goal){
				winners.add(player);
			}
		}
	}
		
	public CardPlayer next(){
		if(waiting.size() == 0){
			return null;
		}
		return waiting.get(0);
	}
	
	public ArrayList<Player> getPlayers(){
		ArrayList<Player> toReturn = new ArrayList<Player>();
		for(CardPlayer player : players){
			toReturn.add(player.getPlayer());
		}
		return toReturn;
	}
	
	public ArrayList<CardPlayer> getCardPlayers(){
		return players;
	}
	
	public boolean validate(CardPlayer player){
		int sum = 0;
		for(Card card : player.getHand()){
			sum += card.getValue();
		}
		sum += aceBonus(player);
		if(sum == 21 && player.getHand().size() == 2){
			stay(player);
			player.getPlayer().sendMessage("Winner winner, Chicken dinner! You got a blackjack!");
			return false;
		}
		if(sum <= 21 && player.getHand().size() == 5){
			stay(player);
		}
		if(sum > 21){
			stay(player);
			player.getPlayer().sendMessage(ChatColor.RED + "BUST!");
			return false;
		}
		else{
			tellPlayer(player);
			return true;
		}
	}
	public boolean isNotBusted(CardPlayer player){
		int sum = 0;
		for(Card card : player.getHand()){
			sum += card.getValue();
		}
		sum += aceBonus(player);
		if(sum > 21){
			stay(player);
			return false;
		}
		else{
			return true;
		}
	}
	
	public void tellPlayer(CardPlayer player){
		String string = "You have ";
		int sum = 0;
		for(Card card : player.getHand()){
			string += card.toString() + ", ";
			sum += card.getValue();
		}
		sum += aceBonus(player);
		player.getPlayer().sendMessage(ChatColor.GREEN + string + " (" + sum + ")");
	}
	
	public void finish(){
		for(CardPlayer player : finals.keySet()){
			String string = "You ";
			if(isNotBusted(player)){
				int sum = 0;
				for(Card card : player.getHand()){
					sum += card.getValue();
				}
				sum += aceBonus(player);
				if(player.getHand().size() == 5){
					string += "hit out, the dealer ";
				}
				else{					
					string += "got a " + sum + ", the dealer ";
				}
			}
			else {
				string += "busted, the dealer "; 
			}
			if(goal == 0){
				string += "busted. ";
			}
			else {
				int sum = 0;
				for(Card card : dealer.getHand()){
					sum += card.getValue();
				}
				string += "got a " + sum + ". ";
			}
			if(winners.contains(player)){
				string += ChatColor.GREEN + "You win! (" + bets.get(player) + ")";
			}
			else {
				string += ChatColor.RED + "You lose! (" + bets.get(player) + ")";
			}
			player.getPlayer().sendMessage(string);
			player.getPlayer().sendMessage("");
		}
	}
	
	public int aceBonus(CardPlayer player){
		int sum = 0;
		boolean ace = false;
		for(Card card : player.getHand()){
			sum += card.getValue();
			if(card.getValue() == 1){
				ace = true;
			}
		}
		if(ace){
			if(sum + 10 <= 21)
				return 10;
		}
		return 0;
	}
	
	public boolean verifyBet(CardPlayer player, int bet){
		int money = player.getCash();
		if(bet <= money){
			bets.put(player, bet);
			player.setCash(money-bet);
			return true;
		}
		return false;
	}
	
	public CardPlayer match(Player player){
		for(CardPlayer cPlayer : players){
			if(cPlayer.getPlayer().equals(player)){
				return cPlayer;
			}
		}
		return null;
	}
}
