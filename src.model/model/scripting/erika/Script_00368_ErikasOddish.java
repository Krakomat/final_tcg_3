package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00368_ErikasOddish extends PokemonCardScript {

	public Script_00368_ErikasOddish(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Blot", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Sporadic Sponging", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Blot"))
			this.Blot();
		else
			this.SporadicSponging();
	}

	private void Blot() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		// Heal:
		if (((PokemonCard) this.card).getDamageMarks() > 0)
			gameModel.getAttackAction().healPosition(attacker, 10);
	}

	private void SporadicSponging() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Heal:
		if (((PokemonCard) this.card).getDamageMarks() > 0) {
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS)
				gameModel.getAttackAction().healPosition(attacker, 10);
		}
	}
}
