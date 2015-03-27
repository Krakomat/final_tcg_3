package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
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
		this.addAttack("Aquaknarre", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Strudel", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Aquaknarre"))
			this.aquaknarre();
		else
			this.strudel();
	}

	private void aquaknarre() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		List<Element> energyList = new ArrayList<>();

		for (Element ele : gameModel.getPosition(attacker).getEnergy())
			energyList.add(ele);

		energyList.remove(Element.WATER);
		energyList.remove(Element.WATER);

		int remainingWater = 0;
		if (energyList.size() > 1 && energyList.contains(Element.WATER)) {
			// Count remaining water energy:
			for (Element ele : energyList)
				if (ele == Element.WATER)
					remainingWater++;
		}
		remainingWater = remainingWater % 3;
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30 + (10 * remainingWater), true);
	}

	private void strudel() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Position defendingPosition = gameModel.getPosition(defender);
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);

		// Check for energy at enemy position:
		if (defendingPosition.getEnergyCards().size() > 0) {
			// Choose one of them and remove it:
			List<Card> cardList = new ArrayList<>();
			for (Card c : defendingPosition.getEnergyCards())
				cardList.add(c);

			Card chosenEnergyCard = player.playerChoosesCards(cardList, 1, true, "Choose one energy card to remove!").get(0);
			PositionID discardPile = null;
			if (chosenEnergyCard.getCurrentPosition().getColor() == Color.BLUE)
				discardPile = PositionID.BLUE_DISCARDPILE;
			else
				discardPile = PositionID.RED_DISCARDPILE;

			gameModel.getAttackAction().moveCard(chosenEnergyCard.getCurrentPosition().getPositionID(), discardPile, chosenEnergyCard.getGameID(), true);
			gameModel.sendGameModelToAllPlayers();
		}
	}
}
