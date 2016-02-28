package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00059_Quapsel extends PokemonCardScript {

	public Script_00059_Quapsel(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Water Gun", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
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

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 + (10 * waterCounter), true);
	}
}
