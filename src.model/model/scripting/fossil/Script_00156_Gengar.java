package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
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
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;
		Player enemy = this.getEnemyPlayer();

		if (gameModel.getGameModelParameters().getPower_Activated_00156_Gengar().contains(this.card.getGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() != this.getCardOwner().getColor())
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (this.getDamagedEnemyPositions().isEmpty())
			return false;
		if (this.gameModel.getFullArenaPositions(enemy.getColor()).size() < 2)
			return false;

		return true;
	}

	@Override
	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		PositionID chosenHealPosition = player.playerChoosesPositions(getDamagedEnemyPositions(), 1, true, "Choose a pokemon to remove a damage counter from...")
				.get(0);
		List<PositionID> otherPositions = gameModel.getFullArenaPositions(enemy.getColor());
		otherPositions.remove(chosenHealPosition);
		PositionID chosenDamagePosition = player.playerChoosesPositions(otherPositions, 1, true, "Choose a pokemon to damage ...").get(0);

		PokemonCard healPkmn = (PokemonCard) gameModel.getPosition(chosenHealPosition).getTopCard();
		PokemonCard damagePkmn = (PokemonCard) gameModel.getPosition(chosenDamagePosition).getTopCard();

		// Remove counter (does not count as a heal!):
		healPkmn.setDamageMarks(healPkmn.getDamageMarks() - 10);
		damagePkmn.setDamageMarks(damagePkmn.getDamageMarks() + 10);

		// Check knockout
		if (damagePkmn.getHitpoints() == damagePkmn.getDamageMarks()) {
			gameModel.getAttackAction().inflictConditionToPosition(chosenDamagePosition, PokemonCondition.KNOCKOUT);
			// Check if enemy had DESTINY:
			if (damagePkmn.hasCondition(PokemonCondition.DESTINY))
				gameModel.getAttackAction().inflictConditionToPosition(this.card.getCurrentPosition().getPositionID(), PokemonCondition.KNOCKOUT);
		}
		gameModel.getGameModelParameters().getPower_Activated_00156_Gengar().add(this.card.getGameID());
		gameModel.sendGameModelToAllPlayers("");
		gameModel.cleanDefeatedPositions();
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().getPower_Activated_00156_Gengar().contains(this.card.getGameID())) {
			gameModel.getGameModelParameters().getPower_Activated_00156_Gengar().remove(new Integer(this.card.getGameID()));
		}
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Dark Mind"))
			this.darkMind();
	}

	private void darkMind() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {
			PositionID benchDefender = player.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true,
					"Choose a pokemon that receives the damage!").get(0);
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchDefender, 10, false);
		}
	}

	private List<PositionID> getDamagedEnemyPositions() {
		Player enemy = this.getEnemyPlayer();
		List<PositionID> arenaPositions = gameModel.getFullArenaPositions(enemy.getColor());
		List<PositionID> damagedPositions = new ArrayList<>();
		for (PositionID posID : arenaPositions) {
			PokemonCard topCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (topCard.getDamageMarks() > 0)
				damagedPositions.add(posID);
		}
		return damagedPositions;
	}
}
