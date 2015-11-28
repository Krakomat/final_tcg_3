package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00027_Porenta extends PokemonCardScript {

	public Script_00027_Porenta(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Leek Slap", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Pot Smash", att2Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Leek Slap") && gameModel.getGameModelParameters().getLauchschlagUsed_00027_Porenta().contains(card.getGameID()))
			return false;
		return super.attackCanBeExecuted(attackName);
	}

	public void moveToPosition(PositionID targetPosition) {
		if (!PositionID.isArenaPosition(targetPosition) && targetPosition != PositionID.BLUE_DISCARDPILE && targetPosition != PositionID.RED_DISCARDPILE)
			gameModel.getGameModelParameters().getLauchschlagUsed_00027_Porenta().remove(new Integer(this.card.getGameID()));
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Leek Slap"))
			this.lauchschlag();
		else
			this.topfschmetterer();
	}

	private void lauchschlag() {
		gameModel.getGameModelParameters().getLauchschlagUsed_00027_Porenta().add(new Integer(this.card.getGameID()));
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Leek Slap does nothing!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		else
			gameModel.sendTextMessageToAllPlayers("Leek Slap does nothing!", "");
		gameModel.sendTextMessageToAllPlayers("Leek Slap can't be used anymore!", "");
	}

	private void topfschmetterer() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}
}
