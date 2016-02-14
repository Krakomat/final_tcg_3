package model.scripting.koga;

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

public class Script_00387_KogasMuk extends PokemonCardScript {

	public Script_00387_KogasMuk(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Sludge Whirlpool", att1Cost);

		this.addPokemonPower("Energy Drain");
	}

	public void pokemonIsDamaged(int turnNumber, int damage, PositionID source) {
		if (EnergyDrainCanBeExecuted(source)) {
			if (source != null)
				this.EnergyDrain(source);
		}
	}

	private boolean EnergyDrainCanBeExecuted(PositionID source) {
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() == this.getCardOwner().getColor())
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		if (gameModel.getPosition(source).getColor() == gameModel.getPosition(this.getCurrentPositionID()).getColor())
			return false;// Only opponent attacks!
		return true;
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	public void EnergyDrain(PositionID defender) {
		gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Energy Drain!", card, "");
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			Position defendingPosition = gameModel.getPosition(defender);

			// Check for energy at enemy position:
			if (defendingPosition.getEnergyCards().size() > 0 && !((PokemonCard) defendingPosition.getTopCard()).hasCondition(PokemonCondition.BROCKS_PROTECTION)) {
				// Choose one of them and remove it:
				List<Card> cardList = new ArrayList<>();
				for (Card c : defendingPosition.getEnergyCards())
					cardList.add(c);

				Card chosenEnergyCard = this.getCardOwner().playerChoosesCards(cardList, 1, true, "Choose one energy card to remove!").get(0);
				PositionID discardPile = enemyDiscardPile();

				gameModel.getAttackAction().moveCard(defender, discardPile, chosenEnergyCard.getGameID(), true);
				gameModel.sendGameModelToAllPlayers("");
			}
		} else
			gameModel.sendCardMessageToAllPlayers("Energy Drain failed!", card, "");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
	}
}
