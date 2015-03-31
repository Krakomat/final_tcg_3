package model.game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import network.client.Player;
import model.database.Card;
import model.database.DynamicPokemonCondition;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

/**
 * Implements all possible actions from the attack-editor.
 * 
 * @author Michael
 */
public class AttackAction {

	private boolean noEnergyPayment; // no payment for attacks if true
	private PokemonGame gameModel;

	/**
	 * Constructor for this class.
	 * 
	 * @param gameModel
	 *            this has to be a {@link PokemonGameModel}, which can be manipulated by this class.
	 */
	public AttackAction(PokemonGame gameModel) {
		this.gameModel = gameModel;
		this.noEnergyPayment = false;
	}

	/**
	 * Flips a coin and returns the result. No messages send to the client here!
	 * 
	 * @return
	 */
	public Coin flipACoin() {
		Random r = new SecureRandom();
		int value = r.nextInt(2);
		if (value == 0)
			return Coin.HEADS;
		else if (value == 1)
			return Coin.TAILS;
		return null;
	}

	/**
	 * Flips the given amount of coins and returns the number of heads. Does send messages to the clients!
	 * 
	 * @param amount
	 * @return
	 */
	public int flipCoinsCountHeads(int amount) {
		int headsCounter = 0;
		for (int i = 0; i < amount; i++) {
			Coin c = this.flipACoin();
			gameModel.sendTextMessageToAllPlayers("Coin shows " + c, "");
			if (c == Coin.HEADS)
				headsCounter++;
		}
		return headsCounter;
	}

	/**
	 * Damages the given position for the given amount. Messages are being send to clients here!
	 * 
	 * @param attackElement
	 *            element of the attacking pokemon
	 * @param attackerPositionID
	 *            position of the attacking pokemon. Allowed to be null, if no pokemon is responsible for the damage(e.g. for poisondamage).
	 * @param targetPosition
	 *            defending position. Has to be an arena position.
	 * @param damageAmount
	 *            amount of damage
	 * @param applyWeaknessResistance
	 *            false if weakness/resistance of the defending pokemon should NOT influence the resulting damage.
	 * @return
	 */
	public int inflictDamageToPosition(Element attackElement, PositionID attackerPositionID, PositionID targetPosition, int damageAmount,
			boolean applyWeaknessResistance) {
		PokemonCard defenderPokemon = (PokemonCard) gameModel.getPosition(targetPosition).getTopCard();
		PokemonCard attackerPokemon = null;
		if (attackerPositionID != null)
			attackerPokemon = (PokemonCard) gameModel.getPosition(attackerPositionID).getTopCard();

		// Apply weakness/resistance if set:
		if (applyWeaknessResistance && attackerPokemon != null) {
			// Calculate real damage:
			if (attackElement.equals(defenderPokemon.getCurrentWeakness()))
				damageAmount = damageAmount * 2; // Double damage when weakness
			if (attackElement.equals(defenderPokemon.getCurrentResistance()))
				damageAmount = damageAmount - 30; // Reduce damage
		}

		// Check boosts on attacker:
		if (attackerPokemon != null && attackerPokemon.hasCondition(PokemonCondition.DAMAGEINCREASE10))
			damageAmount = damageAmount + 10;

		// Check for conditions that reduce damage on defending pokemon:
		if (defenderPokemon.hasCondition(PokemonCondition.HARDEN30))
			damageAmount = damageAmount - 30;
		if (defenderPokemon.hasCondition(PokemonCondition.HARDEN20))
			damageAmount = damageAmount - 20;
		if (defenderPokemon.hasCondition(PokemonCondition.INVULNERABLE))
			damageAmount = 0;
		if (defenderPokemon.hasCondition(PokemonCondition.NO_DAMAGE))
			damageAmount = 0;

		// Normalize damage:
		if (damageAmount < 0)
			damageAmount = 0;

		// Damage Pokemon:
		defenderPokemon.setDamageMarks(defenderPokemon.getDamageMarks() + damageAmount);

		// Normalize hitpoints:
		if (defenderPokemon.getDamageMarks() > defenderPokemon.getHitpoints())
			defenderPokemon.setDamageMarks(defenderPokemon.getHitpoints());

		// Apply knockout condition if hitpoints = damagepoints:
		if (defenderPokemon.getHitpoints() == defenderPokemon.getDamageMarks()) {
			this.inflictConditionToPosition(targetPosition, PokemonCondition.KNOCKOUT);
			// Check if enemy had DESTINY:
			if (defenderPokemon.hasCondition(PokemonCondition.DESTINY) && attackerPositionID != null)
				this.inflictConditionToPosition(attackerPositionID, PokemonCondition.KNOCKOUT);
		}

		// Call pokemonIsDamaged() on defending pokemon script:
		PokemonCardScript script = (PokemonCardScript) defenderPokemon.getCardScript();
		script.pokemonIsDamaged(gameModel.getTurnNumber(), damageAmount, attackerPositionID);

		this.gameModel.sendTextMessageToAllPlayers(defenderPokemon.getName() + " takes " + damageAmount + " damage!", "");
		this.gameModel.sendGameModelToAllPlayers(Sounds.DAMAGE);
		return damageAmount;
	}

