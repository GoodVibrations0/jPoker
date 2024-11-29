package jPoker;
import java.util.*;
public class game {

	public static ArrayList<Card> cards;
	public static ArrayList<Card> deck;
	public static ArrayList<Card> board;
	public static ArrayList<Seat> table;
	public static ArrayList<Player> playersInPot;
	public static Player user;
	public static Boolean quit = false;
	public static Boolean sidePot = false;
	public static int bb, sb, betToMatch, handNumber, pot, roundPot;
	public static ArrayList<Integer> sidePots; //use the seat number as the index
	public static boolean happy = true;
	public static int[] moveOrder;
	
	public static void main(String[] args) {
		
		cards = new ArrayList<Card>();
		deck = new ArrayList<Card>();
		board = new ArrayList<Card>();
		table = new ArrayList<Seat>();
		//filling the deck
		//s for suit
		for(int s=1; s<=4; s++)
		{	// v for value
			for(int v=2; v<=14; v++)
			{
				cards.add(new Card(v,s));
			}
		}
		refillDeck();
		System.out.println(deck.size()+" cards in deck");
		
		//Add and fill seats
		for(int i=1; i<=6;i++)
		{
			table.add(new Seat(i));
		}
		user = createUser();
		user.human = true;
		System.out.println("Welcome "+user.name+", game commencing.");
		table.get(0).addPlayer(user);
		addBots();
		
		//This will assign big and small blinds
		Random random = new Random();
		int randomAssignBigBlind = random.nextInt(table.size());
		table.get(randomAssignBigBlind).bigBlind=true;
		
		//Start game
		handNumber = 0;
		bb = 20;
		sb = 10;
		betToMatch = bb;
		do
		{
		    pot = 0;
		    sidePot = false;
			System.out.println("Hand #"+ ++handNumber);
			playersInPot = new ArrayList<Player>();
			
			preflop();
			//flop
			draw(3);
			round();
			//turn
			draw(1);
			round();
			//river
			draw(1);
			round();
			showdown();
			
		}while(quit == false);
	}
	
