package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00231_DarkWartortle extends PokemonCardScript {

	public Script_00231_DarkWartortle(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Doubleslap", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Mirror Shell", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Doubleslap"))
			this.Doubleslap();
		else
			this.MirrorShell();
	}

	private void Doubleslap() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 2 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(2);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 10, true);
	}

	private void MirrorShell() {
		gameModel.getAttackAction().inflictConditionToPosition(this.card.getCurrentPosition().getPositionID(), PokemonCondition.RETALIATION);
		gameModel.sendGameModelToAllPlayers("");
	}
}
