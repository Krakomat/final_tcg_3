package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00268_BrocksGolem extends PokemonCardScript {

	public Script_00268_BrocksGolem(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Rock Slide", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Fissure", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Rock Slide"))
			this.RockSlide();
		else
			this.Fissure();
	}

	private void RockSlide() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		if (gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()).size() > 3) {
			List<PositionID> fullBenchPos = gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor());
			PositionID pos1 = getCardOwner().playerChoosesPositions(fullBenchPos, 1, true, "Choose 3 bench pokemon to receive damage!").get(0);
			fullBenchPos.remove(pos1);
			PositionID pos2 = getCardOwner().playerChoosesPositions(fullBenchPos, 1, true, "Choose 2 bench pokemon to receive damage!").get(0);
			fullBenchPos.remove(pos2);
			PositionID pos3 = getCardOwner().playerChoosesPositions(fullBenchPos, 1, true, "Choose a bench pokemon to receive damage!").get(0);
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, pos1, 10, false);
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, pos2, 10, false);
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, pos3, 10, false);

		} else {
			for (PositionID benchPos : gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()))
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);
		}
	}

	private void Fissure() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);
	}
}
