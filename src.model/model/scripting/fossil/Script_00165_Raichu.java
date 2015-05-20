package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00165_Raichu extends PokemonCardScript {

	public Script_00165_Raichu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Gigashock", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Gigashock"))
			this.gigashock();
	}

	private void gigashock() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		List<PositionID> damageBenchPositions = new ArrayList<>();
		if (this.gameModel.getFullBenchPositions(enemy.getColor()).size() > 3) {
			for (int i = 0; i < 3; i++) {
				List<PositionID> chooseList = this.gameModel.getFullBenchPositions(enemy.getColor());

				for (PositionID posID : damageBenchPositions)
					if (chooseList.contains(posID))
						chooseList.remove(posID);

				Preconditions.checkArgument(!chooseList.isEmpty(), "Error: ChooseList is empty!");

				PositionID chosenPosition = player.playerChoosesPositions(chooseList, 1, true, "Choose a position to damage!").get(0);
				damageBenchPositions.add(chosenPosition);
			}
		} else
			damageBenchPositions = this.gameModel.getFullBenchPositions(enemy.getColor());

		for (PositionID posID : damageBenchPositions)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, posID, 10, false);
	}
}
