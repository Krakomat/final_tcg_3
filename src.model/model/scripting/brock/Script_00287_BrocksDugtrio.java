package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Pair;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00287_BrocksDugtrio extends PokemonCardScript {

	public Script_00287_BrocksDugtrio(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		this.addAttack("Lie Low", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Earthdrill", att2Cost);
	}

	@Override
	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Earthdrill")) {
			Pair<Integer, Integer> currentPair = null;
			for (Pair<Integer, Integer> pair : gameModel.getGameModelParameters().getLieLowUsed_00287_BrocksDugtrio()) {
				if (pair.getKey() == this.card.getGameID())
					currentPair = pair;
			}
			if (currentPair == null)
				return false;
		}
		return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Lie Low"))
			this.LieLow();
		else
			this.Earthdrill();
	}

	public void executeEndTurnActions() {
		Pair<Integer, Integer> currentPair = null;
		for (Pair<Integer, Integer> pair : gameModel.getGameModelParameters().getLieLowUsed_00287_BrocksDugtrio()) {
			if (pair.getKey() == this.card.getGameID())
				currentPair = pair;
		}

		if (currentPair != null) {
			gameModel.getGameModelParameters().getLieLowUsed_00287_BrocksDugtrio().remove(currentPair);
			if (currentPair.getValue() > 1)
				gameModel.getGameModelParameters().getLieLowUsed_00287_BrocksDugtrio().add(new Pair<Integer, Integer>(currentPair.getKey(), currentPair.getValue() - 1));
		}
	}

	private void LieLow() {
		gameModel.sendTextMessageToAllPlayers(this.card.getName() + " protects itself!", "");
		gameModel.getAttackAction().inflictConditionToPosition(this.card.getCurrentPosition().getPositionID(), PokemonCondition.HARDEN20);
		gameModel.getGameModelParameters().getLieLowUsed_00287_BrocksDugtrio().add(new Pair<Integer, Integer>(this.card.getGameID(), 2));
	}

	private void Earthdrill() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);
	}
}
