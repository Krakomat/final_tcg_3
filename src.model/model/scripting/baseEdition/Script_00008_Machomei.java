package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00008_Machomei extends PokemonCardScript {

	private PositionID attackerPosition;

	public Script_00008_Machomei(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Seismic Toss", att1Cost);
		this.addPokemonPower("Strikes Back");
		attackerPosition = null;
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);
	}

	public void pokemonIsDamaged(int turnNumber, int damage, PositionID source) {
		if (gegenschlagCanBeExecuted()) {
			this.attackerPosition = source;
			if (this.attackerPosition != null)
				this.executePokemonPower("Strikes Back");
		}
	}

	/*
	 * Only is executed when Machomei is damaged. This automatically implies that machomei has to be in play.
	 */
	private boolean gegenschlagCanBeExecuted() {
		// Can be executed when Machomei isn't asleep, confused or paralyzed. Also can only be executed when the owner of machomei is not on turn, to prevent chain
		// reactions between two opposing Machomei.
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() == this.getCardOwner().getColor())
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
		gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates " + powerName + "!", card, "");
		// Inflict damage:
		PositionID ownPosition = this.card.getCurrentPosition().getPositionID();
		gameModel.getAttackAction().inflictDamageToPosition(Element.ROCK, ownPosition, attackerPosition, 10, false);
	}
}
