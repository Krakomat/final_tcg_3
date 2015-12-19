package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00379_ErikasJigglypuff extends PokemonCardScript {

	public Script_00379_ErikasJigglypuff(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Group Therapy", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Pulled Punch", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Group Therapy"))
			this.GroupTherapy();
		else
			this.PulledPunch();
	}

	private void GroupTherapy() {
		// Execute heal(messages to clients send there):
		List<PositionID> damagedPositions = this.getDamagedPositions();
		for (PositionID posID : damagedPositions)
			gameModel.getAttackAction().healPosition(posID, 10);
	}

	private List<PositionID> getDamagedPositions() {
		Player player = this.getCardOwner();
		List<PositionID> arenaPositions = gameModel.getFullArenaPositions(player.getColor());
		List<PositionID> enemyArenaPositions = gameModel.getFullArenaPositions(getEnemyPlayer().getColor());
		List<PositionID> damagedPositions = new ArrayList<>();
		for (PositionID posID : arenaPositions) {
			PokemonCard topCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (topCard.getDamageMarks() > 0)
				damagedPositions.add(posID);
		}
		for (PositionID posID : enemyArenaPositions) {
			PokemonCard topCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (topCard.getDamageMarks() > 0)
				damagedPositions.add(posID);
		}
		return damagedPositions;
	}

	private void PulledPunch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		PokemonCard topCard = (PokemonCard) gameModel.getPosition(defender).getTopCard();
		int damage = 10;
		if (topCard.getDamageMarks() == 0)
			damage = 40;

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, damage, true);
	}
}
