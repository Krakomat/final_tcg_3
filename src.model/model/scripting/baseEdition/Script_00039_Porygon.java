package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00039_Porygon extends PokemonCardScript {

	public Script_00039_Porygon(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Umwandlung 1", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Umwandlung 2", att2Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PokemonCard attackingPokemon = (PokemonCard) gameModel.getPosition(attacker).getTopCard();

		if (attackName.equals("Umwandlung 1") && defendingPokemon.getCurrentWeakness() == null)
			return false;
		else if (attackName.equals("Umwandlung 2") && attackingPokemon.getCurrentResistance() == null)
			return false;

		return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Umwandlung 1"))
			this.umwandlung1();
		else
			this.umwandlung2();
	}

	private void umwandlung1() {
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
			gameModel.sendTextMessageToAllPlayers(targetPokemon.getName() + "'s weakness is changed to " + chosenElement.toString());
		} else
			gameModel.sendTextMessageToAllPlayers("Umwandlung 1 has no effect on " + defendingPokemon.getName() + "!");

	}

	private void umwandlung2() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();

		ArrayList<Element> elementList = new ArrayList<>();
		elementList.add(Element.FIRE);
		elementList.add(Element.GRASS);
		elementList.add(Element.LIGHTNING);
		elementList.add(Element.PSYCHIC);
		elementList.add(Element.ROCK);
		elementList.add(Element.WATER);

		Element chosenElement = player.playerChoosesElements(elementList, 1, true, "Choose a new resistance for the your active pokemon!").get(0);

		PokemonCard targetPokemon = (PokemonCard) gameModel.getPosition(attacker).getTopCard();
		targetPokemon.setCurrentResistance(chosenElement);
		gameModel.sendTextMessageToAllPlayers(targetPokemon.getName() + "'s resistance is changed to " + chosenElement.toString());
	}
}
