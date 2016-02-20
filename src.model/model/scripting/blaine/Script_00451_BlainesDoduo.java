package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00451_BlainesDoduo extends PokemonCardScript {

	public Script_00451_BlainesDoduo(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Wild Kick", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Retaliate", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Wild Kick"))
			this.WildKick();
		else
			this.Retaliate();
	}

	private void WildKick() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Wild Kick does nothing!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		else
			gameModel.sendTextMessageToAllPlayers("Wild Kick does nothing!", "");
	}

	private void Retaliate() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PokemonCard attackingPokemon = (PokemonCard) gameModel.getPosition(attacker).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int damageOnAttacker = attackingPokemon.getDamageMarks();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, damageOnAttacker, true);
	}
}
