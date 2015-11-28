package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00250_Psyduck extends PokemonCardScript {

	public Script_00250_Psyduck(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Dizziness", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Water Gun", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Dizziness"))
			this.Dizziness();
		else
			this.WaterGun();
	}

	private void Dizziness() {
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " draws a card!", "");
		this.gameModel.getAttackAction().playerDrawsCards(1, getCardOwner());
	}

	private void WaterGun() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		List<Element> energy = gameModel.getPosition(attacker).getEnergy();
		int energyCounter = energy.size();
		int waterCounter = -2;
		for (Element ele : energy)
			if (ele == Element.WATER)
				waterCounter++;

		if (energyCounter == 3)
			waterCounter = waterCounter - 1;
		if (waterCounter < 0)
			waterCounter = 0;
		waterCounter = waterCounter % 3;

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20 + (10 * waterCounter), true);
	}
}
