package model.scripting.teamRocket;

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

public class Script_00470_RocketsMewtwo extends PokemonCardScript {

	public Script_00470_RocketsMewtwo(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Juxtapose", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Hypnoblast", att2Cost);

		List<Element> att3Cost = new ArrayList<>();
		att3Cost.add(Element.PSYCHIC);
		att3Cost.add(Element.PSYCHIC);
		att3Cost.add(Element.PSYCHIC);
		att3Cost.add(Element.COLORLESS);
		this.addAttack("Psyburn", att3Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Juxtapose"))
			this.Juxtapose();
		else if (attackName.equals("Hypnoblast"))
			this.Hypnoblast();
		else
			this.Psyburn();
	}

	private void Juxtapose() {
		// Flip coin to check if defending pokemon is paralyzed:
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
			PokemonCard ownPokemon = (PokemonCard) gameModel.getPosition(getCurrentPositionID()).getTopCard();
			PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
			int ownDamageMarks = ownPokemon.getDamageMarks();
			int enemyDamageMarks = defendingPokemon.getDamageMarks();

			defendingPokemon.setDamageMarks(ownDamageMarks);
			ownPokemon.setDamageMarks(enemyDamageMarks);

			gameModel.sendTextMessageToAllPlayers("Damage marks have been switched!", "");

			if (ownPokemon.getDamageMarks() >= ownPokemon.getHitpoints() && !ownPokemon.hasCondition(PokemonCondition.KNOCKOUT)) {
				ownPokemon.setDamageMarks(ownPokemon.getHitpoints());
				ownPokemon.setDamageMarks(enemyDamageMarks);
				gameModel.getAttackAction().inflictConditionToPosition(ownPokemon.getCurrentPosition().getPositionID(), PokemonCondition.KNOCKOUT);
				gameModel.sendGameModelToAllPlayers("");
				gameModel.cleanDefeatedPositions();
			} else
				gameModel.sendGameModelToAllPlayers("");

			if (defendingPokemon.getDamageMarks() >= defendingPokemon.getHitpoints() && !defendingPokemon.hasCondition(PokemonCondition.KNOCKOUT)) {
				defendingPokemon.setDamageMarks(defendingPokemon.getHitpoints());
				gameModel.getAttackAction().inflictConditionToPosition(defendingPokemon.getCurrentPosition().getPositionID(), PokemonCondition.KNOCKOUT);
				gameModel.sendGameModelToAllPlayers("");
				gameModel.cleanDefeatedPositions();
			} else
				gameModel.sendGameModelToAllPlayers("");

			gameModel.sendGameModelToAllPlayers("");
		} else
			gameModel.sendTextMessageToAllPlayers("Juxtapose missed!", "");
	}

	private void Hypnoblast() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is asleep!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is asleep!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void Psyburn() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);
	}
}
