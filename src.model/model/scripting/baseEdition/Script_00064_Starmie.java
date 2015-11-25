package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00064_Starmie extends PokemonCardScript {

	public Script_00064_Starmie(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		this.addAttack("Recover", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Star Freeze", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Recover"))
			this.erholung();
		else
			this.sternenfrost();
	}

	private void erholung() {
		Player player = this.getCardOwner();

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		costs.add(Element.WATER);
		gameModel.getAttackAction().playerPaysEnergy(player, costs, card.getCurrentPosition().getPositionID());

		// Recover:
		gameModel.getAttackAction().fullHealPosition(card.getCurrentPosition().getPositionID());
	}

	private void sternenfrost() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is poisoned:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
