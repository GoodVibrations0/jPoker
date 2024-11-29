package jPoker;

//The player sits in the seat. Information for blinds are stored here
public class Seat {
	int number;
	boolean bigBlind = false;
	boolean smallBlind = false;
	boolean button = false;
	boolean hasPlayer = false;
	Player player;
	
	public Seat(int number)
	{
		this.number = number;
	}
	
	public void addPlayer(Player player)
	{
		this.player = player;
	}
	public void switchBigBlind() 
	{
		if(bigBlind)
			bigBlind = false;
		else
			bigBlind = true;
	}
	public void switchSmallBlind() 
	{
		if(smallBlind)
			smallBlind = false;
		else
			smallBlind = true;
	}
	public void switchButton() 
	{
		if(button)
			button = false;
		else
			button = true;
	}
	public void switchHasPlayer() 
	{
		if(hasPlayer)
			hasPlayer = false;
		else
			hasPlayer = true;
	}
	public String toString()
	{
		if(bigBlind)
			return "Seat "+number+", Big Blind: "+player.name+". Stack Size: "+player.stack+" chips";
		if(smallBlind)
			return "Seat "+number+", Small Blind: "+player.name+". Stack Size: "+player.stack+" chips";
		if(button)
			return "Seat "+number+", Button: "+player.name+". Stack Size: "+player.stack+" chips";
		return "Seat "+number+": "+player.name+". Stack Size: "+player.stack+" chips";
	}
}
