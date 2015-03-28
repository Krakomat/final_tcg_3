package model.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.GameState;
import model.enums.PositionID;
import model.interfaces.GameField;
import model.interfaces.GameModelUpdate;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.CardScriptFactory;

/**
 * Used by a client to locally store the game model that was send from the server.
 * 
 * @author Michael
 *
 */
public class LocalPokemonGameModel implements PokemonGame {
	protected long gameID;
	protected int turnNumber;
	protected Player playerOnTurn;
	protected Player playerRed, playerBlue;
	protected GameState gameState;
	protected boolean energyPlayed;
	protected AttackAction attackAction;
	protected AttackCondition attackCondition;
	protected Map<Integer, Card> cardMap;
	protected CardScriptFactory cardScriptFactory;

	private GameField gameField;

	/**
	 * Constructs a game model from the given {@link GameModelUpdate}.
	 * 
	 * @param gameModelUpdate
	 */
	public LocalPokemonGameModel(GameModelUpdate gameModelUpdate, Player client) {
		this.gameField = new GameFieldImpl(gameModelUpdate);
		this.turnNumber = gameModelUpdate.getTurnNumber();
		this.gameID = 0; // just a default id
		this.playerOnTurn = client;
		if (client.getColor() == Color.BLUE) {
			this.playerBlue = client;
			this.playerRed = null;
		} else {
			this.playerBlue = null;
			this.playerRed = client;
		}
		this.gameState = GameState.RUNNING;
		this.energyPlayed = false; // modify this in the setter
		this.attackAction = new AttackAction(this);
		this.attackCondition = new AttackCondition(this);
		this.cardScriptFactory = new CardScriptFactory();
		this.cardMap = new HashMap<>();
		for (Position position : this.gameField.getAllPositions())
			for (Card c : position.getCards())
				this.cardMap.put(c.getGameID(), c);
	}

	@Override
	public void initNewGame() {
		// leave empty
	}

	@Override
	public GameModelUpdate getGameModelForPlayer(Player player) {
		// leave empty
		return null;
	}

	@Override
	public Position getPosition(PositionID posID) {
		return this.gameField.getPosition(posID);
	}

	@Override
	public Player getPlayerOnTurn() {
		// leave empty
		return null;
	}

	@Override
	public Player getPlayerRed() {
		// leave empty
		return null;
	}

	@Override
	public void setPlayerRed(Player playerRed) {
		// leave empty
	}

	@Override
	public Player getPlayerBlue() {
		// leave empty
		return null;
	}

	@Override
	public void setPlayerBlue(Player playerBlue) {
		// leave empty
	}

	@Override
	public List<Card> getAllCards() {
		List<Card> cardList = new ArrayList<>();
		for (Integer i : this.cardMap.keySet())
			cardList.add(this.cardMap.get(i));
		return cardList;
	}

	@Override
	public Card getCard(int cardGameID) {
		return this.cardMap.get(cardGameID);
	}

	@Override
	public void registerCard(Card card) {
		// leave empty
	}

	@Override
	public void unregisterCard(Card card) {
		// leave empty
	}

	@Override
	public ArrayList<Card> getBasicPokemonOnPosition(PositionID posID) {
		ArrayList<Card> list = new ArrayList<Card>();
		Position pos = this.getPosition(posID);
		for (int i = 0; i < pos.size(); i++) {
			Card c = pos.getCards().get(i);
			if (c.getCardType().equals(CardType.BASICPOKEMON))
				list.add(c);
		}
		return list;
	}

	@Override
	public ArrayList<PositionID> getFullArenaPositions(Color playerColor) {
		return this.gameField.getFullArenaPositions(playerColor, playerBlue, playerRed);
	}

	@Override
	public ArrayList<PositionID> getFullBenchPositions(Color playerColor) {
		return this.gameField.getFullBenchPositions(playerColor, playerBlue, playerRed);
	}

	@Override
	public ArrayList<PositionID> getPositionsForEvolving(PokemonCard c, Color color) {
		return this.gameField.getPositionsForEvolving(c, color, turnNumber);
	}

	@Override
	public void sendCardMessageToAllPlayers(String string, List<Card> cardList) {
		// leave empty
	}

	@Override
	public void sendCardMessageToAllPlayers(String string, Card card) {
		// leave empty
	}

	@Override
	public void sendTextMessageToAllPlayers(String message) {
		// leave empty
	}

	@Override
	public void sendGameModelToAllPlayers() {
		// leave empty
	}

	@Override
	public AttackAction getAttackAction() {
		return this.attackAction;
	}

	@Override
	public AttackCondition getAttackCondition() {
		return this.attackCondition;
	}

	@Override
	public void setEnergyPlayed(boolean b) {
		this.energyPlayed = b;
	}

	@Override
	public int getTurnNumber() {
		return turnNumber;
	}

	@Override
	public GameState getGameState() {
		return this.gameState;
	}

	@Override
	public void executeEndTurn() {
		// leave empty
	}

	@Override
	public void nextTurn() {
		// leave empty
	}

	@Override
	public void betweenTurns() {
		// leave empty
	}

	@Override
	public long getGameID() {
		return this.gameID;
	}

	@Override
	public void cleanDefeatedPositions() {
		// leave empty
	}

	@Override
	public void playerLoses(Player player) {
		// leave empty
	}

	@Override
	public boolean getEnergyPlayed() {
		return this.energyPlayed;
	}

	@Override
	public PositionID getDefendingPosition(Color attackerColor) {
		if (attackerColor == null)
			return null;
		if (attackerColor == Color.BLUE)
			return PositionID.RED_ACTIVEPOKEMON;
		else
			return PositionID.BLUE_ACTIVEPOKEMON;
	}

	public GameField getGameField() {
		return this.gameField;
	}
}
