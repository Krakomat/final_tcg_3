package network.server;

import java.util.ArrayList;
import java.util.List;

import com.jme3.network.HostedConnection;

import common.utilities.Threads;
import network.client.Player;
import network.tcp.borders.ServerBorder;
import network.tcp.borders.ServerListener;
import network.tcp.borders.ServerMain;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.GameState;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.TurnState;
import model.game.PokemonGameModelImpl;
import model.interfaces.GameModelUpdate;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.CardScript;
import model.scripting.abstracts.PokemonCardScript;

/**
 * Controls the general flow of the game. Does not change the game model, but only calls its game model instance to perform changes on the game model.
 * 
 * @author Michael
 *
 */
public class PokemonGameManagerImpl implements PokemonGameManager {

	protected String name, password;
	private TurnState turnState;
	private boolean moveMade;
	private PokemonGame gameModel;
	private ServerListener serverListener;
	private ServerMain serverMain; // only used for destroying the server from here

	PokemonGameManagerImpl(long id, String name, String password, ServerMain serverMain) {
		this.gameModel = new PokemonGameModelImpl(id);
		this.name = name;
		this.password = password;
		this.moveMade = false;
		this.serverMain = serverMain;
	}

	/**
	 * Is executed between steps in the game flow.
	 * 
	 * @param milis
	 */
	public void timeoutWait(long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the game. Returns true, if startup was successful, false otherwise. Also starts a new Thread, which controls the overall game flow.
	 * 
	 * @return
	 */
	public boolean startGame() {
		if (this.isFull()) {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					gameModel.initNewGame();
					runGame();
				}
			});
			t.setName(Threads.GAME_THREAD.toString());
			t.start();
			return true;
		} else
			System.err.println("You called initNewGame, but there is at least one player missing");

		return false;
	}

	/**
	 * Controls the overall game flow. Is started when everything is already set up, meaning, that all positions are empty except for the decks.
	 */
	protected void runGame() {
		this.timeoutWait(200);

		do {
			gameModel.nextTurn();
			this.turnState = TurnState.TURN_BEGIN; // Set turn state
			while (this.turnState != TurnState.TURN_END) {
				moveMade = false;
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						gameModel.getPlayerOnTurn().playerMakesMove();
					}
				});
				t.setName(Threads.PLAYER_MESSAGE_THREAD.toString());
				t.start();

				while (!moveMade) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (gameModel.getGameState() == GameState.RUNNING) {
				gameModel.executeEndTurn();
				gameModel.betweenTurns();
			}
		} while (gameModel.getGameState() == GameState.RUNNING); // while game not finished

		this.timeoutWait(4000); // Wait 4 seconds

		// Destroy server and clients:
		this.serverMain.exit();
	}

	/**
	 * Returns the game id of this game.
	 * 
	 * @return
	 */
	public long getGameID() {
		return gameModel.getGameID();
	}

	public boolean isFull() {
		return gameModel.getPlayerBlue() != null && gameModel.getPlayerRed() != null;
	}

	@Override
	public boolean connectAsPlayer(HostedConnection connection, String name, String password) {
		if (this.checkPassword(password)) {
			if (this.isFull())
				return false; // game full
			ServerBorder player = new ServerBorder(connection);
			this.serverListener.addConnection(connection, player);
			if (gameModel.getPlayerBlue() == null) {
				player.setColor(Color.BLUE); // assign color
				player.setName(name);
				gameModel.setPlayerBlue(player);
			} else if (gameModel.getPlayerRed() == null) {
				player.setColor(Color.RED); // assign color
				player.setName(name);
				gameModel.setPlayerRed(player);
			} else
				System.err.println("Fatal error in connectAsPlayer!");

			// Start game automatically when game is full:
			if (this.isFull())
				this.startGame(); // Creates a new Thread
			return true;
		}
		return false; // bad password
	}

	/**
	 * Checks if the given password matches with the password of the game and returns true, if that is the case.
	 * 
	 * @param password
	 * @return
	 */
	private boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	@Deprecated
	public List<String> getPlayerActions(int positionIndex, PositionID position, Player player) {
		boolean handCard = position == PositionID.BLUE_HAND || position == PositionID.RED_HAND;
		Card c = handCard ? gameModel.getPosition(position).getCardAtIndex(positionIndex) : gameModel.getPosition(position).getTopCard();
		ArrayList<PlayerAction> actionList = new ArrayList<PlayerAction>();
		Position pos = gameModel.getPosition(position);
		// If player on turn and position doesn't belong to enemy
		boolean playerOnTurn = gameModel.getPlayerOnTurn().getColor() == player.getColor();
		boolean playerOnTurnBlue = gameModel.getPlayerOnTurn().getColor() == Color.BLUE;
		boolean playerOnTurnRed = gameModel.getPlayerOnTurn().getColor() == Color.RED;
		boolean positionColorBlue = pos.getColor() == Color.BLUE;
		if (playerOnTurn && ((positionColorBlue && playerOnTurnBlue) || (!positionColorBlue && playerOnTurnRed))) {
			actionList = getActionsForSelectedPosition(actionList, c, position, player);
		}
		ArrayList<String> stringActionList = new ArrayList<String>();
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
	private ArrayList<PlayerAction> getActionsForSelectedPosition(ArrayList<PlayerAction> actionList, Card c, PositionID posID, Player player) {
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

	public void playerPlaysCard(Player player, int index) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Get the hand position:
				Position handPos = null;
				if (player.getColor() == Color.BLUE)
					handPos = gameModel.getPosition(PositionID.BLUE_HAND);
				else
					handPos = gameModel.getPosition(PositionID.RED_HAND);

				// Get the card:
				Card c = handPos.getCardAtIndex(index);

				// Check if card is allowed to be played:
				if (c.getCardScript().canBePlayedFromHand() == null)
					gameModel.playerLoses(player);

				// If card is a trainer card then send card message beforehand, so scripts don't need to implement this:
				if (c instanceof TrainerCard)
					gameModel.sendCardMessageToAllPlayers(player.getName() + " plays " + c.getName(), c);

				// Execute the card script:
				c.getCardScript().playFromHand();

				gameModel.sendGameModelToAllPlayers();
				gameModel.cleanDefeatedPositions();

				moveMade = true;
			}
		}).start();
	}

	@Override
	public void retreatPokemon(Player player) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Get the script:
				PokemonCard active = null;
				if (player.getColor() == Color.BLUE)
					active = (PokemonCard) gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard();
				else
					active = (PokemonCard) gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard();
				PokemonCardScript pScript = (PokemonCardScript) active.getCardScript();

				// Check is retreat is allowed:
				if (!pScript.retreatCanBeExecuted())
					gameModel.playerLoses(player);

				// Retreat:
				pScript.executeRetreat();

				moveMade = true;
			}
		}).start();
	}

	@Override
	@Deprecated
	public List<String> getAttacksForPosition(PositionID position) {
		Position pos = this.gameModel.getPosition(position);
		if (pos.isEmpty() || !(pos.getTopCard() instanceof PokemonCard))
			return new ArrayList<String>(); // position empty or no arena position

		// Get the attacks from the card script:
		PokemonCard pokemon = (PokemonCard) pos.getTopCard();
		PokemonCardScript script = (PokemonCardScript) pokemon.getCardScript();
		return script.getAttackNames();
	}

	public void executeAttack(Player player, String attackName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Get the script:
				PokemonCard active = null;
				if (player.getColor() == Color.BLUE)
					active = (PokemonCard) gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard();
				else
					active = (PokemonCard) gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard();
				PokemonCardScript pScript = (PokemonCardScript) active.getCardScript();

				// Get the attack name:
				if (pScript.getAttackNumber(attackName) == -1)
					throw new IllegalArgumentException("Error: attack name " + attackName + " is not valid in executeAttack()");

				// Check is attacking is allowed:
				if (!pScript.attackCanBeExecuted(attackName))
					gameModel.playerLoses(player);

				// Check if pokemon is confused and flip a coin, if attacking is allowed or the pokemon hurts itself:
				boolean attackAllowed = true;
				if (active.hasCondition(PokemonCondition.CONFUSED)) {
					gameModel.sendTextMessageToAllPlayers("Coinflip: On TAILS " + active.getName() + " hurts itself!");
					Coin c = gameModel.getAttackAction().flipACoin();
					gameModel.sendTextMessageToAllPlayers("Coin showed " + c);

					if (c == Coin.TAILS) {
						attackAllowed = false;
						// Damage attacker:
						gameModel.sendTextMessageToAllPlayers(active.getName() + " hurts itself!");
						gameModel.getAttackAction().inflictDamageToPosition(active.getElement(), active.getCurrentPosition().getPositionID(),
								active.getCurrentPosition().getPositionID(), 20, true);
					}
				}

				// Check if pokemon is blind and flip a coin, if attacking is allowed:
				if (attackAllowed && active.hasCondition(PokemonCondition.BLIND)) {
					gameModel.sendTextMessageToAllPlayers("Coinflip: " + active.getName() + " can't attack when tails");
					Coin c = gameModel.getAttackAction().flipACoin();
					gameModel.sendTextMessageToAllPlayers("Coin showed " + c);

					if (c == Coin.TAILS)
						attackAllowed = false;
				}

				if (attackAllowed) {
					// Execute the attack:
					gameModel.sendTextMessageToAllPlayers(active.getName() + " attacks with " + attackName);
					pScript.executeAttack(attackName);
				}
				gameModel.cleanDefeatedPositions();
				endTurn(player);
			}
		}).start();
	}

	@Override
	public List<String> getPokePowerForPosition(PositionID posID) {
		Position pos = this.gameModel.getPosition(posID);
		if (pos.isEmpty() || !(pos.getTopCard() instanceof PokemonCard))
			return new ArrayList<String>(); // position empty or no arena position

		// Get the attacks from the card script:
		PokemonCard pokemon = (PokemonCard) pos.getTopCard();
		PokemonCardScript script = (PokemonCardScript) pokemon.getCardScript();
		return script.getPokemonPowerNames();
	}

	@Override
	public void executePokemonPower(Player player, String powerName, PositionID posID) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				// Get the script:
				PokemonCard pokemon = null;
				if (player.getColor() == Color.BLUE)
					pokemon = (PokemonCard) gameModel.getPosition(posID).getTopCard();
				else
					pokemon = (PokemonCard) gameModel.getPosition(posID).getTopCard();
				PokemonCardScript pScript = (PokemonCardScript) pokemon.getCardScript();

				// Check the power name:
				if (pScript.getPokemonPowerNumber(powerName) == -1)
					throw new IllegalArgumentException("Error: attack name " + powerName + " is not valid in executePokemonPower()");

				// Check is activating is allowed:
				if (!pScript.pokemonPowerCanBeExecuted(powerName))
					gameModel.playerLoses(player);

				// Execute the power:
				gameModel.sendCardMessageToAllPlayers(pokemon.getName() + " activates " + powerName + "!", pokemon);
				pScript.executePokemonPower(powerName);

				moveMade = true; // Let player make his next move
			}
		}).start();
	}

	@Override
	public void endTurn(Player player) {
		this.turnState = TurnState.TURN_END;
		this.moveMade = true;
	}

	@Override
	public GameModelUpdate getGameModelForPlayer(Player player) {
		return gameModel.getGameModelForPlayer(player);
	}

	@Override
	public void setListener(ServerListener listener) {
		this.serverListener = listener;
	}
}
