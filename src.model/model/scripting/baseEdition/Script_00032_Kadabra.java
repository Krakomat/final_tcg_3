package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00032_Kadabra extends PokemonCardScript {

	public Script_00032_Kadabra(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Recover", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Super Psy", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Recover"))
			this.erholung();
		else
			this.superPsychoschock();
	}

	private void erholung() {
		Player player = this.getCardOwner();

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		costs.add(Element.PSYCHIC);
		gameModel.getAttackAction().playerPaysEnergy(player, costs, card.getCurrentPosition().getPositionID());

		// Recover:
		gameModel.getAttackAction().fullHealPosition(card.getCurrentPosition().getPositionID());
	}

	private void superPsychoschock() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);
	}
}