	/**
	 * Inflicts the given condition to the target position. No messages to clients here!
	 * 
	 * @param targetPosition
	 * @param condition
	 */
	public void inflictConditionToPosition(PositionID targetPosition, PokemonCondition condition) {
		PokemonCard defenderPokemon = (PokemonCard) gameModel.getPosition(targetPosition).getTopCard();
		if (!defenderPokemon.hasCondition(condition) && !defenderPokemon.hasCondition(PokemonCondition.INVULNERABLE)) {
			switch (condition) {
			case ASLEEP:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 9999));
				break;
			case BLIND:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
				break;
			case CONFUSED:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 9999));
				break;
			case DAMAGEINCREASE10:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 1));
				break;
			case DESTINY:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
				break;
			case HARDEN30:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
				break;
			case HARDEN20:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
				break;
			case INVULNERABLE:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
				break;
			case KNOCKOUT:
				defenderPokemon.getConditions().clear(); // Remove all other conditions
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 9999));
				break;
			case NO_DAMAGE:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
				break;
			case PARALYZED:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
				break;
			case POISONED:
				if (defenderPokemon.hasCondition(PokemonCondition.POISONED))
					this.cureCondition(targetPosition, PokemonCondition.POISONED);
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 9999));
				break;
			case TOXIC:
				defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 9999));
				break;
			}
		}

		// Call pokemonGotCondition() on defending pokemon script:
		PokemonCardScript script = (PokemonCardScript) defenderPokemon.getCardScript();
		script.pokemonGotCondition(gameModel.getTurnNumber(), condition);
	}

	/**
	 * Removes all instances of the given condition from the given position. No messages to clients are being send here!
	 * 
	 * @param posID
	 * @param condition
	 */
	private void cureCondition(PositionID posID, PokemonCondition condition) {
		PokemonCard targetPokemon = (PokemonCard) gameModel.getPosition(posID).getTopCard();
		List<DynamicPokemonCondition> conditions = targetPokemon.getConditions();
		for (int i = 0; i < conditions.size(); i++) {
			PokemonCondition cond = conditions.get(i).getCondition();
			if (cond.equals(condition)) {
				conditions.remove(i);
				i--;
			}
		}
	}

	/**
	 * Heals the pokemon at the given position for the given amount. Sends messages to the clients here!
	 * 
	 * @param targetPosition
	 * @param amount
	 */
	public void healPosition(PositionID targetPosition, int amount) {
		PokemonCard targetPokemon = (PokemonCard) gameModel.getPosition(targetPosition).getTopCard();
		int newDamageMarks = targetPokemon.getDamageMarks() - amount;
		if (newDamageMarks < 0)
			newDamageMarks = 0;
		targetPokemon.setDamageMarks(newDamageMarks);

		this.gameModel.sendTextMessageToAllPlayers(targetPokemon.getName() + " is healed for " + amount, "");
		this.gameModel.sendGameModelToAllPlayers("");
	}

	/**
	 * No messages to clients are send here!
	 * 
	 * @param targetPosition
	 * @return
	 */
	public void cureAllConditionsOnPosition(PositionID targetPosition) {
		PokemonCard targetPokemon = (PokemonCard) gameModel.getPosition(targetPosition).getTopCard();
		List<DynamicPokemonCondition> conditions = targetPokemon.getConditions();
		for (int i = 0; i < conditions.size(); i++) {
			PokemonCondition condition = conditions.get(i).getCondition();
			if (condition.equals(PokemonCondition.ASLEEP) || condition.equals(PokemonCondition.CONFUSED) || condition.equals(PokemonCondition.PARALYZED)
					|| condition.equals(PokemonCondition.POISONED) || condition.equals(PokemonCondition.TOXIC)) {
				conditions.remove(i);
				i--;
			}
		}
	}

	/**
	 * Swaps pokemons at the given positions. Do NOT use this in order to swap a pokemon from a non-arena position like the discardpile! No game model update send to
	 * players here!
	 * 
	 * @param pos1
	 * @param pos2
	 */
	public void swapPokemon(PositionID pos1, PositionID pos2) {
		Position p1 = gameModel.getPosition(pos1);
		Position p2 = gameModel.getPosition(pos2);
		List<Card> cardList1 = p1.getCards();
		p1.setCards(p2.getCards());
		p2.setCards(cardList1);

		// Set current position for all cards:
		for (Card c : p1.getCards())
			c.setCurrentPosition(p1);
		for (Card c : p2.getCards())
			c.setCurrentPosition(p2);

		// Cure conditions if pokemon was swapped to bench from active position:
		if (pos1 == PositionID.BLUE_ACTIVEPOKEMON || pos1 == PositionID.RED_ACTIVEPOKEMON)
			this.cureAllConditionsOnPosition(pos2);
		if (pos2 == PositionID.BLUE_ACTIVEPOKEMON || pos2 == PositionID.RED_ACTIVEPOKEMON)
			this.cureAllConditionsOnPosition(pos1);
	}

	public String moveCard(PositionID sourcePositionID, PositionID targetPositionID, int cardGameID, boolean onTop) {
		Position source = null;
		if (sourcePositionID != null)
			source = gameModel.getPosition(sourcePositionID);
		Position target = null;
		if (targetPositionID != null)
			target = gameModel.getPosition(targetPositionID);
		Card c = gameModel.getCard(cardGameID);

		if (source != null)
			source.removeFromPosition(c);

		if (onTop && target != null)
			target.addToPosition(c);
		else if (target != null)
			target.getCards().add(0, c);
		c.setCurrentPosition(target);

		if (target != null && target.isVisibleForPlayer(Color.BLUE))
			target.setVisible(true, Color.BLUE);
		else
			target.setVisible(false, Color.BLUE);
		if (target != null && target.isVisibleForPlayer(Color.RED))
			target.setVisible(true, Color.RED);
		else
			target.setVisible(false, Color.RED);

		// Make hand cards of both players visible for them:
		Position blueHand = gameModel.getPosition(PositionID.BLUE_HAND);
		Position redHand = gameModel.getPosition(PositionID.RED_HAND);
		blueHand.setVisible(true, Color.BLUE);
		redHand.setVisible(true, Color.RED);

		return "";
	}

	/**
	 * Removes all energy cards from the given position. No messages send to clients here!
	 * 
	 * @param targetPosition
	 * @return
	 */
	public void removeAllEnergyFromPosition(PositionID targetPosition) {
		PositionID discardPileID = null;
		Position position = gameModel.getPosition(targetPosition);
		if (position.getColor() == Color.BLUE)
			discardPileID = PositionID.BLUE_DISCARDPILE;
		else
			discardPileID = PositionID.RED_DISCARDPILE;
		ArrayList<Card> energyCards = gameModel.getPosition(targetPosition).getEnergyCards();
		for (int i = 0; i < energyCards.size(); i++)
			this.moveCard(energyCards.get(i).getCurrentPosition().getPositionID(), discardPileID, energyCards.get(i).getGameID(), true);
	}

	/**
	 * Moves the given card to its owners discard pile. No messages to the client are send here!
	 * 
	 * @param posID
	 * @param gameID
	 */
	public void discardCardToDiscardPile(PositionID posID, int gameID) {
		PositionID discardPileID = null;
		Position position = gameModel.getPosition(posID);
		if (position.getColor() == Color.BLUE)
			discardPileID = PositionID.BLUE_DISCARDPILE;
		else
			discardPileID = PositionID.RED_DISCARDPILE;

		this.moveCard(posID, discardPileID, gameID, true);
	}

	/**
	 * Only sound messages send to clients here!
	 * 
	 * @param amount
	 * @param player
	 * @return
	 */
	public boolean playerDrawsCards(int amount, Player player) {
		Position deck = null;
		Position hand = null;
		if (player.getColor() == Color.BLUE) {
			deck = gameModel.getPosition(PositionID.BLUE_DECK);
			hand = gameModel.getPosition(PositionID.BLUE_HAND);
		} else {
			deck = gameModel.getPosition(PositionID.RED_DECK);
			hand = gameModel.getPosition(PositionID.RED_HAND);
		}

		if (deck.isEmpty())
			return false;

		boolean drawingFinished = false;
		for (int i = 0; i < amount && !drawingFinished; i++) {
			Card c = deck.getTopCard();
			if (c != null) {
				if (hand.getColor() == Color.BLUE)
					c.setVisibleForPlayerBlue(true);
				else
					c.setVisibleForPlayerRed(true);
				this.moveCard(deck.getPositionID(), hand.getPositionID(), c.getGameID(), true);
				gameModel.sendSoundToAllPlayers(Sounds.DRAW);
			} else
				drawingFinished = true;
		}

		checkAndResolveFullHand(hand.getPositionID(), player);

		return true;
	}

	/**
	 * Checks if the given player has too much cards on the given position. If this is the case, the player chooses cards until the position has not too much cards
	 * anymore.
	 * 
	 * @param handPos
	 * @param player
	 */
	public void checkAndResolveFullHand(PositionID handPos, Player player) {
		Position hand = gameModel.getPosition(handPos);
		// Check if player holds too much cards in his hand and force him to destroy some of them then:
		if (hand.size() > 9) {
			this.gameModel.sendTextMessageToAllPlayers(player.getName() + " has too much cards in his hand!", "");
			int destroyCounter = hand.size() - 9;
			List<Card> dCardList = player.playerChoosesCards((ArrayList<Card>) hand.getCards(), destroyCounter, true, "Choose " + destroyCounter
					+ " cards to destroy!");
			List<Card> cardsToRemove = new ArrayList<>();
			for (Card c : dCardList) {
				for (Card card : hand.getCards()) {
					if (card.getGameID() == c.getGameID())
						cardsToRemove.add(card);
				}
			}
			for (Card card : cardsToRemove) {
				hand.removeFromPosition(card);
				card.setCurrentPosition(null);
				this.gameModel.sendCardMessageToAllPlayers(player.getName() + " removes " + card.getName() + " from the game!", card, "");
			}
		}
	}

	/**
	 * No messages to clients send here!
	 * 
	 * @param player
	 */
	public void playerDiscardsAllCards(Player player) {
		PositionID handPos = null;
		PositionID discardPilePos = null;
		if (player.getColor() == Color.BLUE) {
			handPos = PositionID.BLUE_HAND;
			discardPilePos = PositionID.BLUE_DISCARDPILE;
		} else {
			handPos = PositionID.RED_HAND;
			discardPilePos = PositionID.RED_DISCARDPILE;
		}
		List<Card> handCards = gameModel.getPosition(handPos).getCards();
		while (!handCards.isEmpty()) {
			this.moveCard(handPos, discardPilePos, handCards.get(0).getGameID(), true);
		}
	}

	public String playerPutsAllHandCardsOnDeck(Player player) {
		if (player.getColor() == Color.BLUE) {
			Position pos = gameModel.getPosition(PositionID.BLUE_HAND);
			int size = pos.size();
			for (int i = 0; i < size; i++) {
				pos.getCardAtIndex(0).setVisibleForPlayerBlue(false);
				moveCard(pos.getPositionID(), PositionID.BLUE_DECK, pos.getCardAtIndex(0).getGameID(), true);
			}
		} else {
			Position pos = gameModel.getPosition(PositionID.RED_HAND);
			int size = pos.size();
			for (int i = 0; i < size; i++) {
				pos.getCardAtIndex(0).setVisibleForPlayerRed(false);
				moveCard(pos.getPositionID(), PositionID.RED_DECK, pos.getCardAtIndex(0).getGameID(), true);
			}
		}
		return "";
	}

	/**
	 * No update messages to clients send here!
	 * 
	 * @param position
	 * @param amountOfCards
	 * @return
	 */
	public String rearrangeCardsFromPosition(PositionID position, int amountOfCards) {
		Player attacker = gameModel.getPlayerOnTurn();
		ArrayList<Card> cards = new ArrayList<Card>();
		Position pos = gameModel.getPosition(position);
		for (int i = 0; i < amountOfCards; i++) {
			Card c = pos.removeTopCard();
			cards.add(c);
		}
		for (int i = 0; i < amountOfCards - 1; i++) {
			Card chosenCard = attacker.playerChoosesCards(cards, 1, true, "Put a card on the deck.").get(0);
			pos.addToPosition(chosenCard);
			this.removeCardFromList(cards, chosenCard);
		}
		pos.addToPosition(cards.get(0));
		return "";
	}

	private void removeCardFromList(ArrayList<Card> list, Card chosenCard) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getGameID() == chosenCard.getGameID()) {
				list.remove(i);
				i--;
			}
		}
	}

	/**
	 * Removes all damage marks from the given position. Sends client messages!
	 * 
	 * @param position
	 * @return
	 */
	public void fullHealPosition(PositionID position) {
		PokemonCard pokemonCard = (PokemonCard) gameModel.getPosition(position).getTopCard();

		gameModel.sendTextMessageToAllPlayers(pokemonCard.getName() + " is healed fully", "");

		if (pokemonCard.getDamageMarks() > 0)
			pokemonCard.setDamageMarks(0);

		gameModel.sendGameModelToAllPlayers("");
	}

	/**
	 * Shuffles the given position. No messages send here!
	 * 
	 * @param targetPosition
	 */
	public void shufflePosition(PositionID targetPosition) {
		gameModel.getPosition(targetPosition).shuffle();
	}

	/**
	 * Moves the given pokemon card to the players bench. Sends client messages here!
	 * 
	 * @param player
	 * @param card
	 */
	public void putBasicPokemonOnBench(Player player, PokemonCard card) {
		gameModel.sendCardMessageToAllPlayers(player.getName() + " sets " + card.getName() + " on his bench!", card, Sounds.ON_BENCH);

		// Get the lowest bench position that is empty:
		PositionID benchPosition = null;
		for (int i = 5; i >= 1; i--) {
			Position pos = gameModel.getPosition(PositionID.getBenchPosition(player.getColor(), i));
			if (pos.isEmpty())
				benchPosition = pos.getPositionID();
		}

		PositionID sourcePosition = card.getCurrentPosition().getPositionID();

		// Move pokemon to bench:
		this.moveCard(sourcePosition, benchPosition, card.getGameID(), true);

		// Update gameModel:
		gameModel.sendGameModelToAllPlayers("");
	}

	/**
	 * Messages send here!
	 * 
	 * @param chosenPosition
	 * @param cardGameID
	 */
	public void evolvePokemon(PositionID chosenPosition, int cardGameID) {
		PokemonCard c = (PokemonCard) gameModel.getCard(cardGameID);
		Position pos = c.getCurrentPosition();
		PokemonCard oldCard = (PokemonCard) gameModel.getPosition(chosenPosition).getTopCard();

		List<Card> cardList = new ArrayList<>();
		cardList.add(oldCard);
		cardList.add(c);
		gameModel.sendCardMessageToAllPlayers(oldCard.getName() + " evolves into " + c.getName(), cardList, Sounds.EVOLVE);

		int damage = oldCard.getDamageMarks();
		oldCard.resetDynamicAttributes(); // Clean damage and conditions on old card
		this.moveCard(pos.getPositionID(), chosenPosition, cardGameID, true);

		// Add the remaining damage marks to the evolved pokemon:
		c.setDamageMarks(damage);

		// Update gameModel:
		gameModel.sendGameModelToAllPlayers("");
	}

	public String movePokemonToPosition(PositionID from, PositionID to) {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(from).getCards())
			cardList.add(c);

		int cardListSize = cardList.size();
		for (int i = 0; i < cardListSize; i++)
			this.moveCard(from, to, cardList.get(i).getGameID(), true);
		return "";
	}

	/**
	 * The given player pays the given energy costs from the given position.
	 * 
	 * @param player
	 * @param costs
	 * @param posID
	 * @return
	 */
	public void playerPaysEnergy(Player player, List<Element> costs, PositionID posID) {
		if (!this.noEnergyPayment) {
			// Let player choose the cards for payment:
			List<Card> energyCards = this.gameModel.getPosition(posID).getEnergyCards();
			List<Card> chosenEnergyCards = player.playerPaysEnergyCosts(costs, energyCards);

			// Move cards from position to discard pile:
			PositionID targetPosition = player.getColor() == Color.BLUE ? PositionID.BLUE_DISCARDPILE : PositionID.RED_DISCARDPILE;
			for (Card c : chosenEnergyCards)
				this.moveCard(posID, targetPosition, c.getGameID(), true);
		}
	}

	public void setNoEnergyPayment(boolean flag) {
		this.noEnergyPayment = flag;
	}
}
