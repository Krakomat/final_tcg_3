package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00004_Glurak extends PokemonCardScript {

	/** Used to store the position on which the pokemon power was executed, so that it can be reset at the end of the turn. */
	private Position pokemonPowerPosition;

	public Script_00004_Glurak(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		this.addAttack("Fire Spin", att1Cost);
		this.addPokemonPower("Energy Burn");
		this.pokemonPowerPosition = null;
	}

	@Override
	public void executeAttack(String attackName) {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		if (pokemonPowerPosition == null) {
			costs.add(Element.FIRE);
			costs.add(Element.FIRE);
		} else {
			// Can be payed by any energy now:
			costs.add(Element.COLORLESS);
			costs.add(Element.COLORLESS);
		}
		gameModel.getAttackAction().playerPaysEnergy(player, costs, card.getCurrentPosition().getPositionID());

		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 100, true);
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Can be executed when Glurak is in play and isn't asleep, confused or paralyzed and its owner is on turn
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
	public void executePokemonPower(String powerName) {
		// Turn all energy attached to glurak into fire energy:
		PokemonCard card = (PokemonCard) this.card;
		Position pos = card.getCurrentPosition();

		List<Element> providedEnergy = new ArrayList<>();
		for (Card e : pos.getEnergyCards())
			for (int i = 0; i < ((EnergyCard) e).getProvidedEnergy().size(); i++)
				providedEnergy.add(Element.FIRE);

		pos.setEnergy(providedEnergy);
		this.pokemonPowerPosition = pos;

		gameModel.sendGameModelToAllPlayers("");
	}

	public void executeEndTurnActions() {
		if (this.pokemonPowerPosition != null) {
			// Reset the provided energy:
			this.pokemonPowerPosition.setEnergy(new ArrayList<Element>());
			this.pokemonPowerPosition = null; // reset position
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
