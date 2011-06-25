package Base;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
	ArrayList<Card> cards = new ArrayList<Card>();
	
	public Deck(){
		makeDeck();
		shuffle();
	}
	
	private void makeDeck(){
		String[] suits = {"Spades","Clubs","Diamonds","Hearts"};
		for(int suit = 0; suit < 4; suit++){
			for(int i = 1; i < 14;i++){
				Card card = new Card(i,suits[suit]);
				cards.add(card);
			}
		}
	}
	
	public ArrayList<Card> drawTop(int times){
		ArrayList<Card> hand = new ArrayList<Card>();
		for(int i = 0; i < times;i++){
			hand.add(cards.get(0));
			cards.remove(0);
		}
		return hand;
	}
	
	public Card draw(){
		Card card = drawTop(1).get(0);
		return card;
	}
	public ArrayList<Card> draw(int times){
		ArrayList<Card> hand = drawTop(times);
		return hand;
	}
	
	public ArrayList<Card> drawBottom(int times){
		ArrayList<Card> hand = new ArrayList<Card>();
		for(int i = 0; i < times;i++){
			hand.add(cards.get(cards.size()-1));
			cards.remove(cards.size()-1);
		}
		return hand;
	}
	
	public int size(){
		return cards.size();
	}
	
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
