package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Triple;
import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00022_Tauboga extends PokemonCardScript {

	public Script_00022_Tauboga(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Whirlwind", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Mirror Move", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Whirlwind"))
			this.wirbelwind();
		else
			this.spiegeltrick();
	}

	private void wirbelwind() {
		// Get the player:
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0 && !defendingPokemon.hasCondition(PokemonCondition.INVULNERABLE)) {
			// Let enemy choose bench pokemon and swap it with his active:
			gameModel.sendTextMessageToAllPlayers(enemy.getName() + " chooses a new active pokemon", "");
			PositionID chosenPosition = enemy.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true, "Choose a pokemon to swap wtih your active!")
					.get(0);
			Card newPkm = gameModel.getPosition(chosenPosition).getTopCard();
			gameModel.sendTextMessageToAllPlayers(newPkm.getName() + " is the new active pokemon!", "");
			gameModel.getAttackAction().swapPokemon(defender, chosenPosition);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void spiegeltrick() {
		// Check if tauboga was attacked last turn:
		Integer damage = 0;
		List<PokemonCondition> cond = new ArrayList<>();
		for (Triple<Integer, String, Integer> triple : gameModel.getGameModelParameters().getBlockedAttacks()) {
			if (triple.getKey() == cardGameID() && PokemonCondition.isValue(triple.getValue())) {
				PokemonCondition c = PokemonCondition.valueOf(triple.getValue());
				cond.add(c);
			}
			if (triple.getKey() == cardGameID() && isInt(triple.getValue()))
				damage = Integer.valueOf(triple.getValue());
		}
		if (damage > 0 || cond.size() > 0) {
			PositionID attacker = this.card.getCurrentPosition().getPositionID();
			PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
			Element attackerElement = ((PokemonCard) this.card).getElement();

			if (damage > 0)
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, damage, true);

			if (cond.size() > 0) {
				for (PokemonCondition c : cond)
					gameModel.getAttackAction().inflictConditionToPosition(defender, c);
			}
		} else
			gameModel.sendTextMessageToAllPlayers("Mirror Move does nothing!", "");
	}

	private boolean isInt(String value) {
		try {
			Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public void pokemonIsDamaged(int turnNumber, int damage, PositionID source) {
		gameModel.getGameModelParameters().getBlockedAttacks().add(new Triple<Integer, String, Integer>(cardGameID(), String.valueOf(damage), 2));
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		gameModel.getGameModelParameters().getBlockedAttacks().add(new Triple<Integer, String, Integer>(cardGameID(), condition.toString(), 2));
	}
}
