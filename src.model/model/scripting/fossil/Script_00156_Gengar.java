package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00156_Gengar extends PokemonCardScript {

	public Script_00156_Gengar(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Dark Mind", att1Cost);
		
		this.addPokemonPower("Curse");
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Dark Mind"))
			this.darkMind();
	}

	private void darkMind() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
		gameModel.sendGameModelToAllPlayers("");
	}
}
