package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.Database;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.EnergyCardScript;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00021_Lektrobal extends PokemonCardScript {

	public Script_00021_Lektrobal(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Elektoschock", att1Cost);

		this.addPokemonPower("Bruzzeln");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);

		// Flip coin to check if defending pokemon damages itself:
		gameModel.sendTextMessageToAllPlayers("If tails then " + attackingPokemon.getName() + " damages itself!");
		Coin c = gameModel.getAttackAction().flipACoin();
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c);
		if (c == Coin.TAILS) {
			gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " damages itself!");
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 10, true);
			gameModel.sendGameModelToAllPlayers();
		}
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Can be executed when Lektrobal is in play and isn't asleep, confused or paralyzed
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		return true;
	}

	@Override
	public void executePokemonPower(String powerName) {
		final Player player = this.getCardOwner();
		PokemonCard pCard = (PokemonCard) this.card;
		List<PositionID> posList = gameModel.getFullArenaPositions(player.getColor());
		posList.remove(pCard.getCurrentPosition().getPositionID());

		// Choose a position to attach Lektrobal to:
		PositionID chosenPosition = player.playerChoosesPositions(posList, 1, true, "Choose a position to attach Lektrobal to!").get(0);
		Position targetPosition = gameModel.getPosition(chosenPosition);
		Card targetPokemon = targetPosition.getTopCard();

		// Let the player choose an element:
		Element chosenElement = player.playerChoosesElements(Element.getAllElements(), 1, true, "Choose an energy type!").get(0);
		// Generate list of energy:
		List<Element> providedEnergy = new ArrayList<>();
		providedEnergy.add(chosenElement);
		providedEnergy.add(chosenElement);

		// Move all other cards on Lektrobals position to discardpile:
		Position pos = pCard.getCurrentPosition();
		List<Card> cardToPile = new ArrayList<>();
		for (Card c : pos.getCards())
			if (c != pCard)
				cardToPile.add(c);
		for (Card c : cardToPile)
			gameModel.getAttackAction().discardCardToDiscardPile(pos.getPositionID(), c.getGameID());

		// Check if a new active pokemon has to be chosen:
		boolean newActive = pos.getPositionID() == PositionID.BLUE_ACTIVEPOKEMON || pos.getPositionID() == PositionID.RED_ACTIVEPOKEMON ? true : false;

		// Remove lektrobal from its position:
		pos.removeFromPosition(pCard);
		pCard.setCurrentPosition(null);

		// Generate token:
		EnergyCard token = (EnergyCard) Database.createCard("00104");
		gameModel.registerCard(token);
		token.setProvidedEnergy(providedEnergy);

		// Set card script:
		token.setCardScript(new Script_Token(token, gameModel, (PokemonCard) card));

		// Attach the energy:
		token.setCurrentPosition(targetPosition);
		targetPosition.getCards().add(0, token);

		// Send messages:
		List<Card> cardMessageList = new ArrayList<>();
		cardMessageList.add(pCard);
		cardMessageList.add(targetPokemon);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " attaches Lektrobal to " + targetPokemon.getName() + "!", cardMessageList);
		gameModel.sendGameModelToAllPlayers();

		// Choose new active pokemon:
		if (newActive) {
			PositionID newActivePosition = player
					.playerChoosesPositions(gameModel.getFullArenaPositions(player.getColor()), 1, true, "Choose a new active pokemon!").get(0);
			Card active = gameModel.getPosition(chosenPosition).getTopCard();
			gameModel.getAttackAction().movePokemonToPosition(newActivePosition, pos.getPositionID());

			// Send gameModel:
			gameModel.sendCardMessageToAllPlayers(player.getName() + " has chosen " + active.getName() + " as his new active pokemon!", active);
			gameModel.sendGameModelToAllPlayers();
		}
	}

	/**
	 * Script for the doll token!
	 * 
	 * @author Michael
	 *
	 */
	private class Script_Token extends EnergyCardScript {

		private PokemonCard lektrobal;

		public Script_Token(EnergyCard token, PokemonGame gameModel, PokemonCard lektrobal) {
			super(token, gameModel);
			this.lektrobal = lektrobal;
		}

		public void moveToPosition(PositionID targetPosition) {
			final Player player = this.getCardOwner();
			if (targetPosition == ownDiscardPile(player)) {
				Position pos = gameModel.getPosition(targetPosition);

				// Remove token from position:
				boolean success = pos.removeFromPosition(this.card);
				if (!success)
					System.err.println("Couldn't remove doll from position");
				this.card.setCurrentPosition(null);

				// Unregister token from gameModel:
				gameModel.unregisterCard(this.card);

				// Add Lektrobal to discard pile:
				pos.addToPosition(this.lektrobal);
				this.lektrobal.setCurrentPosition(pos);
				gameModel.sendGameModelToAllPlayers();
			}
		}

		private PositionID ownDiscardPile(Player player) {
			if (player.getColor() == Color.BLUE)
				return PositionID.BLUE_DISCARDPILE;
			else
				return PositionID.RED_DISCARDPILE;
		}
	}
}
