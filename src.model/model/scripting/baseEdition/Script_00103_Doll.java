package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

/**
 * Script for the doll token!
 * 
 * @author Michael
 *
 */
public class Script_00103_Doll extends PokemonCardScript {

	private TrainerCard clefaryDoll;
	private boolean firstPositionMove;

	/**
	 * Constructor only for local purposes!
	 * 
	 * @param card
	 * @param gameModel
	 */
	public Script_00103_Doll(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		this.clefaryDoll = null;
		this.addPokemonPower("Discard");
		this.firstPositionMove = false;
	}

	public Script_00103_Doll(PokemonCard card, PokemonGame gameModel, TrainerCard clefaryDoll) {
		super(card, gameModel);
		this.clefaryDoll = clefaryDoll;
		this.addPokemonPower("Discard");
		this.firstPositionMove = false;
	}

	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Can be called when the player has at least one other pokemon:
		if (gameModel.getFullArenaPositions(this.getCardOwner().getColor()).size() > 1)
			return true;
		return false;
	}

	public void executePokemonPower(String powerName) {
		final Player player = this.getCardOwner();
		gameModel.sendTextMessageToAllPlayers(player.getName() + " discards Clefary Doll!", "");

		Position pos = this.card.getCurrentPosition();
		boolean newActive = pos.getPositionID() == PositionID.BLUE_ACTIVEPOKEMON || pos.getPositionID() == PositionID.RED_ACTIVEPOKEMON ? true : false;
		PokemonCard doll = (PokemonCard) this.card;

		// Discard all cards attached to doll:
		List<Card> cardToPile = new ArrayList<>();
		for (Card c : pos.getCards())
			if (c != doll)
				cardToPile.add(c);
		for (Card c : cardToPile)
			gameModel.getAttackAction().discardCardToDiscardPile(pos.getPositionID(), c.getGameID(), true);

		// Remove doll from position:
		boolean success = pos.removeFromPosition(doll);
		if (!success)
			System.err.println("Couldn't remove doll from position");
		this.card.setCurrentPosition(null);

		// Unregister doll from gameModel:
		gameModel.unregisterCard(this.card);

		// Add trainer card to discard pile:
		Position discardPile = gameModel.getPosition(ownDiscardPile(player));
		discardPile.addToPosition(clefaryDoll);
		clefaryDoll.setCurrentPosition(discardPile);
		gameModel.sendGameModelToAllPlayers("");

		// Choose new active pokemon:
		if (newActive) {
			PositionID chosenPosition = player.playerChoosesPositions(gameModel.getFullArenaPositions(player.getColor()), 1, true, "Choose a new active pokemon!")
					.get(0);
			Card active = gameModel.getPosition(chosenPosition).getTopCard();
			gameModel.getAttackAction().movePokemonToPosition(chosenPosition, pos.getPositionID());

			// Send gameModel:
			gameModel.sendCardMessageToAllPlayers(player.getName() + " has chosen " + active.getName() + " as his new active pokemon!", active, "");
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	public void moveToPosition(PositionID targetPosition) {
		if (targetPosition == null || gameModel.getFullArenaPositions(Color.BLUE).contains(targetPosition)
				|| gameModel.getFullArenaPositions(Color.RED).contains(targetPosition)) {
			// Everything ok
		} else {
			if (!firstPositionMove)
				firstPositionMove = true;
			else {
				/*
				 * Swap with trainer card here:
				 */

				Position position = gameModel.getPosition(targetPosition);

				// Remove doll from position:
				boolean success = position.removeFromPosition(this.card);
				if (!success)
					System.err.println("Couldn't remove doll from position");
				this.card.setCurrentPosition(null);

				// Unregister doll from gameModel:
				gameModel.unregisterCard(this.card);

				// Add trainer card to position:
				position.addToPosition(clefaryDoll);
				clefaryDoll.setCurrentPosition(position);
			}
		}
	}

	@Override
	public boolean retreatCanBeExecuted() {
		return false; // Cannot retreat!
	}

	@Override
	public void executeAttack(String attackName) {
		// nothing todo here!
	}

	private PositionID ownDiscardPile(Player player) {
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DISCARDPILE;
		else
			return PositionID.RED_DISCARDPILE;
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		// Clefairy doll cannot be ASLEEP, CONFUSED, POISONED/TOXIC or PARALYZED!
		PokemonCard doll = (PokemonCard) this.card;
		if (condition == PokemonCondition.ASLEEP || condition == PokemonCondition.CONFUSED || condition == PokemonCondition.PARALYZED
				|| condition == PokemonCondition.POISONED || condition == PokemonCondition.TOXIC)
			doll.cureCondition(condition);
	}
}
