package Base;

/**
 * 
 * @author JorganPubshire
 * 
 * A lightweight card object
 *
 */
public class Card {
	private String suit;
	private int value;
	private String name = "";
	
	public Card(int $value,String $suit){
		value = $value;
		suit = $suit;
		giveName();
	}
	
	/*
	 * getter and setter methods
	 */
	public String getSuit(){
		return suit;
	}
	
	public int getValue(){
		return value;
	}
	
	public void setValue(int i) {
		value = i;	
	}
	
	public String getName(){
		return name;
	}
	
	public void giveName(){
		switch(value){
		case 1: name = "Ace"; break;
		case 2: name = "Two"; break;
		case 3: name = "Three"; break;
		case 4: name = "Four"; break;
		case 5: name = "Five"; break;
		case 6: name = "Six"; break;
		case 7: name = "Seven"; break;
		case 8: name = "Eight"; break;
		case 9: name = "Nine"; break;
		case 10: name = "Ten"; break;
		case 11: name = "Jack"; break;
		case 12: name = "Queen"; break;
		case 13: name = "King"; break;
		}
	}
	
	public String toString(){		
		String str = "The " + name + " of " + suit;
		return str;
	}


}