	public static void showdown() {
		if(playersInPot.size()>1) //There will only be a showdown if more than 1 player left in pot
		{
			ArrayList<ValueCalculator> playerHandValues = new ArrayList<ValueCalculator>();
			for(Player p : playersInPot)
			{
				ArrayList<Card> playerCards = new ArrayList<Card>();
				for(Card c : p.hand)
					playerCards.add(c);
				for(Card c : board)
					playerCards.add(c);
				playerHandValues.add(new ValueCalculator(p,playerCards));
			}
			Collections.sort(playerHandValues); //This will sort best to worst
			ArrayList<Player> winners = new ArrayList<Player>();
			int bestHandValue = playerHandValues.get(0).handValue;
			for(ValueCalculator v : playerHandValues)
			{
				if(v.handValue == bestHandValue)
					winners.add(v.player);
			}
			if(sidePot)
			{
				//if the sidepot for player >0 then the player wins the sidepot else wins pot
				for(Player p : winners) //This will split the pot if multiple players have the same best hand 
				{
					int seat=0;
					for(Seat s : table)
						if(s.player == p)
							seat = s.number;
					if(sidePots.get(seat) > 0) //If the player has a sidepot he will only win this. He is then removed from the winners and the rest
					{
						System.out.println(p.name+" wins sidepot"+sidePots.get(seat));
						p.stack += sidePots.get(seat);
						pot -= sidePots.get(seat);
						winners.remove(p);
						playersInPot.remove(p);
						if(winners.isEmpty()) //If the sidepot winner was the only winner then the next best hand(s) takes the rest of the pot
						{
							bestHandValue = playerHandValues.get(0).handValue;
							for(ValueCalculator v : playerHandValues)
							{
								if(v.handValue == bestHandValue)
									winners.add(v.player);
							}
							for(Player pSecond : winners) //This will split the pot if multiple players have the same best hand 
							{
								System.out.println(pSecond.name+" wins "+(pot/winners.size()));
								pSecond.stack += pot/winners.size();
							}
						}
					}
					else
					{
						System.out.println(p.name+" wins "+(pot/winners.size()));
						p.stack += pot/winners.size();
					}
				}
			}
			else
			{
				for(Player p : winners) //This will split the pot if multiple players have the same best hand 
				{
					System.out.println(p.name+" wins "+(pot/winners.size()));
					p.stack += pot/winners.size();
				}
			}
		}
	}
	static void preflop()
	{
		betToMatch = bb;
		moveBlinds();
		//Table order
		
		moveOrder = order(true);
		//THIS IS WHERE I LEFT OFF
		
		printState();
		System.out.println("DEALING CARDS");
		deal();
		printCards(table.get(0).player);
		System.out.println("PREFLOP");
		payBlinds();
		round();	
	}
	static void round()
	{
		playersInPot.clear();
		do
		{	
			//Players will move In the moveOrder until everyone is happy. 
			happy = true;
			//int playersInPot = 0; //If only one player is still in the pot they win
			for(Seat s : table)
			{
				if(s.player.stack == 6000)
				{
					System.out.println(s.player.name+" wins!");
					quit = true;
				}
			}
			if(quit)
				break;

			if(playersInPot.size()==1)
			{
				System.out.println(playersInPot.get(0).name+" wins pot of "+(pot+roundPot));
				playersInPot.get(0).stack += pot += roundPot;
				pot = roundPot = 0;
			}
				
			for(int i : moveOrder)
			{
				for(Seat s : table)
				{
					if(s.number == i)
					{
						if(!s.player.hand.isEmpty())
						{
							if(s.player.human)
							{
								printMenu();
							}								
							else
							{
								ai(s);
							}
							if(!s.player.even) //If any player who HAS CARDS is NOT EVEN they we are NOT HAPPY yet
								happy = false;
						}
					}
				}
			}
		}while(!happy);
		int highestInRoundPot = 0;
		for(Player p : playersInPot) //Checking if sidepots are needed
		{
			if(p.inRoundPot > highestInRoundPot)
				highestInRoundPot = p.inRoundPot;
		}
		for(Player p : playersInPot) //Checking if sidepots are needed
		{
			if(p.inRoundPot < highestInRoundPot)
				sidePot = true;
			sidePots();
		}
		pot += roundPot;
		roundPot = 0;
	}
	static void call(Player player) 
	{
		if(betToMatch == 0)
		{
			check(player);
			return;
		}
		int toPay = (betToMatch - player.inRoundPot);
		if(player.stack <= toPay)
		{
			allIn(player);
			sidePot = true;
			return;
		}	
		else
		{
			player.stack = player.stack - (toPay);
			roundPot = roundPot+(toPay);
		}
		player.inRoundPot += toPay;
		player.inPotTotal += toPay;
		player.even = true;
		System.out.println(player.name+" calls "+toPay);
		if(!playersInPot.contains(player))
			playersInPot.add(player);
	}
	static void allIn(Player player)
	{
		player.inRoundPot += player.stack;
		player.inPotTotal += player.stack;
		System.out.println(player.name+" is all in for "+player.inPotTotal+".");
		int sNum = 0;
		for(Seat s : table)
		{
			if(s.player == player)
				sNum = s.number-1; //This is to store the players split pot amount.
		}
		roundPot += player.stack;

		if(player.stack > betToMatch)
		{
			betToMatch = player.stack;
			moveOrder = order(sNum);
		}
		player.stack = 0;
		player.even = true;
		player.isAllIn = true;
		if(!playersInPot.contains(player))
			playersInPot.add(player);
	}
	static void check(Player player)
	{
		System.out.println(player.name+" checks.");
		player.even = true;
		if(!playersInPot.contains(player))
			playersInPot.add(player);
	}
	static void raiseTo(Player player, int amount)
	{
		System.out.println(player.name+" raises to "+amount+".");
		betToMatch = amount;
		int toPay = (betToMatch - player.inRoundPot);
		if(player.stack <= toPay)
			allIn(player);
		else
		{
			player.stack = player.stack - (toPay);
			roundPot = roundPot+(toPay);
		}
		player.inRoundPot += toPay;
		player.inPotTotal += toPay;
		
		int sNum = 0;
		for(Seat s : table)
		{
			s.player.even = false; //when you raise everyone owes you.
			if(s.player == player)
				sNum = s.number-1; 
		}
		player.even = true;
		moveOrder = order(sNum);
		if(!playersInPot.contains(player))
			playersInPot.add(player);
	}
	static int[] order(Boolean preflop)
	{
		int pointerNum = 0;
		int[] order = new int[6];
		for(Seat s : table)
		{
			if(s.bigBlind)
				pointerNum = s.number;
		}
		if(preflop) 
		{
			for(int i=0; i<6; i++)
			{
				pointerNum++;
				if(pointerNum == 7)
					pointerNum = 1;
				order[i] = pointerNum;
			}
		}
		else
		{
			if(pointerNum == 1)
				pointerNum = 6;
			else 
				pointerNum--; //order starts at small blind
			for(int i=0; i<6; i++)
			{
				order[i] = pointerNum;
				pointerNum++;
				if(pointerNum == 7)
					pointerNum = 1;
			}
		}
		return order;
	}
	static int[] order(int pos) //This method changes the move order after a player raises. New move order will not include him
	{
		int pointerNum = pos;
		int[] order = new int[5];

			for(int i=0; i<5; i++)
			{
				pointerNum++;
				if(pointerNum == 7)
					pointerNum = 1;
				order[i] = pointerNum;
			}
		return order;
	}
	static void payBlinds()
	{
		for(Seat s : table)
		{
			if(s.bigBlind)
			{
				if(s.player.stack <= bb)
				{
					allIn(s.player);
				}
				else
				{
					s.player.stack = s.player.stack - bb;
					roundPot = roundPot+bb;
					s.player.inRoundPot += bb;
					s.player.inPotTotal += bb;
					System.out.println(s.player.name+" pays big blind "+bb);
					playersInPot.add(s.player);
				}
			}
			if(s.smallBlind)
			{
				if(s.player.stack <= sb)
				{
					roundPot = roundPot+s.player.allIn();
				}
				else
				{
					s.player.stack = s.player.stack - sb;
					roundPot = roundPot+sb;
					s.player.inRoundPot += sb;
					s.player.inPotTotal += sb;
					System.out.println(s.player.name+" pays small blind "+sb);
					playersInPot.add(s.player);
				}
			}
		}
	}
	static void sidePots()
	{
		Collections.sort(playersInPot);
		int max = playersInPot.get(playersInPot.size()-1).inRoundPot;
		for(Player p : playersInPot)
		{
			int seat=0;
			for(Seat s : table)
				if(s.player == p)
					seat = s.number;
			if(p.inRoundPot < max)
				sidePots.add(seat, (p.inRoundPot*playersInPot.size()) + pot);
		}
		sidePot = true;
	}
	static void printMenu()
	{
		Scanner key = new Scanner(System.in);
		boolean valid = false;
		do
		{
			do
			{
				System.out.println("1. Check");
				System.out.println("2. Call "+(betToMatch-user.inRoundPot));
				System.out.println("3. Raise");
				System.out.println("4. Fold");
				System.out.println("5. Show Cards");
				System.out.println("6. Show Table State");
				System.out.println("0. Exit Game");
				if(!key.hasNextInt())
					key.next();
			}while(!key.hasNextInt());

			int input = key.nextInt();

				switch(input)
				{
				case 1 : //Check
					if(user.even)
					{
						user.check();
						valid = true;
					}
					else
					{
						System.out.println("Cannot check until bet "+betToMatch+"has been matched. To match: "+(betToMatch-user.inRoundPot));
					}
					break;
				case 2 : //Call
					call(user);
					valid = true; break;
				case 3 : //Raise to
					int amount = 0;
					do
					{
						System.out.println("Enter amount to raise to (min "+betToMatch*2+" stack "+user.stack+").");
						System.out.println("Enter a non integer to cancel");
						if(!key.hasNextInt())
							break;
						amount = key.nextInt();
						if(amount >= user.stack)
						{
							allIn(user);
							valid = true;
							break;
						}
						else if(amount < betToMatch*2)
						{
							System.out.println("Invalid amount");
						}
						else
						{
							raiseTo(user,amount);
							valid = true;
							break;
						}
					}while(amount < betToMatch*2);
					break;
				case 4 :
					user.fold();
					valid = true; break;
				case 5 :
					printCards(table.get(0).player);
					break;
				case 6 :
					printState();
					break;
				case 0 :
					System.out.println("Thanks for playing!");
					quit = true;
					valid = true; break;
				default :
					break;
				}
		}while(valid == false);		
	}
	static void printCards(Player player)
	{
		System.out.println("Hand:\t"+player.hand.get(0).toString()+"\t"+player.hand.get(1).toString());
		System.out.println("");
		if(!board.isEmpty())
		{
			System.out.println("Board: ");
			for(Card c : board)
				System.out.println(c.toString());
			System.out.println("");
		}
	}
	static void printState()
	{
		for(Seat s: table)
			System.out.println(s.toString());
	}
	static void moveBlinds()
	{
		for(Seat s : table)
		{
			if(s.smallBlind)
				s.smallBlind = false;
			if(s.bigBlind)
			{
				s.bigBlind = false;
				s.smallBlind = true;
				if(s.number==table.size())
				{
					table.get(0).bigBlind = true;
				}
				else	//the seat number will always be 1 higher than its index. So I'm targeting the next seat on the table
					table.get(s.number).bigBlind = true;
				break;
			}
		}
	}
	static void deal()
	{
		refillDeck();
		Random random = new Random();
		for(Seat s: table)
		{
			for(int i=0; i<2; i++)
			{
				if(s.player!=null && s.player.hand.size()<2 && s.player.stack>0)
				{
					Card card = deck.get(random.nextInt(deck.size()));
					deck.remove(card);
					s.player.hand.add(card);
				}
			}
		}	
	}
	static void draw(int amount)
	{
		Random random = new Random();
		for(int i=0; i<amount; i++)
		{
			Card card = deck.get(random.nextInt(deck.size()));
			deck.remove(card);
			board.add(card);
		}
	}
	static void refillDeck()
	{
		for(Card c : cards)
		{
			//System.out.println(c.toString());
			deck.add(c);
		}
		System.out.println("deck refilled");
	}
	static void addBots()
	{
		if(table != null)
		{
			table.get(1).addPlayer(new Player("Liam McCarran"));
			table.get(2).addPlayer(new Player("Fyodor Dostoevsky"));
			table.get(3).addPlayer(new Player("Phil Helmuth"));
			table.get(4).addPlayer(new Player("Daniel Negreanu"));
			table.get(5).addPlayer(new Player("Phil Ivey"));
		}
	}
	static Player createUser()
	{
		String name;
		boolean valid = false;
		Scanner key = new Scanner(System.in);
		do
		{
			System.out.println("Enter your name: ");
			name = key.nextLine();
			if(name.isEmpty())
				System.out.println("Invalid name");
			else
				valid = true;
		}while(valid == false);
		return new Player(name);
	}
	static void ai(Seat seat)
	{
		//int stack = seat.player.stack;
		if(seat.player.inRoundPot == betToMatch)
			check(seat.player);
		else
			seat.player.fold();
	}
}
