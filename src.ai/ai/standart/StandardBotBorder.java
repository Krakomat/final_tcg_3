package ai.standart;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ai.util.AIUtilities;
import common.utilities.Triple;
import network.client.AccountImpl;
import network.client.Player;
import network.server.PokemonGameManager;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.AccountType;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;

/**
 * Represents a stupid bot.
 * 
 * @author Michael
 *
 */
public class StandardBotBorder extends AccountImpl implements Player {

	private PokemonGameManager server;
	private LocalPokemonGameModel localGameModel;
	private AIUtilities aiUtilities;

	/**
	 * Creates a new {@link PlayerImpl} object with the given parameters.
	 * 
	 * @param id
	 * @param name
	 * @param password
	 * @return
	 */
	public static Player createBot(long id, String name, String password) {
		StandardBotBorder p = new StandardBotBorder(id, name, password);
		return p;
	}

	public StandardBotBorder(long id, String name, String password) {
		super(id, name, password);
		this.accountType = AccountType.BOT_STANDARD;
		this.localGameModel = null;
		this.aiUtilities = new AIUtilities();
	}

	private Color playerColor;

	@Override
	public List<Card> playerChoosesCards(List<Card> cards, int amount, boolean exact, String message) {
		List<Card> chosenCards = new ArrayList<Card>();
		for (int i = 0; i < amount && i < cards.size(); i++)
			chosenCards.add(cards.get(i));
		return chosenCards;
	}

	@Override
	public List<PositionID> playerChoosesPositions(List<PositionID> positionList, int amount, boolean exact, String message) {
		ArrayList<PositionID> chosenPositions = new ArrayList<PositionID>();
		for (int i = 0; i < amount && i < positionList.size(); i++)
			chosenPositions.add(positionList.get(i));
		return chosenPositions;
	}

	@Override
	public List<Element> playerChoosesElements(List<Element> elements, int amount, boolean exact, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> playerChoosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Card> playerPaysEnergyCosts(List<Element> costs, List<Card> energyCards) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void playerUpdatesGameModel(GameModelUpdate gameModelUpdate) {
		this.localGameModel = new LocalPokemonGameModel(gameModelUpdate, this);
	}

	@Override
	public List<Integer> playerDistributesDamage(List<Position> positionList, List<Integer> damageList, List<Integer> maxDistList, DistributionMode mode) {
		return null;
	}

	@Override
	public void playerMakesMove() {
		this.aiUtilities.sleep(4000);
		// (Position, positionIndex, Action)
		List<Triple<Position, Integer, String>> actionList = this.aiUtilities.computePlayerActions(this.localGameModel, this);
		List<Triple<Position, Integer, String>> attackList = this.aiUtilities.filterActions(actionList, PlayerAction.ATTACK_1, PlayerAction.ATTACK_2,
				PlayerAction.RETREAT_POKEMON);
		this.aiUtilities.filterActions(attackList, PlayerAction.RETREAT_POKEMON);
		if (!actionList.isEmpty()) {
			Random r = new SecureRandom();
			int index = r.nextInt(actionList.size());
			int handCardIndex = actionList.get(index).getValue();
			server.playerPlaysCard(this, handCardIndex);
		} else if (!attackList.isEmpty()) {
			Random r = new SecureRandom();
			int index = r.nextInt(attackList.size());
			Triple<Position, Integer, String> attackTriple = attackList.get(index);
			if (attackTriple.getAction().equals(PlayerAction.ATTACK_1.toString()))
				this.server.executeAttack(this, ((PokemonCard) attackTriple.getKey().getTopCard()).getAttackNames().get(0));
			else if (attackTriple.getAction().equals(PlayerAction.ATTACK_2.toString()))
				this.server.executeAttack(this, ((PokemonCard) attackTriple.getKey().getTopCard()).getAttackNames().get(1));
		} else
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

	}

	@Override
	public void playerReceivesGameTextMessage(String message) {

	}

	@Override
	public void playerReceivesCardMessage(String message, Card card) {

	}

	@Override
	public void playerReceivesCardMessage(String message, List<Card> cardList) {
	}

	@Override
	public void startGame() {
		// TODO
	}

	@Override
	public void setServer(PokemonGameManager server) {
		this.server = server;
	}

	@Override
	public void exit() {

	}
}
