package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00116_Vaporeon extends PokemonCardScript {

	public Script_00116_Vaporeon(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Quick Attack", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Watergun", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Quick Attack"))
			this.quickAttack();
		else
			this.watergun();
	}

	private void quickAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 20 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void watergun() {
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
}
