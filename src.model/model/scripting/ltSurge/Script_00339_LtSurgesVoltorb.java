package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00339_LtSurgesVoltorb extends PokemonCardScript {

	public Script_00339_LtSurgesVoltorb(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Spin Ball", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Double Spin", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Spin Ball"))
			this.SpinBall();
		else
			this.DoubleSpin();
	}

	private void SpinBall() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 1 coin...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(1);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 20, true);
	}

	private void DoubleSpin() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 2 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(2);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 20, true);
	}
}
