package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00010_Mewto extends PokemonCardScript {

	public Script_00010_Mewto(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Psychic", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Barrier", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Psychic"))
			this.psychokinese();
		else
			this.barriere();
	}

	private void psychokinese() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		Position defendingPosition = defendingPokemon.getCurrentPosition();

		int energyCardDamage = defendingPosition.getEnergyCards().size() * 10;
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 + energyCardDamage, true);
	}

	private void barriere() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		costs.add(Element.PSYCHIC);
		gameModel.getAttackAction().playerPaysEnergy(player, costs, card.getCurrentPosition().getPositionID());

		gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " protects itself!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.INVULNERABLE);
		gameModel.sendGameModelToAllPlayers("");
	}
}
