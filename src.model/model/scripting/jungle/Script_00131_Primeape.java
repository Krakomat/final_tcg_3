package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00131_Primeape extends PokemonCardScript {

	public Script_00131_Primeape(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Fury Swipes", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Tantrum", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Fury Swipes"))
			this.furySwipes();
		else
			this.tantrum();
	}

	private void furySwipes() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 3 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 20, true);
	}

	private void tantrum() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);

		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();
		// Flip coin to check if attacking pokemon is confused:
		gameModel.sendTextMessageToAllPlayers("If tails then " + attackingPokemon.getName() + " is confused!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c, "");
		if (c == Coin.TAILS) {
			gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.CONFUSED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
