package model.game;

import gui2d.animations.Animation;
import gui2d.animations.AnimationType;
import gui2d.animations.CardDrawAnimation;
import gui2d.animations.CardMoveAnimation;
import gui2d.animations.CoinflipAnimation;
import gui2d.animations.DamageAnimation;

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

	private PokemonGame gameModel;

	/**
	 * Constructor for this class.
	 * 
	 * @param gameModel
	 *            this has to be a {@link PokemonGameModel}, which can be manipulated by this class.
	 */
	public AttackAction(PokemonGame gameModel) {
		this.gameModel = gameModel;
	}

	/**
	 * Flips a coin and returns the result. Only sound messages send here!
	 * 
	 * @return
	 */
	public Coin flipACoin() {
		// gameModel.sendSoundToAllPlayers(Sounds.COINFLIP);

		Random r = new SecureRandom();
		int value = r.nextInt(2);
		if (value == 0) {
			// Execute animation:
			Animation animation = new CoinflipAnimation(true);
			gameModel.sendAnimationToAllPlayers(animation);
			return Coin.HEADS;
		} else if (value == 1) {
			Animation animation = new CoinflipAnimation(false);
			gameModel.sendAnimationToAllPlayers(animation);
			return Coin.TAILS;
		}
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
	 * @return the damage that was actually applied to the defending pokemon
	 */
	public int inflictDamageToPosition(Element attackElement, PositionID attackerPositionID, PositionID targetPosition, int damageAmount, boolean applyWeaknessResistance) {
		PokemonCard defenderPokemon = (PokemonCard) gameModel.getPosition(targetPosition).getTopCard();
		PokemonCard attackerPokemon = null;
		if (attackerPositionID != null)
			attackerPokemon = (PokemonCard) gameModel.getPosition(attackerPositionID).getTopCard();

		// Apply weakness/resistance if set:
		if (applyWeaknessResistance && attackerPokemon != null) {
			// Calculate real damage:
			if (attackElement.equals(defenderPokemon.getCurrentWeakness()))
				damageAmount = damageAmount * 2; // Double damage when weakness
			// Don't apply resistance, if Pewter City Gym is in play and the
			// attacker has Brock in its name:
			if (attackElement.equals(defenderPokemon.getCurrentResistance())
					&& !(gameModel.getCurrentStadium() != null && gameModel.getCurrentStadium().getCardId().equals("00286") && attackerPokemon.getName().contains("Brock")))
				damageAmount = damageAmount - 30; // Reduce damage
		}

		// Check boosts on attacker:
		if (attackerPokemon != null && attackerPokemon.hasCondition(PokemonCondition.DAMAGEINCREASE10))
			damageAmount = damageAmount + 10;
		// Check Misty:
		if (attackerPokemon != null && attackerPokemon.getName().contains("Misty") && gameModel.getGameModelParameters().isActivated_00296_Misty())
			damageAmount = damageAmount + 20;
		// Check Vermillion City Gym:
		if (gameModel.getGameModelParameters().isVermillionCityGymAttackModifier() && damageAmount > 0)
			damageAmount = damageAmount + 10;

		// Check for conditions that reduce damage on defending pokemon:
		if (defenderPokemon.hasCondition(PokemonCondition.HARDEN30))
			damageAmount = damageAmount - 30;
		if (defenderPokemon.hasCondition(PokemonCondition.HARDEN20))
			damageAmount = damageAmount - 20;
		if (defenderPokemon.hasCondition(PokemonCondition.HALF_DAMAGE)) {
			damageAmount = damageAmount / 2;
			// Round down:
			if (damageAmount % 10 > 0)
				damageAmount = damageAmount - 5;
		}
		if (defenderPokemon.hasCondition(PokemonCondition.INVULNERABLE))
			damageAmount = 0;
		if (defenderPokemon.hasCondition(PokemonCondition.NO_DAMAGE))
			damageAmount = 0;

		// Normalize damage:
		if (damageAmount < 0)
			damageAmount = 0;

		// Test if defender pokemon is able to modify the incoming damage:
		for (Card c : gameModel.getAllCards()) {
			if (c instanceof PokemonCard) {
				PokemonCardScript script = (PokemonCardScript) c.getCardScript();
				damageAmount = script.modifyIncomingDamage(damageAmount, attackerPokemon, targetPosition);
			}
		}

		// Test if attacker pokemon is able to modify the outgoing damage:
		if (attackerPokemon != null) {
			PokemonCardScript attackerScript = (PokemonCardScript) attackerPokemon.getCardScript();
			damageAmount = attackerScript.modifyOutgoingDamage(damageAmount);
		}

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
		if (damageAmount > 0) {
			// Execute animation:
			Animation animation = new DamageAnimation(AnimationType.DAMAGE_POSITION, targetPosition, damageAmount);
			gameModel.sendAnimationToAllPlayers(animation);
		}
		this.gameModel.sendGameModelToAllPlayers("");

		if (defenderPokemon.hasCondition(PokemonCondition.RETALIATION) && attackerPositionID != null)
			this.inflictDamageToPosition(defenderPokemon.getElement(), defenderPokemon.getCurrentPosition().getPositionID(), attackerPositionID, damageAmount, true);
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
		PokemonCardScript script = (PokemonCardScript) defenderPokemon.getCardScript();
		if (script.allowIncomingCondition(condition)) {
			if (!defenderPokemon.hasCondition(condition) && !defenderPokemon.hasCondition(PokemonCondition.INVULNERABLE)) {
				switch (condition) {
				case ASLEEP:
					defenderPokemon.cureCondition(PokemonCondition.ASLEEP);
					defenderPokemon.cureCondition(PokemonCondition.CONFUSED);
					defenderPokemon.cureCondition(PokemonCondition.PARALYZED);
					defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 9999));
					break;
				case BLIND:
					defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
					break;
				case CONFUSED:
					defenderPokemon.cureCondition(PokemonCondition.ASLEEP);
					defenderPokemon.cureCondition(PokemonCondition.CONFUSED);
					defenderPokemon.cureCondition(PokemonCondition.PARALYZED);
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
					defenderPokemon.getConditions().clear(); // Remove all other
																// conditions
					defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 9999));
					break;
				case NO_DAMAGE:
					defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
					break;
				case PARALYZED:
					defenderPokemon.cureCondition(PokemonCondition.ASLEEP);
					defenderPokemon.cureCondition(PokemonCondition.CONFUSED);
					defenderPokemon.cureCondition(PokemonCondition.PARALYZED);
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
				case RETALIATION:
					defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
					break;
				case POKEMON_POWER_BLOCK:
					defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
					break;
				case BROCKS_PROTECTION:
					defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 9999));
					break;
				case NO_ENERGY:
					defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
					break;
				case HALF_DAMAGE:
					defenderPokemon.getConditions().add(new DynamicPokemonCondition(condition, 2));
					break;
				default:
					break;
				}
			}

			// Call pokemonGotCondition() on defending pokemon script:
			script.pokemonGotCondition(gameModel.getTurnNumber(), condition);
		}
	}

	/**
	 * Removes all instances of the given condition from the given position. No messages to clients are being send here!
	 * 
	 * @param posID
	 * @param condition
	 */
	public void cureCondition(PositionID posID, PokemonCondition condition) {
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
		// Execute animation:
		Animation animation = new DamageAnimation(AnimationType.HEAL_POSITION, targetPosition, amount);
		gameModel.sendAnimationToAllPlayers(animation);
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
	 * Swaps pokemons at the given positions. Do NOT use this in order to swap a pokemon from a non-arena position like the discardpile! No game model update send to players here!
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

		if (c instanceof PokemonCard && !PositionID.isArenaPosition(targetPositionID))
			((PokemonCard) c).resetDynamicAttributes();

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
	public void discardCardToDiscardPile(PositionID posID, int gameID, boolean executeAnimation) {
		PositionID discardPileID = null;
		Position position = gameModel.getPosition(posID);
		if (position.getColor() == Color.BLUE)
			discardPileID = PositionID.BLUE_DISCARDPILE;
		else if (position.getColor() == Color.RED)
			discardPileID = PositionID.RED_DISCARDPILE;

		this.moveCard(posID, discardPileID, gameID, true);

		Card card = gameModel.getCard(gameID);

		// Execute animation:
		if (executeAnimation) {
			Animation animation = new CardMoveAnimation(posID, discardPileID, card.getCardId(), "");
			gameModel.sendAnimationToAllPlayers(animation);
		}
	}

	/**
	 * Sends gamemodel + sound to clients here!
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

				// Execute animation:
				Animation animation = new CardDrawAnimation(player.getColor());
				gameModel.sendAnimationToAllPlayers(animation);
				gameModel.sendGameModelToAllPlayers("");
			} else
				drawingFinished = true;
		}

		return true;
	}

	/**
	 * Both players draw simultaneously! Use only for initial draw! Sends gamemodel + sound to clients here!
	 * 
	 * @param amount
	 * @param player
	 * @return
	 */
	public void playersDrawCards(int amount1, Player player1, int amount2, Player player2) {
		{
			Position deck = null;
			Position hand = null;
			if (player1.getColor() == Color.BLUE) {
				deck = gameModel.getPosition(PositionID.BLUE_DECK);
				hand = gameModel.getPosition(PositionID.BLUE_HAND);
			} else {
				deck = gameModel.getPosition(PositionID.RED_DECK);
				hand = gameModel.getPosition(PositionID.RED_HAND);
			}

			boolean drawingFinished = false;
			for (int i = 0; i < amount1 && !drawingFinished; i++) {
				Card c = deck.getTopCard();
				if (c != null) {
					if (hand.getColor() == Color.BLUE)
						c.setVisibleForPlayerBlue(true);
					else
						c.setVisibleForPlayerRed(true);
					this.moveCard(deck.getPositionID(), hand.getPositionID(), c.getGameID(), true);
				} else
					drawingFinished = true;
			}
		}

		{
			Position deck = null;
			Position hand = null;
			if (player2.getColor() == Color.BLUE) {
				deck = gameModel.getPosition(PositionID.BLUE_DECK);
				hand = gameModel.getPosition(PositionID.BLUE_HAND);
			} else {
				deck = gameModel.getPosition(PositionID.RED_DECK);
				hand = gameModel.getPosition(PositionID.RED_HAND);
			}

			boolean drawingFinished = false;
			for (int i = 0; i < amount2 && !drawingFinished; i++) {
				Card c = deck.getTopCard();
				if (c != null) {
					if (hand.getColor() == Color.BLUE)
						c.setVisibleForPlayerBlue(true);
					else
						c.setVisibleForPlayerRed(true);
					this.moveCard(deck.getPositionID(), hand.getPositionID(), c.getGameID(), true);
				} else
					drawingFinished = true;
			}
		}

		// Execute animation:
		Animation animation = new CardDrawAnimation(null);
		gameModel.sendAnimationToAllPlayers(animation);
		gameModel.sendGameModelToAllPlayers("");
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
		while (!handCards.isEmpty() && !handContainsOnlyDummyCards(handCards)) {
			Card realCard = null;
			for (Card c : handCards)
				if (c.getGameID() != -1) {
					realCard = c;
					break;
				}
			this.moveCard(handPos, discardPilePos, realCard.getGameID(), true);
		}
	}

	private boolean handContainsOnlyDummyCards(List<Card> handCards) {
		for (Card c : handCards)
			if (c.getGameID() != -1)
				return false;
		return true;
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
			Card realCard = gameModel.getCard(chosenCard.getGameID());
			pos.addToPosition(realCard);
			this.removeCardFromList(cards, chosenCard);
		}
		pos.addToPosition(cards.get(0));
		return "";
	}

	private void removeCardFromList(ArrayList<Card> list, Card chosenCard) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getGameID() == chosenCard.getGameID()) {
				list.remove(i);
				return;
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

		// Execute animation:
		Animation animation = new DamageAnimation(AnimationType.HEAL_POSITION, position, pokemonCard.getHitpoints());
		gameModel.sendAnimationToAllPlayers(animation);
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
		gameModel.sendCardMessageToAllPlayers(player.getName() + " sets " + card.getName() + " on his bench!", card, "");

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

		// Execute animation:
		Animation animation = new CardMoveAnimation(sourcePosition, benchPosition, card.getCardId(), Sounds.ON_BENCH);
		gameModel.sendAnimationToAllPlayers(animation);

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
		PositionID startPosID = pos.getPositionID();
		PokemonCard oldCard = (PokemonCard) gameModel.getPosition(chosenPosition).getTopCard();

		List<Card> cardList = new ArrayList<>();
		cardList.add(oldCard);
		cardList.add(c);
		gameModel.sendCardMessageToAllPlayers(oldCard.getName() + " evolves into " + c.getName(), cardList, "");

		int damage = oldCard.getDamageMarks();
		oldCard.resetDynamicAttributes(); // Clean damage and conditions on old
											// card
		this.moveCard(pos.getPositionID(), chosenPosition, cardGameID, true);

		// Add the remaining damage marks to the evolved pokemon:
		c.setDamageMarks(damage);

		// Execute animation:
		Animation animation = new CardMoveAnimation(startPosID, chosenPosition, c.getCardId(), Sounds.EVOLVE);
		gameModel.sendAnimationToAllPlayers(animation);

		// Update gameModel:
		gameModel.sendGameModelToAllPlayers("");
	}

	public String movePokemonToPosition(PositionID from, PositionID to) {
		List<Card> cardList = new ArrayList<>();
		String topCardID = gameModel.getPosition(from).getTopCard().getCardId();
		for (Card c : gameModel.getPosition(from).getCards())
			cardList.add(c);

		int cardListSize = cardList.size();
		for (int i = 0; i < cardListSize; i++)
			this.moveCard(from, to, cardList.get(i).getGameID(), true);

		// Execute animation:
		Animation animation = new CardMoveAnimation(from, to, topCardID, "");
		gameModel.sendAnimationToAllPlayers(animation);

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
		if (!this.gameModel.getGameModelParameters().isNoEnergyPayment()) {
			// Let player choose the cards for payment:
			List<Card> energyCards = this.gameModel.getPosition(posID).getEnergyCards();
			List<Card> chosenEnergyCards = player.playerPaysEnergyCosts(costs, energyCards);

			// Move cards from position to discard pile:
			PositionID targetPosition = player.getColor() == Color.BLUE ? PositionID.BLUE_DISCARDPILE : PositionID.RED_DISCARDPILE;
			for (Card c : chosenEnergyCards) {
				this.moveCard(posID, targetPosition, c.getGameID(), true);

				// Execute animation:
				Animation animation = new CardMoveAnimation(posID, targetPosition, c.getCardId(), "");
				gameModel.sendAnimationToAllPlayers(animation);
			}
		}
	}
}
