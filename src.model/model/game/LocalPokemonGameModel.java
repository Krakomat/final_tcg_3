package model.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network.client.Player;
import network.client.PlayerImpl;
import network.server.PokemonGameManager;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.GameState;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.GameField;
import model.interfaces.GameModelUpdate;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.CardScript;
import model.scripting.abstracts.CardScriptFactory;
import model.scripting.abstracts.PokemonCardScript;
import model.scripting.abstracts.ServerCards;

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
	protected boolean energyPlayed, retreatExecuted;
	protected AttackAction attackAction;
	protected AttackCondition attackCondition;
	protected Map<Integer, Card> cardMap;
	protected CardScriptFactory cardScriptFactory;
	protected PokemonGameManager server;
	private GameField gameField;

	/**
	 * Constructs a game model from the given {@link GameModelUpdate}.
	 * 
	 * @param gameModelUpdate
	 */
	public LocalPokemonGameModel(GameModelUpdate gameModelUpdate, Player client, PokemonGameManager server) {
		this.gameField = new GameFieldImpl(gameModelUpdate);
		this.turnNumber = gameModelUpdate.getTurnNumber();
		this.gameID = 0; // just a default id
		this.playerOnTurn = client;
		this.server = server;
		if (client.getColor() == Color.BLUE) {
			this.playerBlue = client;
			this.playerRed = new PlayerImpl(gameID, "EnemyPlayer", "");
		} else {
			this.playerBlue = new PlayerImpl(gameID, "EnemyPlayer", "");
			this.playerRed = client;
		}
		this.gameState = GameState.RUNNING;
		this.energyPlayed = gameModelUpdate.isEnergyPlayAllowed();
		this.retreatExecuted = gameModelUpdate.isRetreatAllowed();
		this.attackAction = new AttackAction(this);
		this.attackCondition = new AttackCondition(this);
		this.cardScriptFactory = new CardScriptFactory();
		this.cardMap = new HashMap<>();
		for (Position position : this.gameField.getAllPositions()) {
			for (Card c : position.getCards()) {
				c.setCurrentPositionLocal(position);
				this.cardMap.put(c.getGameID(), c);
				if (!c.getCardId().equals("00000")) {
					CardScript script = this.cardScriptFactory.createScript(c, this);
					c.setCardScript(script);
				}
			}
		}
	}

	public List<String> getPlayerActions(int positionIndex, PositionID position, Player player) {
		boolean handCard = position == PositionID.BLUE_HAND || position == PositionID.RED_HAND;
		Card c = handCard ? this.getPosition(position).getCardAtIndex(positionIndex) : this.getPosition(position).getTopCard();
		List<PlayerAction> actionList = new ArrayList<PlayerAction>();
		Position pos = this.getPosition(position);
		// If player on turn and position doesn't belong to enemy
		boolean playerOnTurn = this.getPlayerOnTurn().getColor() == player.getColor();
		boolean playerOnTurnBlue = this.getPlayerOnTurn().getColor() == Color.BLUE;
		boolean playerOnTurnRed = this.getPlayerOnTurn().getColor() == Color.RED;
		boolean positionColorBlue = pos.getColor() == Color.BLUE;
		if (playerOnTurn && ((positionColorBlue && playerOnTurnBlue) || (!positionColorBlue && playerOnTurnRed))) {
			if (ServerCards.createInstance().contains(c.getCardId()))
				return server.getPlayerActions(positionIndex, position, player); // ask server!
			actionList = getActionsForSelectedPosition(actionList, c, position, player);
		}
		List<String> stringActionList = new ArrayList<String>();
		for (int i = 0; i < actionList.size(); i++)
			stringActionList.add(actionList.get(i).toString());
		return stringActionList;
	}

	/**
	 * Adds actions to the given list, which are dependent to the position, the selected card is on. Only is called, if the given player is on turn.
	 * 
	 * @param actionList
	 * @param game
	 * @param c
	 * @param posID
	 * @param player
	 * @return
	 */
	private List<PlayerAction> getActionsForSelectedPosition(List<PlayerAction> actionList, Card c, PositionID posID, Player player) {
		CardScript script = c.getCardScript();

		// Check playedFromHand
		if (c instanceof PokemonCard || c instanceof TrainerCard || c instanceof EnergyCard) {
			if (script.canBePlayedFromHand() != null)
				actionList.add(script.canBePlayedFromHand());
		} else
			throw new IllegalArgumentException("Error: Card is not valid!");

		// Check attack1/2, pokemonPower, retreat:
		if (c instanceof PokemonCard) {
			PokemonCardScript pScript = (PokemonCardScript) script;

			// Check retreat:
			if (pScript.retreatCanBeExecuted())
				actionList.add(PlayerAction.RETREAT_POKEMON);

			// Check attacks:
			if (c.getCurrentPosition().getPositionID() == PositionID.BLUE_ACTIVEPOKEMON || c.getCurrentPosition().getPositionID() == PositionID.RED_ACTIVEPOKEMON) {
				for (String attName : pScript.getAttackNames()) {
					if (pScript.attackCanBeExecuted(attName)) {
						switch (pScript.getAttackNumber(attName)) {
						case 0:
							actionList.add(PlayerAction.ATTACK_1);
							break;
						case 1:
							actionList.add(PlayerAction.ATTACK_2);
							break;
						case -1:
							throw new IllegalArgumentException("Error: AttackName" + attName + " is not valid!");
						default:
							throw new IllegalArgumentException("Error: AttackName " + attName + " is out of range in the list");
						}
					}
				}
			}
			// Check pokemon power:
			for (String powerName : pScript.getPokemonPowerNames()) {
				if (pScript.pokemonPowerCanBeExecuted(powerName)) {
					switch (pScript.getPokemonPowerNumber(powerName)) {
					case 0:
						actionList.add(PlayerAction.POKEMON_POWER);
						break;
					case -1:
						throw new IllegalArgumentException("Error: powerName" + powerName + " is not valid!");
					default:
						throw new IllegalArgumentException("Error: powerName " + powerName + " is out of range in the list");
					}
				}
			}
		}
		return actionList;
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
		return this.playerOnTurn;
	}

	@Override
	public Player getPlayerRed() {
		return this.playerRed;
	}

	@Override
	public void setPlayerRed(Player playerRed) {
		this.playerRed = playerRed;
	}

	@Override
	public Player getPlayerBlue() {
		return this.playerBlue;
	}

	@Override
	public void setPlayerBlue(Player playerBlue) {
		this.playerBlue = playerBlue;
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
	public void sendCardMessageToAllPlayers(String string, List<Card> cardList, String sound) {
		// leave empty
	}

	@Override
	public void sendCardMessageToAllPlayers(String string, Card card, String sound) {
		// leave empty
	}

	@Override
	public void sendTextMessageToAllPlayers(String message, String sound) {
		// leave empty
	}

	@Override
	public void sendGameModelToAllPlayers(String sound) {
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

	public List<String> getAttacksForPosition(PositionID position) {
		Position pos = this.getPosition(position);
		if (pos.isEmpty() || !(pos.getTopCard() instanceof PokemonCard))
			return new ArrayList<String>(); // position empty or no arena position

		// Get the attacks from the card script:
		PokemonCard pokemon = (PokemonCard) pos.getTopCard();
		PokemonCardScript script = (PokemonCardScript) pokemon.getCardScript();
		return script.getAttackNames();
	}

	@Override
	public void sendSoundToAllPlayers(String sound) {
		// leave empty
	}

	public List<String> getPokePowerForPosition(PositionID posID) {
		Position pos = this.getPosition(posID);
		if (pos.isEmpty() || !(pos.getTopCard() instanceof PokemonCard))
			return new ArrayList<String>(); // position empty or no arena position

		// Get the attacks from the card script:
		PokemonCard pokemon = (PokemonCard) pos.getTopCard();
		PokemonCardScript script = (PokemonCardScript) pokemon.getCardScript();
		return script.getPokemonPowerNames();
	}

	@Override
	public boolean getRetreatExecuted() {
		return retreatExecuted;
	}

	@Override
	public void setRetreatExecuted(boolean value) {
		this.retreatExecuted = value;
	}
}
