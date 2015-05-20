package model.game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import network.client.Player;
import model.database.Card;
import model.database.Database;
import model.database.Deck;
import model.database.DynamicPokemonCondition;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.GameState;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.GameField;
import model.interfaces.GameModelUpdate;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.CardScriptFactory;
import model.scripting.abstracts.PokemonCardScript;

public class PokemonGameModelImpl implements PokemonGame {

	protected long gameID;
	protected int cardGameIDCounter;
	protected Player playerOnTurn;
	protected Player playerRed, playerBlue;
	protected GameField gameField;
	protected AttackAction attackAction;
	protected AttackCondition attackCondition;
	protected Map<Integer, Card> cardMap;
	protected CardScriptFactory cardScriptFactory;
	protected GameModelParameters gameModelParameters;

	/**
	 * The given player initializes a new PokemonGameModel with the given id.
	 * 
	 * @param id
	 * @param player
	 */
	public PokemonGameModelImpl(long id) {
		cardGameIDCounter = 0;
		gameID = id;
		playerOnTurn = null;
		playerRed = null;
		playerBlue = null;
		gameField = new GameFieldImpl();
		cardMap = new HashMap<>();
		attackCondition = new AttackCondition(this);
		attackAction = new AttackAction(this);
		cardScriptFactory = CardScriptFactory.getInstance();
		gameModelParameters = new GameModelParameters();
	}

	@Override
	public void initNewGame() {
		this.gameField = new GameFieldImpl();
		this.cardGameIDCounter = 0;
		List<Card> blueCards = getDeckInstance(playerBlue.getDeck());
		List<Card> redCards = getDeckInstance(playerRed.getDeck());
		Position blueDeck = this.getPosition(PositionID.BLUE_DECK);
		Position redDeck = this.getPosition(PositionID.RED_DECK);
		for (int i = 0; i < blueCards.size(); i++) {
			blueCards.get(i).setCurrentPosition(blueDeck);
			blueDeck.addToPosition(blueCards.get(i));
		}
		for (int i = 0; i < redCards.size(); i++) {
			redCards.get(i).setCurrentPosition(redDeck);
			redDeck.addToPosition(redCards.get(i));
		}
		blueDeck.setVisible(false, Color.BLUE);
		blueDeck.setVisible(false, Color.RED);
		redDeck.setVisible(false, Color.BLUE);
		redDeck.setVisible(false, Color.RED);

		// Notify players that the game has started:
		for (Player p : this.getPlayerList())
			p.startGame();

		this.sendGameModelToPlayers(this.getPlayerList(), "");
		this.sendTextMessageToPlayers(getPlayerList(), "The game has started", "");
		this.gameModelParameters.setGameState(GameState.RUNNING);

		this.initDraw();
		this.initPrizes();
		this.initStartPokemon();
		this.initBenchPokemon();
		this.startupCoinflip();
	}

	/**
	 * Simulates the initial draw phase. Both players draw 7 cards. If a player doesn't have any basic pokemon in his hand, he shuffles his hand into his deck and
	 * draws once more.
	 */
	private void initDraw() {
		this.sendTextMessageToPlayers(getPlayerList(), "Each player shuffles his deck.", Sounds.SHUFFLE);
		this.timeoutWait(200);
		Position blueDeck = this.getPosition(PositionID.BLUE_DECK);
		blueDeck.shuffle();
		Position redDeck = this.getPosition(PositionID.RED_DECK);
		redDeck.shuffle();

		this.sendGameModelToPlayers(this.getPlayerList(), "");

		this.sendTextMessageToPlayers(getPlayerList(), "Each player draws 7 cards.", "");

		boolean playerBlueHandOk = false, playerRedHandOk = false, bothFinished = false;
		while (!(bothFinished)) {
			// Each player draws 7 cards:
			for (int i = 0; i < 7; i++) {
				if (!playerBlueHandOk)
					this.attackAction.playerDrawsCards(1, playerBlue);
				if (!playerRedHandOk)
					this.attackAction.playerDrawsCards(1, playerRed);
				// this.sendGameModelToPlayers(this.getPlayerList(), "");
				this.timeoutWait(200);
			}

			this.timeoutWait(200);

			if (this.attackCondition.positionHasBasicPokemon(PositionID.BLUE_HAND))
				playerBlueHandOk = true;
			if (this.attackCondition.positionHasBasicPokemon(PositionID.RED_HAND))
				playerRedHandOk = true;

			if (!playerBlueHandOk && !playerRedHandOk)
				this.sendTextMessageToPlayers(getPlayerList(), "No player has basic pokemon in his hand!", "");
			else if (!playerBlueHandOk && playerRedHandOk)
				this.sendTextMessageToPlayers(getPlayerList(), playerBlue.getName() + " has no basic pokemon in his hand!", "");
			else if (playerBlueHandOk && !playerRedHandOk)
				this.sendTextMessageToPlayers(getPlayerList(), playerRed.getName() + " has no basic pokemon in his hand!", "");
			else
				bothFinished = true;

			// Show hand, if no basic pokemon drawn:
			if (!playerBlueHandOk) {
				this.getPosition(PositionID.BLUE_HAND).setVisible(true, Color.RED);
			}
			if (!playerRedHandOk) {
				this.getPosition(PositionID.RED_HAND).setVisible(true, Color.BLUE);
			}
			// Update GameModel:
			this.sendGameModelToPlayers(this.getPlayerList(), "");

			if (!playerBlueHandOk || !playerRedHandOk)
				this.timeoutWait(5000);// Wait so players can look at the hands
			this.getPosition(PositionID.BLUE_HAND).setVisible(false, Color.RED);
			this.getPosition(PositionID.RED_HAND).setVisible(false, Color.BLUE);
			// Update GameModel:
			this.sendGameModelToPlayers(this.getPlayerList(), "");

			// Shuffle hand into deck, if no basic pokemon drawn
			if (!playerBlueHandOk) {
				this.attackAction.playerPutsAllHandCardsOnDeck(playerBlue);
				blueDeck.shuffle();
				this.sendSoundToAllPlayers(Sounds.SHUFFLE);
			}
			if (!playerRedHandOk) {
				this.attackAction.playerPutsAllHandCardsOnDeck(playerRed);
				redDeck.shuffle();
				this.sendSoundToAllPlayers(Sounds.SHUFFLE);
			}
			// Update GameModel:
			this.sendGameModelToPlayers(this.getPlayerList(), "");
		}
	}

