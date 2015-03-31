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

public class Script_00050_Nebulak extends PokemonCardScript {

	public Script_00050_Nebulak(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Schlafgas", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Schicksalsband", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Schlafgas"))
			this.schlafgas();
		else
			this.schicksalsband();
	}

	private void schlafgas() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		// Flip coin to check if defending pokemon is asleep:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is asleep!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c, "");
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is asleep!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void schicksalsband() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		costs.add(Element.PSYCHIC);
		gameModel.getAttackAction().playerPaysEnergy(player, costs, card.getCurrentPosition().getPositionID());

		gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " establishes a curse!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.DESTINY);
		gameModel.sendGameModelToAllPlayers("");
	}
}
