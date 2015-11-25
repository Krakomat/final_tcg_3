package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00108_Jolteon extends PokemonCardScript {

	public Script_00108_Jolteon(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Quick Attack", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Pin Missile", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Quick Attack"))
			this.quickAttack();
		else
			this.pinMissile();
	}

	private void quickAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 20 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void pinMissile() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 4 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(4);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 20, true);
	}
}
