package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00169_Gastly extends PokemonCardScript {

	public Script_00169_Gastly(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Lick", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Energy Conversion", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Lick"))
			this.lick();
		else
			this.energyConversion();
	}

	private void lick() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void energyConversion() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		costs.add(Element.PSYCHIC);
		gameModel.getAttackAction().playerPaysEnergy(player, costs, card.getCurrentPosition().getPositionID());

		// Choose a trainer card:
		List<Card> energyCards = gameModel.getPosition(ownDiscardPile()).getEnergyCards();
		List<Card> chosenCards = player.playerChoosesCards(energyCards, 2, false, "Choose up to 2 energy cards to recover!");
		for (Card chosenCard : chosenCards) {
			// Message clients:
			gameModel.sendCardMessageToAllPlayers(player.getName() + " recovers " + chosenCard.getName() + " from his discard pile!", chosenCard, "");
			// Move trainer card:
			gameModel.getAttackAction().moveCard(ownDiscardPile(), ownHand(), chosenCard.getGameID(), true);
		}

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 10, true);
	}

	private PositionID ownDiscardPile() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DISCARDPILE;
		else
			return PositionID.RED_DISCARDPILE;
	}

	private PositionID ownHand() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_HAND;
		else
			return PositionID.RED_HAND;
	}
}
