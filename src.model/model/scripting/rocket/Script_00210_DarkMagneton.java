package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00210_DarkMagneton extends PokemonCardScript {

	public Script_00210_DarkMagneton(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Wing Attack", att1Cost);

		this.addPokemonPower("Prehistoric Power");
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Wing Attack"))
			this.wingAttack();
	}

	public void moveToPosition(PositionID targetPosition) {
		super.moveToPosition(targetPosition);

		// Remove gameID to the power list of Aerodactyl:
		if (!PositionID.isArenaPosition(targetPosition))
			this.gameModel.getGameModelParameters().getPower_Active_00153_Aerodactyl().remove(new Integer(this.card.getGameID()));
	}

	public void playFromHand() {
		super.playFromHand();

		// Add gameID to the power list of Aerodactyl:
		this.gameModel.getGameModelParameters().getPower_Active_00153_Aerodactyl().add(this.card.getGameID());
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		super.pokemonGotCondition(turnNumber, condition);

		// Remove gameID to the power list of Aerodactyl:
		if (condition == PokemonCondition.ASLEEP || condition == PokemonCondition.CONFUSED || condition == PokemonCondition.PARALYZED)
			this.gameModel.getGameModelParameters().getPower_Active_00153_Aerodactyl().remove(new Integer(this.card.getGameID()));
	}

	private void wingAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}
}
