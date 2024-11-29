package jPoker;

public class Card {
	int value;
	String suit;
	String name;
	boolean ace;
	
	
	//j11 q12 k13 a14
	public Card(int value, int suit)
	{
		this.value = value;
		switch(value)
		{
			case 11 : name = "Jack"; 
			break;
			case 12 : name = "Queen";
			break;
			case 13 : name = "King";
			break;
			case 14 : name = "Ace";
				ace = true;
				break;
			default : name = Integer.toString(value);
		}
		switch(suit)
		{
			case 1 : this.suit = "Spades";
			break;
			case 2 : this.suit = "Hearts";
			break;
			case 3 : this.suit = "Diamonds";
			break;
			case 4 : this.suit = "Clubs";
		}
	}
	
	public String toString()
	{
		return name+" of "+suit;
	}
}
