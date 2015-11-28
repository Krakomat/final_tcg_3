package model.scripting.rocket;

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

public class Script_00233_Porygon extends PokemonCardScript {

	public Script_00233_Porygon(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Conversion I", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Psybeam", att2Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		if (attackName.equals("Conversion I") && defendingPokemon.getCurrentWeakness() == null)
			return false;
		return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Conversion I"))
			this.ConversionI();
		else
			this.Psybeam();
	}

	private void ConversionI() {
		Player player = this.getCardOwner();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		if (!defendingPokemon.hasCondition(PokemonCondition.INVULNERABLE)) {
			ArrayList<Element> elementList = new ArrayList<>();
			elementList.add(Element.FIRE);
			elementList.add(Element.GRASS);
			elementList.add(Element.LIGHTNING);
			elementList.add(Element.PSYCHIC);
			elementList.add(Element.ROCK);
			elementList.add(Element.WATER);

			Element chosenElement = player.playerChoosesElements(elementList, 1, true, "Choose a new weakness for the defending pokemon!").get(0);

			PokemonCard targetPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
			targetPokemon.setCurrentWeakness(chosenElement);
			gameModel.sendTextMessageToAllPlayers(targetPokemon.getName() + "'s weakness is changed to " + chosenElement.toString(), "");
		} else
			gameModel.sendTextMessageToAllPlayers("Conversion I has no effect on " + defendingPokemon.getName() + "!", "");
	}

	private void Psybeam() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is confused:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is confused!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
