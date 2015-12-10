package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00294_MistysSeadra extends PokemonCardScript {

	public Script_00294_MistysSeadra(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Tail Snap", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		this.addAttack("Knockout Needle", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Tail Snap"))
			this.TailSnap();
		else
			this.KnockoutNeedle();
	}

	private void TailSnap() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void KnockoutNeedle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("Check for extra damage!", "");
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 90, true);
			} else
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}
}
