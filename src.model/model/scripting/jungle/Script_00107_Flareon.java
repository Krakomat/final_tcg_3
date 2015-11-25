package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00107_Flareon extends PokemonCardScript {

	public Script_00107_Flareon(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Quick Attack", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Flamethrower", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Quick Attack"))
			this.quickAttack();
		else
			this.flamethrower();
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

	private void flamethrower() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		costs.add(Element.FIRE);
		gameModel.getAttackAction().playerPaysEnergy(player, costs, card.getCurrentPosition().getPositionID());

		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);
	}
}
