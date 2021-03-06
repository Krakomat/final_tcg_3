package ai.interfaces;

import gui2d.animations.Animation;

import java.util.List;

import ai.dummy.DummyBot;
import ai.treebot.GameTree;
import ai.treebot.TreeBot;
import network.client.AccountImpl;
import network.client.Player;
import network.server.PokemonGameManager;
import model.database.Card;
import model.enums.AccountType;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PositionID;
import model.game.GameModelUpdate;
import model.interfaces.Position;

/**
 * Represents a stupid bot.
 * 
 * @author Michael
 *
 */
public class BotBorder extends AccountImpl implements Player {

	private PokemonGameManager server;
	private Bot botModel;

	/**
	 * Creates a new {@link PlayerImpl} object with the given parameters.
	 * 
	 * @param id
	 * @param name
	 * @param password
	 * @param prizeCards
	 * @return
	 */
	public static Player createBot(long id, String name, String password, String avatar, int prizeCards, AccountType type) {
		BotBorder p = new BotBorder(id, name, password, avatar, prizeCards, type);
		return p;
	}

	public BotBorder(long id, String name, String password, String avatar, int prizeCards, AccountType type) {
		super(id, name, password, avatar, prizeCards);
		this.accountType = type;
		switch (this.accountType) {
		case BOT_DUMMY:
			this.botModel = new DummyBot();
			break;
		case BOT_TREE:
			this.botModel = new TreeBot();
			break;
		default:
			break;
		}
	}

	private Color playerColor;

	@Override
	public List<Card> playerChoosesCards(List<Card> cards, int amount, boolean exact, String message) {
		return this.botModel.choosesCards(cards, amount, exact);
	}

	@Override
	public List<PositionID> playerChoosesPositions(List<PositionID> positionList, int amount, boolean exact, String message) {
		return this.botModel.choosesPositions(positionList, amount, exact);
	}

	@Override
	public List<Element> playerChoosesElements(List<Element> elements, int amount, boolean exact, String message) {
		return this.botModel.choosesElements(elements, amount, exact);
	}

	@Override
	public List<String> playerChoosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact, String message) {
		return this.botModel.choosesAttacks(attackOwner, attacks, amount, exact);
	}

	@Override
	public boolean playerDecidesYesOrNo(String question) {
		return true; // Always answer 'Yes'
	}

	@Override
	public List<Card> playerPaysEnergyCosts(List<Element> costs, List<Card> energyCards) {
		return this.botModel.paysEnergyCosts(costs, energyCards);
	}

	@Override
	public void playerUpdatesGameModel(GameModelUpdate gameModelUpdate, String sound) {
		this.botModel.updateGameModel(gameModelUpdate, this, server);
	}

	@Override
	public List<Integer> playerDistributesDamage(List<Position> positionList, List<Integer> damageList, List<Integer> maxDistList, DistributionMode mode) {
		return damageList;
	}

	@Override
	public void playerMakesMove() {
		Player self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				botModel.makeMove(server, self);
			}
		}).start();
	}

	@Override
	public Color getColor() {
		return playerColor;
	}

	@Override
	public void setColor(Color color) {
		playerColor = color;
		this.botModel.setColor(color);
	}

	@Override
	public void receiveGameDeleted() {

	}

	@Override
	public void playerReceivesGameTextMessage(String message, String sound) {

	}

	@Override
	public void playerReceivesCardMessage(String message, Card card, String sound) {

	}

	@Override
	public void playerReceivesCardMessage(String message, List<Card> cardList, String sound) {
	}

	@Override
	public void startGame() {
		this.botModel.startGame();
	}

	@Override
	public void setServer(PokemonGameManager server) {
		this.server = server;
	}

	@Override
	public void exit() {

	}

	@Override
	public void playerReceivesSound(String sound) {

	}

	@Override
	public void playerReceivesAnimation(Animation animation) {

	}

	@Override
	public void setBotDifficulty(int diff) {
		GameTree.TREE_DEPTH = diff;
	}
}
