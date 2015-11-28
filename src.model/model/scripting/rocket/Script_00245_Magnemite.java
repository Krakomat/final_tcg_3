package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00245_Magnemite extends PokemonCardScript {

	public Script_00245_Magnemite(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Tackle", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Magnetism", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Tackle"))
			this.Tackle();
		else
			this.Magnetism();
	}

	private void Tackle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}

	private void Magnetism() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int extraDamage = 0;
		for (PositionID posID : gameModel.getFullBenchPositions(this.getCardOwner().getColor())) {
			Card pkmn = gameModel.getPosition(posID).getTopCard();
			if (pkmn.getName().equals("Magnemite") || pkmn.getName().equals("Magneton") || pkmn.getName().equals("Dark Magneton"))
				extraDamage = extraDamage + 10;
		}
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 + extraDamage, true);
	}
}
