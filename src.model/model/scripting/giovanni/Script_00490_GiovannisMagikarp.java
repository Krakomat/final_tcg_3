package model.scripting.giovanni;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00490_GiovannisMagikarp extends PokemonCardScript {

	public Script_00490_GiovannisMagikarp(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Ancestral Memory", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Flail Around", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Ancestral Memory"))
			this.AncestralMemory();
		else
			this.FlailAround();
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Ancestral Memory") && gameModel.getGameModelParameters().activeEffect("00490", this.card.getGameID()))
			return false;
		return super.attackCanBeExecuted(attackName);
	}

	public void moveToPosition(PositionID targetPosition) {
		if (!PositionID.isArenaPosition(targetPosition) && targetPosition != PositionID.BLUE_DISCARDPILE && targetPosition != PositionID.RED_DISCARDPILE
				&& gameModel.getGameModelParameters().activeEffect("00490", cardGameID()))
			gameModel.getGameModelParameters().deactivateEffect("00490", this.card.getGameID());
	}

	private void AncestralMemory() {
		gameModel.getGameModelParameters().activateEffect("00490", this.card.getGameID());
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Ancestral Memory does nothing!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		else
			gameModel.sendTextMessageToAllPlayers("Ancestral Memory does nothing!", "");
		gameModel.sendTextMessageToAllPlayers("Ancestral Memory can't be used anymore!", "");
	}

	private void FlailAround() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 3 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 10, true);
	}
}
