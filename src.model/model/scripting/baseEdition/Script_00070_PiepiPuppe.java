package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.Database;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00070_PiepiPuppe extends TrainerCardScript {

	public Script_00070_PiepiPuppe(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the players bench is not full
		if (gameModel.getFullBenchPositions(getCardOwner().getColor()).size() < 5)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Generate token:
		PokemonCard doll = (PokemonCard) Database.createCard("00103");
		gameModel.registerCard(doll);
		doll.setPriceValueable(true); // no prices if defeated

		// Set card script:
		doll.setCardScript(new Script_Doll(doll, gameModel, (TrainerCard) card));

		// Remove trainer card from hand:
		Position handPos = gameModel.getPosition(ownHand());
		boolean success = handPos.removeFromPosition(card);
		if (!success)
			System.err.println("Couldn't remove trainer card from position");
		this.card.setCurrentPosition(null);

		// Insert doll into hand:
		doll.setCurrentPosition(handPos);
		handPos.addToPosition(doll);

		// Set doll onto bench:
		doll.getCardScript().playFromHand();
	}

	/**
	 * Script for the doll token!
	 * 
	 * @author Michael
	 *
	 */
	private class Script_Doll extends PokemonCardScript {

		private TrainerCard clefaryDoll;

		public Script_Doll(PokemonCard card, PokemonGame gameModel, TrainerCard clefaryDoll) {
			super(card, gameModel);
			this.clefaryDoll = clefaryDoll;
			this.addPokemonPower("Discard");
		}

		public boolean pokemonPowerCanBeExecuted(String powerName) {
			// Can be called when the player has at least one other pokemon:
			if (gameModel.getFullArenaPositions(this.getCardOwner().getColor()).size() > 1)
				return true;
			return false;
		}

		public void executePokemonPower(String powerName) {
			final Player player = this.getCardOwner();
			gameModel.sendTextMessageToAllPlayers(player.getName() + " discards clefary doll!");

			Position pos = this.card.getCurrentPosition();
			boolean newActive = pos.getPositionID() == PositionID.BLUE_ACTIVEPOKEMON || pos.getPositionID() == PositionID.RED_ACTIVEPOKEMON ? true : false;
			PokemonCard doll = (PokemonCard) this.card;

			// Discard all cards attached to doll:
			List<Card> cardToPile = new ArrayList<>();
			for (Card c : pos.getCards())
				if (c != doll)
					cardToPile.add(c);
			for (Card c : cardToPile)
				gameModel.getAttackAction().discardCardToDiscardPile(pos.getPositionID(), c.getGameID());

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
			gameModel.sendGameModelToAllPlayers();

			// Choose new active pokemon:
			if (newActive) {
				PositionID chosenPosition = player.playerChoosesPositions(gameModel.getFullArenaPositions(player.getColor()), 1, true,
						"Choose a new active pokemon!").get(0);
				Card active = gameModel.getPosition(chosenPosition).getTopCard();
				gameModel.getAttackAction().movePokemonToPosition(chosenPosition, pos.getPositionID());

				// Send gameModel:
				gameModel.sendCardMessageToAllPlayers(player.getName() + " has chosen " + active.getName() + " as his new active pokemon!", active);
				gameModel.sendGameModelToAllPlayers();
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
	}

	private PositionID ownHand() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_HAND;
		else
			return PositionID.BLUE_HAND;
	}
}
