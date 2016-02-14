package model.scripting.koga;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00383_KogasBeedrill extends PokemonCardScript {

	public Script_00383_KogasBeedrill(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Nerve Poison", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Hyper Needle", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Nerve Poison"))
			this.NervePoison();
		else
			this.HyperNeedle();
	}

	private void NervePoison() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is paralyzed and poisoned:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed and poisoned!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed and poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Hyper Needle") && gameModel.getGameModelParameters().activeEffect("00383", this.card.getGameID()))
			return false;
		return super.attackCanBeExecuted(attackName);
	}

	public void moveToPosition(PositionID targetPosition) {
		if (!PositionID.isArenaPosition(targetPosition) && targetPosition != PositionID.BLUE_DISCARDPILE && targetPosition != PositionID.RED_DISCARDPILE
				&& gameModel.getGameModelParameters().activeEffect("00383", cardGameID()))
			gameModel.getGameModelParameters().deactivateEffect("00383", this.card.getGameID());
	}

	private void HyperNeedle() {
		gameModel.getGameModelParameters().activateEffect("00383", this.card.getGameID());
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Hyper Needle does nothing!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 70, true);
		else
			gameModel.sendTextMessageToAllPlayers("Hyper Needle does nothing!", "");
		gameModel.sendTextMessageToAllPlayers("Hyper Needle can't be used anymore!", "");
	}
}
