package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00413_SabrinasVenonat extends PokemonCardScript {

	public Script_00413_SabrinasVenonat(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Poison Antennae", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Removal Beam", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Poison Antennae"))
			this.PoisonAntennae();
		else
			this.RemovalBeam();
	}

	private void PoisonAntennae() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is poisoned!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void RemovalBeam() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		Position defendingPosition = gameModel.getPosition(defender);
		if (defendingPosition.getEnergyCards().size() > 0 && !((PokemonCard) defendingPosition.getTopCard()).hasCondition(PokemonCondition.BROCKS_PROTECTION)) {
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				// Check for energy at enemy position:
				// Choose one of them and remove it:
				List<Card> cardList = new ArrayList<>();
				for (Card c : defendingPosition.getEnergyCards())
					cardList.add(c);

				Card chosenEnergyCard = getCardOwner().playerChoosesCards(cardList, 1, true, "Choose one energy card to remove!").get(0);
				PositionID discardPile = enemyDiscardPile();

				gameModel.getAttackAction().moveCard(defender, discardPile, chosenEnergyCard.getGameID(), true);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}
}
