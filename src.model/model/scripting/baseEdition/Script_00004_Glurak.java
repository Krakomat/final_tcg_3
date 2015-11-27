package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private Map<Integer, List<Element>> cardGameIDs;

	public Script_00004_Glurak(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		this.addAttack("Fire Spin", att1Cost);
		this.addPokemonPower("Energy Burn");
		this.cardGameIDs = new HashMap<>();
	}

	@Override
	public void executeAttack(String attackName) {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		costs.add(Element.FIRE);
		costs.add(Element.FIRE);
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
		return super.pokemonPowerCanBeExecuted(powerName);
	}

	@Override
	public void executePokemonPower(String powerName) {
		this.cardGameIDs = new HashMap<>();
		// Turn all energy attached to glurak into fire energy:
		PokemonCard card = (PokemonCard) this.card;
		Position pos = card.getCurrentPosition();

		for (Card e : pos.getEnergyCards()) {
			EnergyCard eCard = (EnergyCard) e;
			List<Element> originalEnergy = new ArrayList<>();
			for (int i = 0; i < eCard.getProvidedEnergy().size(); i++) {
				originalEnergy.add(eCard.getProvidedEnergy().remove(i));
				eCard.getProvidedEnergy().add(i, Element.FIRE);
			}
			if (!this.cardGameIDs.containsKey(eCard.getGameID()))
				this.cardGameIDs.put(eCard.getGameID(), originalEnergy);
		}

		gameModel.sendGameModelToAllPlayers("");
	}

	public void executeEndTurnActions() {
		if (!this.cardGameIDs.isEmpty()) {
			// Reset the provided energy:
			for (Integer gameID : this.cardGameIDs.keySet()) {
				EnergyCard eCard = (EnergyCard) gameModel.getCard(gameID);
				eCard.setProvidedEnergy(this.cardGameIDs.get(gameID));
			}
			this.cardGameIDs.clear();
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
