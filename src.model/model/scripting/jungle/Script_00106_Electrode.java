package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00106_Electrode extends PokemonCardScript {

	public Script_00106_Electrode(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Tackle", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Chain Lightning", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Tackle"))
			this.tackle();
		else
			this.chainLightning();
	}

	private void tackle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}

	private void chainLightning() {
		// Damage:
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Effect:
		Element defenderElement = ((PokemonCard) this.gameModel.getPosition(defender).getTopCard()).getElement();
		if (defenderElement != Element.COLORLESS) {
			for (PositionID posID : gameModel.getFullBenchPositions(Color.BLUE)) {
				PokemonCard pkmnCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
				if (pkmnCard.getElement() == defenderElement)
					this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, posID, 10, true);
			}

			for (PositionID posID : gameModel.getFullBenchPositions(Color.RED)) {
				PokemonCard pkmnCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
				if (pkmnCard.getElement() == defenderElement)
					this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, posID, 10, true);
			}
		}
	}
}
