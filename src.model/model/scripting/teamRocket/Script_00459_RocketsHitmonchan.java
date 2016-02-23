package model.scripting.teamRocket;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00459_RocketsHitmonchan extends PokemonCardScript {

	public Script_00459_RocketsHitmonchan(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		this.addAttack("Crosscounter", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Magnum Punch", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Crosscounter"))
			this.Crosscounter();
		else
			this.MagnumPunch();
	}

	private void Crosscounter() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();

		gameModel.sendTextMessageToAllPlayers(card.getName() + " is ready to counter attacks!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.SUPER_RETALIATION);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void MagnumPunch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);
	}
}
