package Base;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple Deck object composed of 52 Card objects
 * 
 * @author Parker
 *
 */
public class Deck {
	ArrayList<Card> cards = new ArrayList<Card>();
	
	public Deck(){
		makeDeck();
		shuffle();
	}
	
	private void makeDeck(){
		String[] suits = {"Spades","Clubs","Diamonds","Hearts"};
		//cycles through the four suits
		for(int suit = 0; suit < 4; suit++){
			//creates 13 differently valued cards for each suit
			for(int i = 1; i < 14;i++){
				Card card = new Card(i,suits[suit]);
				cards.add(card);
			}
		}
	}
	
	/**
	 * Draws x cards from the top of the deck
	 * 
	 * @param times
	 * @return
	 */
	public ArrayList<Card> drawTop(int times){
		ArrayList<Card> hand = new ArrayList<Card>();
		for(int i = 0; i < times;i++){
			hand.add(cards.get(0));
			cards.remove(0);
		}
		return hand;
	}
	
	/**
	 * Draws one card from the top of the deck
	 * 
	 * @return
	 */
	public Card draw(){
		Card card = drawTop(1).get(0);
		return card;
	}
	
	/**
	 * Draws x cards from the top of the deck
	 * 
	 * @param times
	 * @return
	 */
	public ArrayList<Card> draw(int times){
		return drawTop(times);
	}
	
	/**
	 * Draws x cards from the bottom of the deck
	 * 
	 * @param times
	 * @return
	 */
	public ArrayList<Card> drawBottom(int times){
		ArrayList<Card> hand = new ArrayList<Card>();
		for(int i = 0; i < times;i++){
			hand.add(cards.get(cards.size()-1));
			cards.remove(cards.size()-1);
		}
		return hand;
	}
	
	/**
	 * Calculates how many cards are left in the deck
	 * 
	 * @return
	 */
	public int size(){
		return cards.size();
	}
	
	/**
	 * Randomizes the order of the cards in the deck
	 */
	public void shuffle(){
		ArrayList<Card> temp = new ArrayList<Card>();
		Random rand = new Random();
		while(cards.size() > 0){
			int index = rand.nextInt(cards.size());
			temp.add(cards.get(index));
			cards.remove(index);
		}
		cards = temp;
	}
	
}
