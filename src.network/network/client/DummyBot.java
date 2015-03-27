package network.client;

import java.util.ArrayList;
import java.util.List;

import network.server.PokemonGameManager;
import model.database.Card;
import model.enums.AccountType;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;

/**
 * Represents a stupid bot.
 * 
 * @author Michael
 *
 */
public class DummyBot extends AccountImpl implements Player {

	static boolean BOT_DEBUGGER = false;
	private PokemonGameManager server;

	/**
	 * Creates a new {@link PlayerImpl} object with the given parameters.
	 * 
	 * @param id
	 * @param name
	 * @param password
	 * @return
	 */
	public static Player createDummyBot(long id, String name, String password) {
		DummyBot p = new DummyBot(id, name, password);
		return p;
	}

	public DummyBot(long id, String name, String password) {
		super(id, name, password);
		this.accountType = AccountType.BOT_DUMMY;
	}

	private Color playerColor;

	@Override
	public List<Card> playerChoosesCards(List<Card> cards, int amount, boolean exact, String message) {
		if (BOT_DEBUGGER) {
			System.out.println("[Bot] " + this.name + " received playerChoosesCards");
			for (int i = 0; i < cards.size(); i++)
				System.out.println("[Bot] Card " + cards.get(i).getName() + " can be chosen by " + this.name);
		}
		ArrayList<Card> chosenCards = new ArrayList<Card>();
		for (int i = 0; i < amount && i < cards.size(); i++)
			chosenCards.add(cards.get(i));
		return chosenCards;
	}

	@Override
	public List<PositionID> playerChoosesPositions(List<PositionID> positionList, int amount, boolean exact, String message) {
		if (BOT_DEBUGGER) {
			System.out.println("[Bot] " + this.name + " received playerChoosesPositions");
			for (int i = 0; i < positionList.size(); i++)
				System.out.println("[Bot] Position " + positionList.get(i) + " can be chosen by " + this.name);
		}
		ArrayList<PositionID> chosenPositions = new ArrayList<PositionID>();
		for (int i = 0; i < amount && i < positionList.size(); i++)
			chosenPositions.add(positionList.get(i));
		return chosenPositions;
	}

	@Override
	public List<Element> playerChoosesElements(List<Element> elements, int amount, boolean exact, String message) {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received playerChoosesElements");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> playerChoosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact, String message) {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received playerChoosesAttacks");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Card> playerPaysEnergyCosts(List<Element> costs, List<Card> energyCards) {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received playerPaysEnergyCosts");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void playerUpdatesGameModel(GameModelUpdate gameModelUpdate) {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received playerUpdatesGameModel");
		// TODO Auto-generated method stub

	}

	@Override
	public List<Integer> playerDistributesDamage(List<Position> positionList, List<Integer> damageList, List<Integer> maxDistList, DistributionMode mode) {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received playerDistributesDamage");
		return null;
	}

	@Override
	public void playerMakesMove() {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received playerMakesMove");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		server.endTurn(this);
	}

	@Override
	public Color getColor() {
		return playerColor;
	}

	@Override
	public void setColor(Color color) {
		playerColor = color;
	}

	@Override
	public void receiveGameDeleted() {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received receiveGameDeleted");
	}

	@Override
	public void playerReceivesGameTextMessage(String message) {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received playerReceivesCardMessage");
	}

	@Override
	public void playerReceivesCardMessage(String message, Card card) {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received playerReceivesCardMessage");

	}

	@Override
	public void playerReceivesCardMessage(String message, List<Card> cardList) {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received playerReceivesCardMessage");
	}

	@Override
	public void startGame() {
		if (BOT_DEBUGGER)
			System.out.println("[Bot] " + this.name + " received startGame()");
	}

	@Override
	public void setServer(PokemonGameManager server) {
		this.server = server;
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}
}