	/**
	 * Lays down the amount of price cards for each player.
	 */
	private void initPrizes() {
		// System.out.println("Init prizes...");
		this.sendTextMessageToPlayers(getPlayerList(), "Each player lays down " + GameParameters.PRIZE_NUMBER + " price cards", "");
		Position blueDeck = this.getPosition(PositionID.BLUE_DECK);
		Position redDeck = this.getPosition(PositionID.RED_DECK);

		for (int i = 1; i <= GameParameters.PRIZE_NUMBER; i++) {
			this.attackAction.moveCard(blueDeck.getPositionID(), PositionID.valueOf("BLUE_PRICE_" + i), blueDeck.getTopCard().getGameID(), true);
			this.attackAction.moveCard(redDeck.getPositionID(), PositionID.valueOf("RED_PRICE_" + i), redDeck.getTopCard().getGameID(), true);
		}
		// Update GameModel:
		this.sendGameModelToPlayers(this.getPlayerList(), "");
		this.timeoutWait(200);

		// System.out.println("Init prizes finished!");
	}

	/**
	 * Only used in initStartPokemon!
	 */
	private Card blueChosenActive;

	/**
	 * Both players have to choose their starting pokemon.
	 */
	private void initStartPokemon() {
		// System.out.println("Init choose start Pokemon...");
		List<Card> chooseList = this.getBasicPokemonOnPosition(PositionID.BLUE_HAND);

		blueChosenActive = null;
		new Thread(new Runnable() {
			@Override
			public void run() {
				blueChosenActive = playerBlue.playerChoosesCards(chooseList, 1, true, "Choose an active Pokemon!").get(0);
			}
		}).start();
		Card redChosenActive = playerRed.playerChoosesCards(this.getBasicPokemonOnPosition(PositionID.RED_HAND), 1, true, "Choose an active Pokemon!").get(0);

		// Wait:
		while (blueChosenActive == null)
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		System.out.println("Finished Waiting");

		// Both players have chosen:
		blueChosenActive.setPlayedInTurn(0);
		blueChosenActive.setVisibleForPlayerBlue(false);
		this.attackAction.moveCard(PositionID.BLUE_HAND, PositionID.BLUE_ACTIVEPOKEMON, blueChosenActive.getGameID(), true);
		redChosenActive.setPlayedInTurn(0);
		redChosenActive.setVisibleForPlayerRed(false);
		this.attackAction.moveCard(PositionID.RED_HAND, PositionID.RED_ACTIVEPOKEMON, redChosenActive.getGameID(), true);

		// Update GameModel:
		// this.sendGameModelToPlayers(this.getPlayerList());

		// System.out.println("Init choose start Pokemon finished...");
	}

	private List<Card> blueChosenBench;

