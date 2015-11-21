package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00176_Omastar extends PokemonCardScript {

	public Script_00176_Omastar(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Water Gun", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		this.addAttack("Spike Cannon", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Water Gun"))
			this.waterGun();
		else
			this.spikeCannon();
	}

	private void waterGun() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		List<Element> energy = gameModel.getPosition(attacker).getEnergy();
		int waterCounter = -1;
		for (Element ele : energy)
			if (ele == Element.WATER)
				waterCounter++;

		if (waterCounter < 0)
			waterCounter = 0;
		waterCounter = waterCounter % 3;

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20 + (10 * waterCounter), true);
	}

	private void spikeCannon() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 2 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(2);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 30, true);
	}
}