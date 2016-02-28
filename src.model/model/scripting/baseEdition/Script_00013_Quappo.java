package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00013_Quappo extends PokemonCardScript {

	public Script_00013_Quappo(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Water Gun", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Whirlpool", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Water Gun"))
			this.aquaknarre();
		else
			this.strudel();
	}

	private void aquaknarre() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		List<Element> energy = gameModel.getPosition(attacker).getEnergy();
		List<Element> waterEnergy = new ArrayList<>();
		List<Element> otherEnergy = new ArrayList<>();
		for (Element e : energy)
			if (e == Element.WATER)
				waterEnergy.add(e);
			else
				otherEnergy.add(e);

		List<Element> attackCosts = new ArrayList<>();
		attackCosts.add(Element.WATER);
		attackCosts.add(Element.WATER);
		attackCosts.add(Element.COLORLESS);
		for (Element e : attackCosts) {
			if (e == Element.WATER)
				waterEnergy.remove(Element.WATER);
			else if (!otherEnergy.isEmpty())
				otherEnergy.remove(0);
			else
				waterEnergy.remove(Element.WATER);
		}
		int waterCounter = waterEnergy.size();
		if (waterCounter > 2)
			waterCounter = 2;

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30 + (10 * waterCounter), true);
	}

	private void strudel() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Position defendingPosition = gameModel.getPosition(defender);
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);

		// Check for energy at enemy position:
		if (defendingPosition.getEnergyCards().size() > 0 && !((PokemonCard) defendingPosition.getTopCard()).hasCondition(PokemonCondition.BROCKS_PROTECTION)) {
			// Choose one of them and remove it:
			List<Card> cardList = new ArrayList<>();
			for (Card c : defendingPosition.getEnergyCards())
				cardList.add(c);

			Card chosenEnergyCard = player.playerChoosesCards(cardList, 1, true, "Choose one energy card to remove!").get(0);
			PositionID discardPile = enemyDiscardPile();

			gameModel.getAttackAction().moveCard(defender, discardPile, chosenEnergyCard.getGameID(), true);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
