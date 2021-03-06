package network.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.jme3.network.HostedConnection;

import common.utilities.Threads;
import network.client.Player;
import network.tcp.borders.ServerBorder;
import network.tcp.borders.ServerListener;
import network.tcp.borders.ServerMain;
import model.database.Card;
import model.database.Database;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.GameState;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.enums.TurnState;
import model.game.GameModelUpdate;
import model.game.GameParameters;
import model.game.PokemonGameModelImpl;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.CardScript;
import model.scripting.abstracts.PokemonCardScript;
import model.scripting.abstracts.TrainerCardScript;

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
	private ServerMain serverMain; // only used for destroying the server from
									// here
	private boolean debug_mode;

	PokemonGameManagerImpl(long id, String name, String password, ServerMain serverMain, int prizeCards) {
		this.gameModel = new PokemonGameModelImpl(id);
		GameParameters.PRIZE_NUMBER = prizeCards;
		((PokemonGameModelImpl) this.gameModel).setPokemonGameManager(this);
		this.name = name;
		this.password = password;
		this.moveMade = false;
		this.serverMain = serverMain;
		this.debug_mode = false;
	}

	public PokemonGameManagerImpl(long id, String name, String password, int prizeCards) {
		this.gameModel = new PokemonGameModelImpl(id);
		GameParameters.PRIZE_NUMBER = prizeCards;
		((PokemonGameModelImpl) this.gameModel).setPokemonGameManager(this);
		this.name = name;
		this.password = password;
		this.moveMade = false;
		this.serverMain = null;
		this.debug_mode = false;
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
	 * Only used for debugging!
	 */
	public void activateDebugMode() {
		this.debug_mode = true;
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
					try {
						if (!debug_mode)
							gameModel.initNewGame();
						runGame();
					} catch (Exception e) {
						e.printStackTrace();
						printErrorReport(gameModel, e);
					}
				}
			});
			t.setName(Threads.GAME_THREAD.toString());
			t.start();
			return true;
		} else
			System.err.println("You called initNewGame, but there is at least one player missing");

		return false;
	}

	protected void printErrorReport(PokemonGame gameModel, Exception error) {
		Calendar cal = Calendar.getInstance();
		String fileName = "";
		fileName = fileName + cal.get(Calendar.YEAR);
		fileName = fileName + "_" + cal.get(Calendar.MONTH);
		fileName = fileName + "_" + cal.get(Calendar.DAY_OF_MONTH);
		fileName = fileName + "_" + cal.get(Calendar.HOUR_OF_DAY);
		fileName = fileName + "_" + cal.get(Calendar.MINUTE);
		fileName = fileName + "_" + cal.get(Calendar.SECOND);
		fileName = fileName + "_" + cal.get(Calendar.MILLISECOND);
		File file = new File("data/ErrorReports/" + fileName + ".txt");
		try {
			FileWriter writer = new FileWriter(file, true);

			for (String s : ((PokemonGameModelImpl) gameModel).getMatchHistory()) {
				writer.write(s);
				writer.write(System.getProperty("line.separator"));
			}
			writer.write(System.getProperty("line.separator"));

			writer.write(error.toString());
			writer.write(System.getProperty("line.separator"));
			for (StackTraceElement element : error.getStackTrace()) {
				writer.write(element.toString());
				writer.write(System.getProperty("line.separator"));
			}

			writer.flush();
			writer.close();

			JOptionPane jop = new JOptionPane();
			jop.setMessage("An Error has occured! See the error report " + fileName + "! Close game?");
			jop.setOptionType(JOptionPane.YES_NO_OPTION);
			JDialog dialog = jop.createDialog(null, "Error");
			dialog.setAlwaysOnTop(true);
			dialog.setModal(true);
			dialog.setVisible(true);
			if ((int) jop.getValue() == JOptionPane.YES_OPTION)
				System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		} while (gameModel.getGameState() == GameState.RUNNING); // while game
																	// not
																	// finished

		// Destroy server and clients:
		if (serverMain != null)
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

	@Override
	public boolean connectAsLocalPlayer(Player player, String password) {
		if (this.checkPassword(password)) {
			if (this.isFull())
				return false; // game full
			if (gameModel.getPlayerBlue() == null) {
				player.setColor(Color.BLUE); // assign color
				gameModel.setPlayerBlue(player);
			} else if (gameModel.getPlayerRed() == null) {
				player.setColor(Color.RED); // assign color
				gameModel.setPlayerRed(player);
			} else
				System.err.println("Fatal error in connectAsPlayer!");

			// Start game automatically when game is full:
			if (this.isFull() && !debug_mode)
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
		try {
			if (position == PositionID.STADIUM) {
				List<String> actions = new ArrayList<>();
				Position pos = gameModel.getPosition(position);
				if (pos.isEmpty())
					return actions;
				TrainerCard stadiumCard = (TrainerCard) pos.getTopCard();
				TrainerCardScript stadiumScript = (TrainerCardScript) stadiumCard.getCardScript();
				if (stadiumScript.stadiumCanBeActivatedOnField(player)) {
					actions.add(PlayerAction.ACTIVATE_STADIUM_EFFECT.toString());
					return actions;
				}
				return actions;
			} else {
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
		} catch (Exception e) {
			e.printStackTrace();
			printErrorReport(gameModel, e);
		}
		return null;
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
						case 2:
							actionList.add(PlayerAction.ATTACK_3);
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
		try {
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

			// If card is a trainer card then send card message beforehand,
			// so scripts don't need to implement this:
			if (c instanceof TrainerCard)
				gameModel.sendCardMessageToAllPlayers(player.getName() + " plays " + c.getName(), c, Sounds.ACTIVATE_TRAINER);

			// Set the turn number for the card:
			c.setPlayedInTurn(gameModel.getTurnNumber());

			// Execute the card script:
			c.getCardScript().playFromHand();

			gameModel.sendGameModelToAllPlayers("");
			gameModel.cleanDefeatedPositions();

			moveMade = true;
		} catch (Exception e) {
			e.printStackTrace();
			printErrorReport(gameModel, e);
		}
	}

	@Override
	public void retreatPokemon(Player player) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
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
				} catch (Exception e) {
					e.printStackTrace();
					printErrorReport(gameModel, e);
				}
			}
		}).start();
	}

	@Override
	public List<String> getAttacksForPosition(PositionID position) {
		try {
			Position pos = this.gameModel.getPosition(position);
			if (pos.isEmpty() || !(pos.getTopCard() instanceof PokemonCard))
				return new ArrayList<String>(); // position empty or no arena
												// position

			// Get the attacks from the card script:
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			PokemonCardScript script = (PokemonCardScript) pokemon.getCardScript();
			return script.getAttackNames();
		} catch (Exception e) {
			e.printStackTrace();
			printErrorReport(gameModel, e);
		}
		return null;
	}

	public void executeAttack(Player player, String attackName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
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

					// Check if pokemon is confused and flip a coin, if attacking is
					// allowed or the pokemon hurts itself:
					boolean attackAllowed = true;
					if (active.hasCondition(PokemonCondition.CONFUSED)) {
						gameModel.sendTextMessageToAllPlayers("Coinflip: On TAILS " + active.getName() + " hurts itself!", "");
						Coin c = gameModel.getAttackAction().flipACoin();
						if (c == Coin.TAILS) {
							attackAllowed = false;
							// Damage attacker:
							gameModel.sendTextMessageToAllPlayers(active.getName() + " hurts itself!", "");
							gameModel.getAttackAction().inflictDamageToPosition(active.getElement(), active.getCurrentPosition().getPositionID(),
									active.getCurrentPosition().getPositionID(), 20, true);
						}
					}

					// Check if pokemon is blind and flip a coin, if attacking is
					// allowed:
					if (attackAllowed && active.hasCondition(PokemonCondition.BLIND)) {
						gameModel.sendTextMessageToAllPlayers("Coinflip: " + active.getName() + " can't attack when tails", "");
						Coin c = gameModel.getAttackAction().flipACoin();
						if (c == Coin.TAILS)
							attackAllowed = false;
					}

					if (attackAllowed) {
						// Check Vermillion City Gym:
						boolean selfDamage = false;
						if (!gameModel.getPosition(PositionID.STADIUM).isEmpty() && gameModel.getPosition(PositionID.STADIUM).getTopCard().getCardId().equals("00342")
								&& active.getName().contains("Lt. Surge")) {
							boolean answer = player.playerDecidesYesOrNo("Do you want to use the effect of Vermillion City Gym?");
							if (answer) {
								gameModel.sendCardMessageToAllPlayers(player.getName() + " activates the effect of Vermillion City Gym!", Database.createCard("00342"),
										Sounds.ACTIVATE_TRAINER);
								if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
									gameModel.getGameModelParameters().setVermillionCityGymAttackModifier(true);
									gameModel.sendTextMessageToAllPlayers("Attacks from " + active.getName() + " do 10 more damage this turn!", "");
								} else {
									selfDamage = true;
									gameModel.sendTextMessageToAllPlayers(active.getName() + " will hurt itself after attacking!", "");
								}
							}
						}

						// Check Koga's Ninja Trick on the defending pokemon:
						// Get the defending pokemon:
						PokemonCard defender = null;
						Player defendingPlayer = null;
						if (player.getColor() == Color.BLUE) {
							defender = (PokemonCard) gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard();
							defendingPlayer = gameModel.getPlayerRed();
						} else {
							defender = (PokemonCard) gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard();
							defendingPlayer = gameModel.getPlayerBlue();
						}
						if (defender.hasCondition(PokemonCondition.KOGAS_NINJA_TRICK) && gameModel.getFullBenchPositions(defendingPlayer.getColor()).size() > 0) {
							boolean answer = defendingPlayer.playerDecidesYesOrNo(active.getName() + " attacks with " + attackName + "! Do you want to use Koga's Ninja Trick?");
							if (answer) {
								PositionID benchPos = defendingPlayer.playerChoosesPositions(gameModel.getFullBenchPositions(defendingPlayer.getColor()), 1, true,
										"Choose a pokemon to swap with your active pokemon!").get(0);

								// Message clients:
								Card bench = gameModel.getPosition(benchPos).getTopCard();
								List<Card> cardList = new ArrayList<>();
								cardList.add(defender);
								cardList.add(bench);
								gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + defender.getName() + " with " + bench.getName() + "!", cardList, "");

								// Execute swap:
								gameModel.getAttackAction().swapPokemon(benchPos, defender.getCurrentPosition().getPositionID());
							}
						}

						// Execute the attack:
						gameModel.sendTextMessageToAllPlayers(active.getName() + " attacks with " + attackName, "");
						pScript.executeAttack(attackName);
						gameModel.getGameModelParameters().setVermillionCityGymAttackModifier(false);

						// Do 10 damage to yourself:
						if (selfDamage) {
							gameModel.getAttackAction().inflictDamageToPosition(active.getElement(), active.getCurrentPosition().getPositionID(),
									active.getCurrentPosition().getPositionID(), 10, true);
						}
					}
					gameModel.cleanDefeatedPositions();
					endTurn(player);
				} catch (Exception e) {
					e.printStackTrace();
					printErrorReport(gameModel, e);
				}
			}
		}).start();
	}

	@Override
	public List<String> getPokePowerForPosition(PositionID posID) {
		try {
			Position pos = this.gameModel.getPosition(posID);
			if (pos.isEmpty() || !(pos.getTopCard() instanceof PokemonCard))
				return new ArrayList<String>(); // position empty or no arena
												// position

			// Get the pokemon power from the card script:
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			PokemonCardScript script = (PokemonCardScript) pokemon.getCardScript();
			return script.getPokemonPowerNames();
		} catch (Exception e) {
			e.printStackTrace();
			printErrorReport(gameModel, e);
		}
		return null;
	}

	@Override
	public void executePokemonPower(Player player, String powerName, PositionID posID) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
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
					gameModel.sendCardMessageToAllPlayers(pokemon.getName() + " activates " + powerName + "!", pokemon, "");
					pScript.executePokemonPower(powerName);
					gameModel.sendGameModelToAllPlayers("");

					moveMade = true; // Let player make his next move
				} catch (Exception e) {
					e.printStackTrace();
					printErrorReport(gameModel, e);
				}
			}
		}).start();
	}

	@Override
	public void activateStadium(Player player) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Get the script:
					Card stadium = gameModel.getPosition(PositionID.STADIUM).getTopCard();
					TrainerCardScript stadiumScript = (TrainerCardScript) stadium.getCardScript();

					// Check is activating is allowed:
					if (!stadiumScript.stadiumCanBeActivatedOnField(player))
						gameModel.playerLoses(player);

					// Execute the power:
					gameModel.sendCardMessageToAllPlayers(player.getName() + " activates " + stadium.getName() + "!", stadium, "");
					stadiumScript.executeStadiumActiveEffect(player);

					moveMade = true; // Let player make his next move
				} catch (Exception e) {
					e.printStackTrace();
					printErrorReport(gameModel, e);
				}
			}
		}).start();
	}

	@Override
	public void endTurn(Player player) {
		this.turnState = TurnState.TURN_END;
		this.moveMade = true;
	}

	@Override
	public void surrender(Player player) {
		this.gameModel.sendTextMessageToAllPlayers(player.getName() + " surrenders!", "");
		this.gameModel.playerLoses(player);
		this.turnState = TurnState.TURN_END;
		this.moveMade = true;
	}

	@Override
	public GameModelUpdate getGameModelForPlayer(Player player, int version) {
		try {
			return gameModel.getGameModelForPlayer(player);
		} catch (Exception e) {
			e.printStackTrace();
			printErrorReport(gameModel, e);
		}
		return null;
	}

	/**
	 * Used only for debugging!
	 * 
	 * @return
	 */
	public PokemonGame getGameModel() {
		return this.gameModel;
	}

	@Override
	public void setListener(ServerListener listener) {
		this.serverListener = listener;
	}

	@Override
	public int getGameModelVersion() {
		return this.gameModel.getGameModelParameters().getGameModelVersion();
	}
}
