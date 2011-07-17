package Games.BlackJack;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import Base.Card;
import Base.Deck;
import Base.CardPlayer;
import Base.Slot;

import com.iConomy.*;
import com.iConomy.system.Holdings;

/**
 * 
 * @author JorganPubshire
 *
 *
 * The center of the Blackjack game; holds players, deck information, and major functions
 */
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
	public Slot[] slots = new Slot[5];
	public HashMap<Slot, CardPlayer> slotStatus = new HashMap<Slot, CardPlayer>();

	/**
	 * Loads the locations from the config file into the game
	 */
	public void init(){
		slotStatus.clear();
		for(Slot loc : slots){
			slotStatus.put(loc, null);
		}
	}

	/**
	 * Checks if a player is playing blackjack
	 * @param player
	 */
	public boolean containsPlayer(Player player){
		return playerNames.contains(player);
	}

	@SuppressWarnings("unchecked")
	/**
	 * creates a list of players that need to place bets
	 */
	public void prepBets(){
		waiting = (ArrayList<CardPlayer>) players.clone();
	}

	@SuppressWarnings("unchecked")
	/**
	 * Performs pre-game setup
	 */
	public void startHand(){
		clearSigns();
		//creates turn list
		waiting = (ArrayList<CardPlayer>) players.clone();
		//checks if there is enough cards for everyone
		checkDeck();
		//gives players cards
		deal();
	}

	public void clearSigns() {
		for(Slot slot:slots){
			slot.clearSigns();
		}
	}

	/**
	 * Resets values after each hand
	 */
	public void endHand(){
		waiting.clear();
		winners.clear();
		bets.clear();
		goal = 0;
		finals.clear();
	}

	/**
	 * Removes a player from the turn list
	 * @param player
	 */
	public void removeWaiting(CardPlayer player){
		waiting.remove(player);
	}

	/**
	 * Draws one card from the deck
	 * @return
	 */
	public Card hit(){
		Card card = deck.draw();
		changeCard(card);
		return card;
	}

	/**
	 * Pays winning players
	 */
	public void payout(){
		for(CardPlayer player : winners){
			if(players.contains(player)){
				player.giveCash(bets.get(player)*2);
			}
		}
	}

	/**
	 * Ends the player's turn
	 * @param player
	 */
	public void stay(CardPlayer player){
		int sum = 0;
		//totals the player's score
		for(Card card : player.getHand()){
			sum += card.getValue();
		}
		sum += aceBonus(player);
		//checks for instant blackjack (100% win)
		if(sum == 21 && player.getHand().size() == 2){
			finals.put(player,22);
			bets.put(player, bets.get(player)*2);
			return;
		}
		//checks for a hit out (100% win)
		if(sum <= 21 && player.getHand().size() == 5){
			finals.put(player, 22);
			return;
		}
		//checks for bust
		if(sum > 21){
			sum = -1;
		}
		//saves player's score
		finals.put(player,sum);
	}

	/**
	 * Adds a player to the game
	 * 
	 * @param player
	 * @return
	 */
	public boolean addPlayer(Player player){
		//checks if the player is already playing
		if(!playerNames.contains(player)){
			//creates a new player
			boolean added = addPlayer(new CardPlayer(100,"BlackJack",player));
			//the game is full
			if(!added){
				player.sendMessage("Sorry, there is no room in that game for you.");
				return false;
			}
			//the game has room
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

	/**
	 * Adds a player to the game and uses their iConomy holdings 
	 * 
	 * @param player
	 * @param iconomy
	 * @return
	 */
	public boolean addPlayer(Player player,iConomy iconomy){
		//checks if the player is in the game already
		if(!playerNames.contains(player)){
			//gets the player's money
			Holdings money = iConomy.getAccount(player.getName()).getHoldings();
			//checks if the player is broke
			if(money.balance() == 0){
				player.sendMessage(ChatColor.RED + "You don't have any money!");

				return false;
			}
			//adds the player to the game
			boolean added = addPlayer(new CardPlayer((int) money.balance(),"BlackJack",player));
			//player not added
			if(!added){
				player.sendMessage("Sorry, there is no room in that game for you.");
				return false;
			}
			//player added
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

	/**
	 * fully integrates the player into the game and physically moves them to the blackjack area
	 * 
	 * @param name
	 * @return
	 */
	public boolean addPlayer(CardPlayer name){
		//checks if the player is already in the game and if there is room for them
		if(!players.contains(name)&&players.size() < 5){
			//adds the player
			players.add(name);
			//moves the player
			tpIn(name);
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Moves the player to an available blackjack slot
	 * 
	 * @param player
	 */
	public void tpIn(CardPlayer player){
		//checks for open slots
		for(Slot loc : slots){
			//checks if a slot is occupied
			if(slotStatus.get(loc) == null){
				//places the player in the slot
				player.getPlayer().teleport(loc.getLoc());
				slotStatus.put(loc, player);
				return;
			}
		}
		player.getPlayer().sendMessage(ChatColor.RED + "No open slots!");
	}

	/**
	 * Moves the player out of the blackjack area
	 * 
	 * @param player
	 */
	public void tpOut(CardPlayer player){
		if(player.getOrigin() == null)
			return;
		//checks if the player is in a slot
		if(slotStatus.containsValue(player)){
			//finds which slot the player is in
			for(Slot loc : slots){
				if(slotStatus.get(loc) != null && slotStatus.get(loc).equals(player)){
					//removes the player from the slot
					slotStatus.put(loc, null);
					//returns the player to their original location
					player.getPlayer().teleport(player.getOrigin());
					return;
				}
			}
		}
	}

	/**
	 * Moves the player out of the blackjack area
	 * 
	 * @param player
	 */
	public void tpOutNow(CardPlayer player){
		//checks if the player is in a slot
		if(slotStatus.containsValue(player)){
			//finds which slot the player is in
			for(Slot loc : slots){
				if(slotStatus.get(loc) != null && slotStatus.get(loc).equals(player)){
					//removes the player from the slot
					slotStatus.put(loc, null);
					//returns the player to their original location
					player.getPlayer().teleport(player.getOrigin());
					player.setOrigin(null);
					return;
				}
			}
		}
	}

	/**
	 * Removes the player from the game
	 * 
	 * @param name
	 */
	public void removePlayer(CardPlayer name){
		//checks if the player is in the game
		if(players.contains(name)){
			//removes the player from the game
			players.remove(name);
			playerNames.remove(name.getPlayer());
			//moves player out of blackjack area
			tpOut(name);
		}
		//awards players with their earnings if iConomy is active
		if(usingIconomy){
			Holdings money = iConomy.getAccount(name.getPlayer().getName()).getHoldings();
			money.add(name.getCash()-name.getInitial());
		}
	}

	/**
	 * Checks if there is enough cards left in the deck
	 */
	public void checkDeck(){
		if(deck.size() < (players.size()+1)*4){
			deck = new Deck();
		}
	}

	/**
	 * Reduces face card values to ten
	 * 
	 * @param card
	 * @return 
	 */
	public Card changeCard(Card card){
		if(card.getValue()>10){
			card.setValue(10);
		}
		return card;
	}

	/**
	 * gives two cards to each player and the dealer
	 */
	public void deal(){
		//deals to players
		for(CardPlayer player : players){
			ArrayList<Card> temp = deck.draw(2);
			for(Card card : temp){
				changeCard(card);
			}
			player.setHand(temp);
		}
		ArrayList<Card> temp = deck.draw(2);
		//deals to dealer
		for(Card card : temp){
			changeCard(card);
		}
		dealer.setHand(temp);
	}

	/**
	 * Automates the dealer's turn
	 * The dealer always hits when they have less than 17
	 * The dealer wins ties (except for instant blackjack and hit out)
	 */
	public void dealerTurn(){
		int sum = 0;
		//calculates dealer's score
		for(Card card : dealer.getHand()){
			sum += card.getValue();
		}
		sum+=aceBonus(dealer);
		//the dealer hits until he has 17 or more
		while(sum < 17){
			Card card = hit();
			changeCard(card);
			dealer.hand.add(card);
			sum += card.getValue();
		}
		//dealer busts
		if(sum > 21){
			sum = 0;
		}
		goal = sum;
		//lists all players that beat the dealer
		for(CardPlayer player : finals.keySet()){
			if (finals.get(player) > goal){
				winners.add(player);
			}
		}
	}

	/**
	 * Progresses the betting/turn to the next player
	 * 
	 * @return
	 */
	public CardPlayer next(){
		if(waiting.size() == 0){
			return null;
		}
		return waiting.get(0);
	}

	/**
	 * returns a list of the players in the game
	 * 
	 * @return
	 */
	public ArrayList<Player> getPlayers(){
		return playerNames;
	}

	/**
	 * returns a list of the players in the game
	 * 
	 * @return
	 */
	public ArrayList<CardPlayer> getCardPlayers(){
		return players;
	}

	/**
	 * Checks the player's cards for busting, blackjack, and hit out
	 * 
	 * @param player
	 * @return
	 */
	public boolean validate(CardPlayer player){
		int sum = 0;
		//calculates the player's score
		for(Card card : player.getHand()){
			sum += card.getValue();
		}
		sum += aceBonus(player);
		//informs the player of their cards and current score
		tellPlayer(player);
		//instant blackjack
		if(sum == 21 && player.getHand().size() == 2){
			stay(player);
			player.getPlayer().sendMessage("Winner winner, Chicken dinner! You got a blackjack!");
			return false;
		}
		//hit out
		if(sum <= 21 && player.getHand().size() == 5){
			stay(player);
		}
		//bust
		if(sum > 21){
			stay(player);
			player.getPlayer().sendMessage(ChatColor.RED + "BUST!");
			return false;
		}
		else{
			return true;
		}
	}

	/**
	 * Checks if the player is busted
	 */
	public boolean isNotBusted(CardPlayer player){
		int sum = 0;
		//totals player's score
		for(Card card : player.getHand()){
			sum += card.getValue();
		}
		sum += aceBonus(player);
		//bust
		if(sum > 21){
			stay(player);
			return false;
		}
		else{
			return true;
		}
	}

	/**
	 * Tells the player what cards they have and their current score
	 * 
	 * @param player
	 */
	public void tellPlayer(CardPlayer player){
		String string = "You have ";
		int sum = 0;
		int line = 0;
		Slot slot = null;
		for(Slot s : slotStatus.keySet()){
			if(slotStatus.get(s)!=null && slotStatus.get(s).equals(player)){
				slot = s;
				break;
			}
		}
		// lists cards and totals score
		for(Card card : player.getHand()){
			string += card.toString() + ", ";
			sum += card.getValue();
			if(slot!=null){
				slot.write(line,card.toString());
				line++;
			}
		}
		sum += aceBonus(player);
		ChatColor color;
		if(sum <= 21){
			color = ChatColor.GREEN;
		}
		else{
			color = ChatColor.RED;
		}
		player.getPlayer().sendMessage(ChatColor.GREEN + string + color + " (" + sum + ")");
	}

	/**
	 * Informs each player of their score, the dealer's score, and their winnings/losses for the hand
	 */
	public void finish(){
		for(CardPlayer player : finals.keySet()){
			String string = "You ";
			if(isNotBusted(player)){
				int sum = 0;
				for(Card card : player.getHand()){
					sum += card.getValue();
				}
				sum += aceBonus(player);
				//hit out
				if(player.getHand().size() == 5){
					string += "hit out, the dealer ";
				}
				else{					
					string += "got a " + sum + ", the dealer ";
				}
			}
			//bust
			else {
				string += "busted, the dealer "; 
			}
			//dealer bust
			if(goal == 0){
				string += "busted. ";
			}
			else {
				int sum = 0;
				for(Card card : dealer.getHand()){
					sum += card.getValue();
				}
				sum += aceBonus(dealer);
				string += "got a " + sum + ". ";
			}
			//player wins
			if(winners.contains(player)){
				string += ChatColor.GREEN + "You win! (" + bets.get(player) + ")";
			}
			//player loses
			else {
				string += ChatColor.RED + "You lose! (" + bets.get(player) + ")";
			}
			player.getPlayer().sendMessage(string);
			player.getPlayer().sendMessage("");
		}
	}

	/**
	 * determines if the ace is worth 11 or 1
	 * 
	 * @param player
	 * @return
	 */
	public int aceBonus(CardPlayer player){
		int sum = 0;
		boolean ace = false;
		//totals the player's score while looking for an ace
		for(Card card : player.getHand()){
			sum += card.getValue();
			if(card.getValue() == 1){
				ace = true;
			}
		}
		//player has an ace
		if(ace){
			//checks if player can add ten to their score
			if(sum + 10 <= 21)
				return 10;
		}
		return 0;
	}

	/**
	 * Checks if the player has enough money to make a bet
	 * Executes the bet if they have enough money
	 * 
	 * @param player
	 * @param bet
	 * @return
	 */
	public boolean verifyBet(CardPlayer player, int bet){
		int money = player.getCash();
		if(bet <= money){
			bets.put(player, bet);
			player.setCash(money-bet);
			return true;
		}
		return false;
	}

	/**
	 * Locates the CardPlayer linked to a player 
	 * 
	 * @param player
	 * @return
	 */
	public CardPlayer match(Player player){
		for(CardPlayer cPlayer : players){
			if(cPlayer.getPlayer().equals(player)){
				return cPlayer;
			}
		}
		return null;
	}

	public Slot matchSlot(Player player) {
		CardPlayer cardplayer = match(player);
		for(Slot slot : slotStatus.keySet()){
			if(slotStatus.get(slot) != null && slotStatus.get(slot).equals(cardplayer)){
				return slot;
			}
		}
		return null;
	}


}
