package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00367_ErikasExeggcute extends PokemonCardScript {

	public Script_00367_ErikasExeggcute(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Eggsplosion", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Psychic", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Eggsplosion"))
			this.Eggsplosion();
		else
			this.Psychic();
	}

	private void Eggsplosion() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int coinNumber = this.card.getCurrentPosition().getEnergy().size();
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips " + coinNumber + " coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(coinNumber);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 10, true);
	}

	private void Psychic() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		Position defendingPosition = defendingPokemon.getCurrentPosition();

		int energyCardDamage = defendingPosition.getEnergyCards().size() * 10;
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 + energyCardDamage, true);
	}
}
