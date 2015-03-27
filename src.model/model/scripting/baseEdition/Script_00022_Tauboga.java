package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00022_Tauboga extends PokemonCardScript {

	/** Turnnumber at which Tauboga got damaged/a condition the last time */
	private int lastAttackTurn;
	/** Stores all damaging actions against tauboga */
	private Map<Integer, Integer> damageHistory;
	/** Stores all conditions inflicted against Tauboga */
	private Map<Integer, PokemonCondition> conditionHistory;

	public Script_00022_Tauboga(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Wirbelwind", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Spiegeltrick", att2Cost);

		this.lastAttackTurn = -100;
		this.damageHistory = new HashMap<>();
		this.conditionHistory = new HashMap<>();
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Wirbelwind"))
			this.wirbelwind();
		else
			this.spiegeltrick();
	}

	private void wirbelwind() {
		// Get the player:
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		if (gameModel.getFullBenchPositions(player.getColor()).size() > 0 && !defendingPokemon.hasCondition(PokemonCondition.INVULNERABLE)) {
			// Let enemy choose bench pokemon and swap it with his active:
			gameModel.sendTextMessageToAllPlayers(enemy.getName() + " chooses a new active pokemon");
			PositionID chosenPosition = enemy.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true,
					"Choose a pokemon to swap wtih your active!").get(0);
			Card newPkm = gameModel.getPosition(chosenPosition).getTopCard();
			gameModel.sendTextMessageToAllPlayers(newPkm.getName() + " is the new active pokemon!");
			gameModel.getAttackAction().swapPokemon(defender, chosenPosition);
			gameModel.sendGameModelToAllPlayers();
		}
	}

	private void spiegeltrick() {
		// Check if tauboga was attacked last turn:
		int currentTurnNumber = gameModel.getTurnNumber();
		if (currentTurnNumber - 1 == lastAttackTurn) {
			PositionID attacker = this.card.getCurrentPosition().getPositionID();
			PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
			Element attackerElement = ((PokemonCard) this.card).getElement();

			if (this.damageHistory.containsKey(lastAttackTurn))
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, damageHistory.get(lastAttackTurn), true);

			if (this.conditionHistory.containsKey(lastAttackTurn))
				gameModel.getAttackAction().inflictConditionToPosition(defender, conditionHistory.get(lastAttackTurn));
		} else
			gameModel.sendTextMessageToAllPlayers("Spiegeltrick does nothing!");
	}

	public void pokemonIsDamaged(int turnNumber, int damage) {
		this.lastAttackTurn = turnNumber;
		this.damageHistory.put(turnNumber, damage);
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		this.lastAttackTurn = turnNumber;
		this.conditionHistory.put(turnNumber, condition);
	}
}
