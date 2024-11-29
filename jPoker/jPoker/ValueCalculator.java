package jPoker;
import java.util.*;

public class ValueCalculator implements Comparable<ValueCalculator>{

	ArrayList<Card> playerCards = new ArrayList<Card>();
	int handValue = 0;
	Player player;
	
	public ValueCalculator(Player player, ArrayList<Card> playerCards) {
		this.player = player;
		this.playerCards = playerCards;
		handValue = calculator();
	}
	//Calculator method
	public int calculator()
	{
		int value = 0;
		
		return value = 0;
	}
	@Override
	public int compareTo(ValueCalculator v) {
		if (handValue == v.handValue)
            return 0;
        else if (handValue < v.handValue) // < for descending (I think)
            return 1;
        else
            return -1;
	}
}
