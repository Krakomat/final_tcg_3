package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00002_Turtok extends PokemonCardScript {

	public Script_00002_Turtok(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		this.addAttack("Hydro Pump", att1Cost);
		this.addPokemonPower("Rain Dance");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		List<Element> energy = gameModel.getPosition(attacker).getEnergy();
		int waterCounter = -3;
		for (Element ele : energy)
			if (ele == Element.WATER)
				waterCounter++;

		if (waterCounter < 0)
			waterCounter = 0;
		waterCounter = waterCounter % 3;

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40 + (10 * waterCounter), true);
	}

	public void energyCardPlayed(EnergyCard energyCard) {
		if (energyCard.isBasisEnergy() && energyCard.getProvidedEnergy().get(0) == Element.WATER && this.regentanzCanBeExecuted())
			this.executePokemonPower("Regentanz");
	}

	private boolean regentanzCanBeExecuted() {
		// Can be executed when Turtok is in play and isn't asleep, confused or paralyzed and its owner is on turn
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() != this.getCardOwner().getColor())
			return false;
		return true;
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public void executePokemonPower(String powerName) {
		// Reset energy played in game model:
		gameModel.setEnergyPlayed(false);
	}
}
