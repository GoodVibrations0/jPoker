package jPoker;
import java.util.ArrayList;

public class Player implements Comparable<Player> {
	String name;
	int bankroll;
	int stack = 1000;
	int inRoundPot;
	int inPotTotal;
	ArrayList<Card> hand = new ArrayList<Card>();
	Boolean human = false;
	Boolean even = false;
	Boolean isAllIn = false;
	
	public Player(String name)
	{
		this.name = name;
	}
	public Player(String name, boolean human)
	{
		this.name = name;
		this.human = true;
	}
	
	public void setStack(int value)
	{
		stack = value;
	}
	public int getStack()
	{
		return stack;
	}
	public void check()
	{
		System.out.println(name+" checks.");
	}
	public int call(int bet)
	{
		System.out.println(name+" calls "+bet+".");
		stack = stack-bet;
		return bet;
	}
	public int raise(int bet)
	{
		System.out.println(name+" raises to "+bet+".");
		stack = stack-bet;
		return bet;
	}
	public void fold()
	{
		for(Card c : hand)
			hand.remove(c);
		System.out.println(name+" folds.");
		even = true;
		if(game.playersInPot.contains(this))
			game.playersInPot.remove(this);
	}
	public int allIn() {
		System.out.println(name+" is all in for "+stack+".");
		int allIn = getStack();
		setStack(0);
		return allIn;
	}
	@Override
	public int compareTo(Player p) {
		if (inRoundPot == p.inRoundPot)
            return 0;
        else if (inRoundPot > p.inRoundPot)
            return 1;
        else
            return -1;
	}
}