	/**
	 * Both players have to choose their initial bench pokemon.
	 */
	private void initBenchPokemon() {
		// System.out.println("Init choose bench Pokemon...");

		blueChosenBench = null;
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (getBasicPokemonOnPosition(PositionID.BLUE_HAND).size() > 0)
					blueChosenBench = playerBlue.playerChoosesCards(getBasicPokemonOnPosition(PositionID.BLUE_HAND), 5, false,
							"Choose up to 5 Pokemon for your bench...");
				else
					blueChosenBench = new ArrayList<>(); // so the server won't get stuck
			}
		}).start();

		List<Card> redChosenBench = new ArrayList<Card>();
		if (this.getBasicPokemonOnPosition(PositionID.RED_HAND).size() > 0)
			redChosenBench = playerRed.playerChoosesCards(this.getBasicPokemonOnPosition(PositionID.RED_HAND), 5, false, "Choose up to 5 Pokemon for your bench...");

		// Wait:
		while (blueChosenBench == null)
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		// Both players have chosen:
		for (int i = 0; i < blueChosenBench.size(); i++) {
			this.attackAction.moveCard(PositionID.BLUE_HAND, PositionID.getBenchPosition(Color.BLUE, i + 1), blueChosenBench.get(i).getGameID(), true);
			blueChosenBench.get(i).setPlayedInTurn(0);
		}
		for (int i = 0; i < redChosenBench.size(); i++) {
			this.attackAction.moveCard(PositionID.RED_HAND, PositionID.getBenchPosition(Color.RED, i + 1), redChosenBench.get(i).getGameID(), true);
			redChosenBench.get(i).setPlayedInTurn(0);
		}

		// Update GameModel:
		// this.sendGameModelToPlayers(this.getPlayerList());

		// Make Arena visible for enemies:
		this.makePositionVisibleForAllPlayers(PositionID.BLUE_ACTIVEPOKEMON);
		this.makePositionVisibleForAllPlayers(PositionID.RED_ACTIVEPOKEMON);
		this.makePositionVisibleForAllPlayers(PositionID.BLUE_BENCH_1);
		this.makePositionVisibleForAllPlayers(PositionID.BLUE_BENCH_2);
		this.makePositionVisibleForAllPlayers(PositionID.BLUE_BENCH_3);
		this.makePositionVisibleForAllPlayers(PositionID.BLUE_BENCH_4);
		this.makePositionVisibleForAllPlayers(PositionID.BLUE_BENCH_5);
		this.makePositionVisibleForAllPlayers(PositionID.RED_BENCH_1);
		this.makePositionVisibleForAllPlayers(PositionID.RED_BENCH_2);
		this.makePositionVisibleForAllPlayers(PositionID.RED_BENCH_3);
		this.makePositionVisibleForAllPlayers(PositionID.RED_BENCH_4);
		this.makePositionVisibleForAllPlayers(PositionID.RED_BENCH_5);
		this.makePositionVisibleForAllPlayers(PositionID.BLUE_DISCARDPILE);
		this.makePositionVisibleForAllPlayers(PositionID.RED_DISCARDPILE);

		// Update GameModel:
		this.sendGameModelToPlayers(this.getPlayerList(), "");

		// System.out.println("Init choose bench Pokemon finished...");
	}

	/**
	 * Makes the given position visible for all players.
	 * 
	 * @param posID
	 */
	private void makePositionVisibleForAllPlayers(PositionID posID) {
		this.getPosition(posID).setVisible(true, Color.BLUE);
		this.getPosition(posID).setVisible(true, Color.RED);
	}

	/**
	 * Waits until both players are ready and flips a coin to determine, who will begin the game.
	 * 
	 */
	private void startupCoinflip() {
		Coin coinflip = this.attackAction.flipACoin();

		switch (coinflip) {
		case HEADS:
			this.playerOnTurn = playerRed; // Gets switched before the real first turn, because of method nextTurn()!
			this.sendTextMessageToPlayers(getPlayerList(), "Coin shows 'Heads' - " + playerBlue.getName() + " begins.", "");
			break;
		case TAILS:
			this.playerOnTurn = playerBlue; // Gets switched before the real first turn, because of method nextTurn()!
			this.sendTextMessageToPlayers(getPlayerList(), "Coin shows 'Tails' - " + playerRed.getName() + " begins.", "");
			break;
		default:
			System.err.println("Coinflip-Error: Coin shows " + coinflip);
		}
		this.timeoutWait(1000);
	}

	/**
	 * Cleans all positions with defeated pokemon on it. Also forces players to take prices and checks the winning conditions. Does nothing if the given list is
	 * empty.
	 * 
	 * @param defeatedPositions
	 */
	public void cleanDefeatedPositions() {
		List<PositionID> defeatedPositions = this.checkForDefeatedPokemon();
		if (!defeatedPositions.isEmpty()) {
			// Message players for knocked out pokemon:
			for (PositionID posID : defeatedPositions) {
				Card card = this.getPosition(posID).getTopCard();
				this.sendTextMessageToPlayers(getPlayerList(), card.getName() + " is knocked out!", "");
			}

			// Count prices for each player:
			int pricesRed = 0;
			int pricesBlue = 0;
			for (PositionID posID : defeatedPositions) {
				Position pos = this.getPosition(posID);
				PokemonCard pokemon = (PokemonCard) pos.getTopCard();
				// Increase price count only if pokemon isn't clefary doll:
				if (!pokemon.getCardId().equals("00103")) {
					if (pos.getColor() == Color.BLUE)
						pricesRed++;
					else if (pos.getColor() == Color.RED)
						pricesBlue++;
					else
						System.err.println("Error in method cleanDefeatedPositions(...) of class PokemonGameManagerImpl: position has no color!");
				}
			}
			// Take prices:
			if (pricesBlue > 0)
				this.playerTakesPrice(Color.BLUE, pricesBlue);
			if (pricesRed > 0)
				this.playerTakesPrice(Color.RED, pricesRed);

			// Remove cards from positions:
			for (PositionID posID : defeatedPositions)
				cleanDefeatedPosition(posID);

			// Send gameModel:
			this.sendGameModelToPlayers(this.getPlayerList(), "");

			// Check, if game is ending:
			GameState state = this.checkForLoser();
			if (state == GameState.BLUE_WON)
				this.playerLoses(playerRed);
			if (state == GameState.RED_WON)
				this.playerLoses(playerBlue);
			if (state == GameState.TIE) {
				this.sendTextMessageToPlayers(this.getPlayerList(), "Game results in a TIE!", "");
				this.gameModelParameters.setGameState(GameState.TIE);
			}

			// Choose new active pokemon:
			if (this.gameModelParameters.getGameState() == GameState.RUNNING) {
				PositionID blueNewActive = null;
				PositionID redNewActive = null;
				if (this.getPosition(PositionID.BLUE_ACTIVEPOKEMON).isEmpty())
					blueNewActive = this.playerBlue.playerChoosesPositions(this.getFullArenaPositions(Color.BLUE), 1, true, "Choose a new active pokemon!").get(0);
				if (this.getPosition(PositionID.RED_ACTIVEPOKEMON).isEmpty())
					redNewActive = this.playerRed.playerChoosesPositions(this.getFullArenaPositions(Color.RED), 1, true, "Choose a new active pokemon!").get(0);

				if (blueNewActive != null)
					this.attackAction.movePokemonToPosition(blueNewActive, PositionID.BLUE_ACTIVEPOKEMON);
				if (redNewActive != null)
					this.attackAction.movePokemonToPosition(redNewActive, PositionID.RED_ACTIVEPOKEMON);

				// Send gameModel:
				this.sendGameModelToPlayers(this.getPlayerList(), "");
			}
		}
	}

	private void cleanDefeatedPosition(PositionID positionID) {
		List<Card> cardList = this.getPosition(positionID).getCards();
		if (cardList.size() > 0) {
			int cardListSize = cardList.size();
			if (this.getPosition(positionID).getColor() == Color.BLUE) {
				for (int i = cardListSize - 1; i >= 0; i--) {
					Card card = cardList.get(i);
					if (card instanceof PokemonCard) // reset pokemon attributes
						((PokemonCard) card).resetDynamicAttributes();
					this.attackAction.moveCard(positionID, PositionID.BLUE_DISCARDPILE, card.getGameID(), true);
				}
			} else {
				for (int i = cardListSize - 1; i >= 0; i--) {
					Card card = cardList.get(i);
					if (card instanceof PokemonCard) // reset pokemon attributes
						((PokemonCard) card).resetDynamicAttributes();
					this.attackAction.moveCard(positionID, PositionID.RED_DISCARDPILE, card.getGameID(), true);
				}
			}
		}
	}

	/**
	 * Checks the arena field for defeated pokemon and adds all found positions to a list, which is returned.
	 *
	 * @return
	 */
	private ArrayList<PositionID> checkForDefeatedPokemon() {
		ArrayList<PositionID> defeatedPokemon = new ArrayList<PositionID>();

		if (pokemonDefeated(PositionID.BLUE_ACTIVEPOKEMON))
			defeatedPokemon.add(PositionID.BLUE_ACTIVEPOKEMON);
		if (pokemonDefeated(PositionID.BLUE_BENCH_1))
			defeatedPokemon.add(PositionID.BLUE_BENCH_1);
		if (pokemonDefeated(PositionID.BLUE_BENCH_2))
			defeatedPokemon.add(PositionID.BLUE_BENCH_2);
		if (pokemonDefeated(PositionID.BLUE_BENCH_3))
			defeatedPokemon.add(PositionID.BLUE_BENCH_3);
		if (pokemonDefeated(PositionID.BLUE_BENCH_4))
			defeatedPokemon.add(PositionID.BLUE_BENCH_4);
		if (pokemonDefeated(PositionID.BLUE_BENCH_5))
			defeatedPokemon.add(PositionID.BLUE_BENCH_5);
		if (pokemonDefeated(PositionID.RED_ACTIVEPOKEMON))
			defeatedPokemon.add(PositionID.RED_ACTIVEPOKEMON);
		if (pokemonDefeated(PositionID.RED_BENCH_1))
			defeatedPokemon.add(PositionID.RED_BENCH_1);
		if (pokemonDefeated(PositionID.RED_BENCH_2))
			defeatedPokemon.add(PositionID.RED_BENCH_2);
		if (pokemonDefeated(PositionID.RED_BENCH_3))
			defeatedPokemon.add(PositionID.RED_BENCH_3);
		if (pokemonDefeated(PositionID.RED_BENCH_4))
			defeatedPokemon.add(PositionID.RED_BENCH_4);
		if (pokemonDefeated(PositionID.RED_BENCH_5))
			defeatedPokemon.add(PositionID.RED_BENCH_5);

		return defeatedPokemon;
	}

	private boolean pokemonDefeated(PositionID posID) {
		if (this.getPosition(posID).isEmpty())
			return false;
		PokemonCard pokemonCard = (PokemonCard) this.getPosition(posID).getTopCard();
		if (pokemonCard.hasCondition(PokemonCondition.KNOCKOUT))
			return true;
		return false;
	}

	/**
	 * The given player takes the given number of price cards.
	 * 
	 * @param playerColor
	 * @param number
	 */
	private void playerTakesPrice(Color playerColor, int number) {
		if (playerColor == Color.BLUE) {
			ArrayList<PositionID> pricePositions = gameField.getNonEmptyPriceList(playerColor);
			// Normalize price number:
			if (number > pricePositions.size())
				number = pricePositions.size();
			// Message to all players:
			this.sendTextMessageToPlayers(getPlayerList(), playerBlue.getName() + " chooses " + number + " price cards.", "");
			// Choose price:
			List<PositionID> chosenPositions = this.playerBlue.playerChoosesPositions(pricePositions, number, true, "Choose " + number + " price cards");
			for (PositionID posID : chosenPositions) {
				// Get card:
				Card c = this.getPosition(posID).getTopCard();
				// Move price card:
				this.attackAction.moveCard(posID, PositionID.BLUE_HAND, c.getGameID(), true);
				c.setVisibleForPlayerBlue(true);
				this.attackAction.checkAndResolveFullHand(PositionID.BLUE_HAND, playerBlue);
			}
		} else if (playerColor == Color.RED) {
			ArrayList<PositionID> pricePositions = gameField.getNonEmptyPriceList(playerColor);
			// Normalize price number:
			if (number > pricePositions.size())
				number = pricePositions.size();
			// Message to all players:
			this.sendTextMessageToPlayers(getPlayerList(), playerRed.getName() + " chooses " + number + " price cards.", "");
			// Choose price:
			List<PositionID> chosenPositions = this.playerRed.playerChoosesPositions(pricePositions, number, true, "Choose " + number + " price cards");
			for (PositionID posID : chosenPositions) {
				// Get card:
				Card c = this.getPosition(posID).getTopCard();
				// Move price card:
				this.attackAction.moveCard(posID, PositionID.RED_HAND, c.getGameID(), true);
				c.setVisibleForPlayerRed(true);
				this.attackAction.checkAndResolveFullHand(PositionID.RED_HAND, playerRed);
			}
		} else
			System.err.println("Error in method playerTakesPrice(...) of class PokemonGameManagerImpl: Wrong color set");
	}

	/**
	 * Checks if any of both player lost the game, sets and returns the {@link GameState}, which has to occur.
	 * 
	 * @return
	 */
	private GameState checkForLoser() {
		// Count full price positions:
		int bluePricesLeft = 0;
		int redPricesLeft = 0;
		if (!gameField.getBluePrice1().isEmpty())
			bluePricesLeft++;
		if (!gameField.getBluePrice2().isEmpty())
			bluePricesLeft++;
		if (!gameField.getBluePrice3().isEmpty())
			bluePricesLeft++;
		if (!gameField.getBluePrice4().isEmpty())
			bluePricesLeft++;
		if (!gameField.getBluePrice5().isEmpty())
			bluePricesLeft++;
		if (!gameField.getBluePrice6().isEmpty())
			bluePricesLeft++;

		if (!gameField.getRedPrice1().isEmpty())
			redPricesLeft++;
		if (!gameField.getRedPrice2().isEmpty())
			redPricesLeft++;
		if (!gameField.getRedPrice3().isEmpty())
			redPricesLeft++;
		if (!gameField.getRedPrice4().isEmpty())
			redPricesLeft++;
		if (!gameField.getRedPrice5().isEmpty())
			redPricesLeft++;
		if (!gameField.getRedPrice6().isEmpty())
			redPricesLeft++;

		// Select GameState to return:
		if (bluePricesLeft == 0 && redPricesLeft == 0)
			return GameState.TIE;
		if (bluePricesLeft == 0)
			return GameState.BLUE_WON;
		if (redPricesLeft == 0)
			return GameState.RED_WON;

		// Check if any player doesn't have any pokemon:
		if (this.getFullArenaPositions(Color.BLUE).size() == 0)
			return GameState.RED_WON;
		if (this.getFullArenaPositions(Color.RED).size() == 0)
			return GameState.BLUE_WON;
		return GameState.RUNNING;
	}

	/**
	 * Switches the turn and updates the gameModel for both players. Starts the next turn, by initiating the draw phase.
	 */
	public void nextTurn() {
		if (playerOnTurn.getColor() == Color.BLUE)
			this.playerOnTurn = playerRed;
		else
			this.playerOnTurn = playerBlue;
		this.sendTextMessageToPlayers(getPlayerList(), playerOnTurn.getName() + " 's turn...", Sounds.ON_TURN);
		this.gameModelParameters.setTurnNumber(this.gameModelParameters.getTurnNumber() + 1); // Increase turn number

		// Draw a card or end game if the player is not able to draw:
		if (this.attackAction.playerDrawsCards(1, playerOnTurn)) {
			this.gameModelParameters.setEnergyPlayed(false);
			this.gameModelParameters.setRetreatExecuted(false);
			// Update GameModel:
			this.sendGameModelToPlayers(this.getPlayerList(), "");
		} else
			this.playerLoses(playerOnTurn);

		// Call executePreTurnActions() for all card scripts in the game model:
		for (Integer gameID : this.cardMap.keySet()) {
			Card card = this.cardMap.get(gameID);
			card.getCardScript().executePreTurnActions();
		}
	}

	/**
	 * Makes actions that have to be executed between the turns, e.g. applying poison damage. Does nothing, if there is nothing to do in between turns.
	 */
	public void betweenTurns() {
		// Update isAllowedToPlayTrainerCards value in gameModelParameters:
		short value = gameModelParameters.isAllowedToPlayTrainerCards();
		if (value > 0) {
			value--;
			this.gameModelParameters.setAllowedToPlayTrainerCards(value);
		}

		List<PositionID> posList = this.getFullArenaPositions(playerBlue.getColor());
		for (PositionID posID : posList)
			updateConditions(posID);

		posList = this.getFullArenaPositions(playerRed.getColor());
		for (PositionID posID : posList)
			updateConditions(posID);

		updatePoisoned(PositionID.BLUE_ACTIVEPOKEMON);
		updatePoisoned(PositionID.RED_ACTIVEPOKEMON);
		updateSleeping(PositionID.BLUE_ACTIVEPOKEMON);
		updateSleeping(PositionID.RED_ACTIVEPOKEMON);
		updateConfused(PositionID.BLUE_ACTIVEPOKEMON);
		updateConfused(PositionID.RED_ACTIVEPOKEMON);
		updateBlockedAttacks();
		this.cleanDefeatedPositions();
	}

	/**
	 * Calls every PokemonCardScript in play to decrease the blocking of attacks.
	 */
	private void updateBlockedAttacks() {
		for (PositionID posID : this.getFullArenaPositions(Color.BLUE)) {
			PokemonCard pokemon = (PokemonCard) this.getPosition(posID).getTopCard();
			PokemonCardScript script = (PokemonCardScript) pokemon.getCardScript();
			script.decreaseBlockingTimeForAttacks(); // Call here
		}
		for (PositionID posID : this.getFullArenaPositions(Color.RED)) {
			PokemonCard pokemon = (PokemonCard) this.getPosition(posID).getTopCard();
			PokemonCardScript script = (PokemonCardScript) pokemon.getCardScript();
			script.decreaseBlockingTimeForAttacks(); // Call here
		}
	}

	/**
	 * Updates the counter for conditions on this position.
	 * 
	 * @param positionID
	 */
	private void updateConditions(PositionID positionID) {
		Position pos = this.getPosition(positionID);
		PokemonCard pokemon = (PokemonCard) pos.getTopCard();
		List<DynamicPokemonCondition> removedConditions = new ArrayList<>();
		for (DynamicPokemonCondition condition : pokemon.getConditions()) {
			// Decrease turn counter:
			condition.setRemainingTurns(condition.getRemainingTurns() - 1);
			if (condition.getRemainingTurns() == 0) {
				removedConditions.add(condition);
				if (condition.getCondition().equals(PokemonCondition.PARALYZED))
					this.sendTextMessageToPlayers(this.getPlayerList(), "Between turns: " + pokemon.getName() + " is not paralyzed anymore!", "");
			}
		}
		// Remove conditions:
		for (DynamicPokemonCondition condition : removedConditions)
			pokemon.getConditions().remove(condition);

		this.sendGameModelToPlayers(getPlayerList(), "");
	}

	/**
	 * Updates the counter for poisoned on this position.
	 * 
	 * @param positionID
	 */
	private void updatePoisoned(PositionID positionID) {
		if (this.attackCondition.pokemonHasCondition(positionID, PokemonCondition.POISONED)
				|| this.attackCondition.pokemonHasCondition(positionID, PokemonCondition.TOXIC)) {
			Position pos = this.getPosition(positionID);
			DynamicPokemonCondition poisonedCondition = null;
			DynamicPokemonCondition toxicCondition = null;
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			for (DynamicPokemonCondition condition : pokemon.getConditions()) {
				if (condition.getCondition().equals(PokemonCondition.POISONED))
					poisonedCondition = condition;
				if (condition.getCondition().equals(PokemonCondition.TOXIC))
					toxicCondition = condition;
			}
			if (poisonedCondition != null) {
				// Apply damage:
				this.sendTextMessageToPlayers(this.getPlayerList(), "Between turns: " + pokemon.getName() + " is poisoned!", "");
				this.attackAction.inflictDamageToPosition(Element.COLORLESS, null, positionID, 10, false);
			}
			if (toxicCondition != null) {
				// Apply damage:
				this.sendTextMessageToPlayers(this.getPlayerList(), "Between turns: " + pokemon.getName() + " is toxicated!", "");
				this.attackAction.inflictDamageToPosition(Element.COLORLESS, null, positionID, 20, false);
			}
		}
	}

	/**
	 * Updates the counter for sleeping on this position.
	 * 
	 * @param positionID
	 */
	private void updateSleeping(PositionID positionID) {
		if (this.attackCondition.pokemonHasCondition(positionID, PokemonCondition.ASLEEP)) {
			Position pos = this.getPosition(positionID);
			DynamicPokemonCondition asleepCondition = null;
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			for (DynamicPokemonCondition condition : pokemon.getConditions()) {
				if (condition.getCondition().equals(PokemonCondition.ASLEEP))
					asleepCondition = condition;
			}
			if (asleepCondition != null) {
				this.sendTextMessageToPlayers(this.getPlayerList(), "Between turns: Flip a coin to check if " + pokemon.getName() + " woke up...", "");
				Random rand = new SecureRandom();
				boolean result = rand.nextInt(2) == 1 ? true : false;
				if (result) {
					this.sendTextMessageToPlayers(this.getPlayerList(), pokemon.getName() + " woke up...", "");
					// Remove condition:
					pokemon.getConditions().remove(asleepCondition);
					this.sendGameModelToPlayers(getPlayerList(), "");
				} else
					this.sendTextMessageToPlayers(this.getPlayerList(), pokemon.getName() + " is still sleeping...", "");
			}
		}
	}

	/**
	 * Updates the counter for confused on this position.
	 * 
	 * @param positionID
	 */
	private void updateConfused(PositionID positionID) {
		if (this.attackCondition.pokemonHasCondition(positionID, PokemonCondition.CONFUSED)) {
			Position pos = this.getPosition(positionID);
			DynamicPokemonCondition confusedCondition = null;
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			for (DynamicPokemonCondition condition : pokemon.getConditions()) {
				if (condition.getRemainingTurns() == 0)
					confusedCondition = condition;
			}
			if (confusedCondition != null) {
				this.sendTextMessageToPlayers(this.getPlayerList(), "Between turns: If tails, then " + pokemon.getName() + " damages itself...", "");
				Random rand = new SecureRandom();
				boolean result = rand.nextInt(2) == 0 ? true : false;
				if (!result) {
					this.sendTextMessageToPlayers(this.getPlayerList(), "Between turns: " + pokemon.getName() + " damages itself!", "");
					this.attackAction.inflictDamageToPosition(Element.COLORLESS, null, positionID, 20, false);
				}
			}
		}
	}

	@Override
	public void executeEndTurn() {
		this.sendTextMessageToPlayers(getPlayerList(), this.playerOnTurn.getName() + " ends his turn", "");

		// Call executeEndTurnActions() for all card scripts in the game model:
		for (Integer gameID : this.cardMap.keySet()) {
			Card card = this.cardMap.get(gameID);
			card.getCardScript().executeEndTurnActions();
		}
	}

	/**
	 * The given player loses the game.
	 * 
	 * @param player
	 */
	public void playerLoses(Player player) {
		if (player.getColor() == Color.BLUE)
			this.gameModelParameters.setGameState(GameState.RED_WON);
		else
			this.gameModelParameters.setGameState(GameState.BLUE_WON);
		this.sendGameModelToPlayers(this.getPlayerList(), "");
		this.sendTextMessageToPlayers(this.getPlayerList(), player.getName() + " loses the game.", "");
	}

	public void registerCard(Card card) {
		this.assignGameID(card);
		this.cardMap.put(card.getGameID(), card);
	}

	public void unregisterCard(Card card) {
		this.cardMap.remove(card.getGameID());
	}

	/**
	 * Adds the card with the given id on top of the given position. For testing puposes.
	 * 
	 * @param string
	 * @param blueDeck
	 */
	@SuppressWarnings("unused")
	private void addCardOnTopOfPosition(String id, Position pos) {
		Card c = Database.createCard(id);
		this.assignGameID(c);
		c.setCardScript(this.cardScriptFactory.createScript(c, this));
		pos.addToPosition(c);
		c.setCurrentPosition(pos);
		this.cardMap.put(c.getGameID(), c);
	}

	// ------------------------------------------------GameServer Methods:-------------------------------------------------------------------
	@Override
	public void sendTextMessageToAllPlayers(String message, String sound) {
		this.sendTextMessageToPlayers(this.getPlayerList(), message, sound);
	}

	@Override
	public void sendCardMessageToAllPlayers(String message, List<Card> cardList, String sound) {
		this.sendCardMessageToPlayers(this.getPlayerList(), message, cardList, sound);
	}

	@Override
	public void sendCardMessageToAllPlayers(String message, Card card, String sound) {
		playerBlue.playerReceivesCardMessage(message, card, sound);
		playerRed.playerReceivesCardMessage(message, card, sound);
	}

	@Override
	public void sendGameModelToAllPlayers(String sound) {
		this.sendGameModelToPlayers(getPlayerList(), sound);
	}

	private void sendTextMessageToPlayers(List<Player> playerList, String message, String sound) {
		for (Player p : playerList)
			p.playerReceivesGameTextMessage(message, sound);
	}

	private void sendCardMessageToPlayers(List<Player> playerList, String message, List<Card> cardList, String sound) {
		for (Player p : playerList)
			p.playerReceivesCardMessage(message, cardList, sound);
	}

	private void sendGameModelToPlayers(List<Player> playerList, String sound) {
		for (Player p : playerList) {
			GameModelUpdate updateModel = this.getGameModelForPlayer(p);
			p.playerUpdatesGameModel(updateModel, sound);
		}
	}

	@Override
	public void sendSoundToAllPlayers(String sound) {
		sendSoundToPlayers(getPlayerList(), sound);
	}

	private void sendSoundToPlayers(List<Player> playerList, String sound) {
		for (Player p : playerList)
			p.playerReceivesSound(sound);
	}

	// ------------------------------------------------/GameServer Methods-------------------------------------------------------------------

	// ------------------------------------------------Get/Set-Methods-----------------------------------------------------------------------

	/**
	 * Creates an instance of the given deck, by creating instances of the cards of the deck and assigning a gameID to them.
	 * 
	 * @param deck
	 * @return
	 */
	private List<Card> getDeckInstance(Deck deck) {
		List<Card> cardList = new ArrayList<Card>();
		for (int i = 0; i < deck.size(); i++) {
			Card c = Database.createCard(deck.get(i));
			if (c != null) {
				this.assignGameID(c);
				c.setCardScript(this.cardScriptFactory.createScript(c, this));
				cardList.add(c);
				this.cardMap.put(c.getGameID(), c);
			} else
				System.err.println("Couldn't create card - id " + deck.get(i) + " not found");
		}
		return cardList;
	}

	public GameModelUpdate getGameModelForPlayer(Player player) {
		GameModelUpdate gameModelUpdate = new GameModelUpdateImpl();
		gameModelUpdate.setGameModelParameters(gameModelParameters);

		Card dummyCard = new Card();
		for (Position pos : gameField.getAllPositions()) {
			Position position = new PositionImpl(pos.getPositionID(), pos.getColor());
			List<Card> cardList = new ArrayList<Card>();
			for (Card c : pos.getCards()) {
				// Check visibility:
				boolean isVisible = (player.getColor() == Color.BLUE && c.isVisibleForPlayerBlue()) || (player.getColor() == Color.RED && c.isVisibleForPlayerRed());
				if (isVisible)
					cardList.add(c);
				else
					cardList.add(dummyCard);
			}
			position.setCards(cardList);
			gameModelUpdate.add(position);
		}
		return gameModelUpdate;
	}

	@Override
	public List<Card> getAllCards() {
		List<Card> cardList = new ArrayList<>();
		for (Integer i : this.cardMap.keySet())
			cardList.add(this.cardMap.get(i));
		return cardList;
	}

	/**
	 * Searches for the card with the given gameID and returns it. Returns null if no such card was found.
	 * 
	 * @param cardGameID
	 * @return
	 */
	public Card getCard(int cardGameID) {
		return this.cardMap.get(cardGameID);
	}

	public Position getPosition(PositionID posID) {
		return this.gameField.getPosition(posID);
	}

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

	public ArrayList<PositionID> getFullArenaPositions(Color playerColor) {
		return this.gameField.getFullArenaPositions(playerColor, playerBlue, playerRed);
	}

	public ArrayList<PositionID> getFullBenchPositions(Color playerColor) {
		return this.gameField.getFullBenchPositions(playerColor, playerBlue, playerRed);
	}

	public ArrayList<PositionID> getPositionsForEvolving(PokemonCard c, Color color) {
		return this.gameField.getPositionsForEvolving(c, color, gameModelParameters.getTurnNumber());
	}

	/**
	 * Assigns a new gameID to a single card.
	 * 
	 * @param card
	 */
	private void assignGameID(Card card) {
		cardGameIDCounter++;
		card.setGameID(cardGameIDCounter);
	}

	public Player getPlayerRed() {
		return playerRed;
	}

	public void setPlayerRed(Player playerRed) {
		this.playerRed = playerRed;
	}

	public Player getPlayerBlue() {
		return playerBlue;
	}

	public void setPlayerBlue(Player playerBlue) {
		this.playerBlue = playerBlue;
	}

	public Player getPlayerOnTurn() {
		return playerOnTurn;
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
	public boolean getEnergyPlayed() {
		return this.gameModelParameters.isEnergyPlayed();
	}

	@Override
	public void setEnergyPlayed(boolean b) {
		this.gameModelParameters.setEnergyPlayed(b);
	}

	@Override
	public int getTurnNumber() {
		return this.gameModelParameters.getTurnNumber();
	}

	private List<Player> getPlayerList() {
		List<Player> playerList = new ArrayList<>();
		playerList.add(playerBlue);
		playerList.add(playerRed);
		return playerList;
	}

	@Override
	public GameState getGameState() {
		return this.gameModelParameters.getGameState();
	}

	/**
	 * Is executed between steps in the game flow.
	 * 
	 * @param milis
	 */
	private void timeoutWait(long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long getGameID() {
		return this.gameID;
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

	@Override
	public boolean getRetreatExecuted() {
		return this.gameModelParameters.isRetreatExecuted();
	}

	@Override
	public void setRetreatExecuted(boolean value) {
		this.gameModelParameters.setRetreatExecuted(value);
	}

	@Override
	public GameModelParameters getGameModelParameters() {
		return this.gameModelParameters;
	}

	@Override
	public void setGameModelParameters(GameModelParameters gameModelParameters) {
		this.gameModelParameters = gameModelParameters;
	}
}
