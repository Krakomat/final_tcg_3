package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.AnimationType;
import gui2d.animations.DamageAnimation;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00311_MistysStaryu extends PokemonCardScript {

	public Script_00311_MistysStaryu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Swift", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attackerPositionID = this.card.getCurrentPosition().getPositionID();
		PositionID targetPosition = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		int damageAmount = 20;
		PokemonCard defenderPokemon = (PokemonCard) gameModel.getPosition(targetPosition).getTopCard();
		PokemonCard attackerPokemon = null;
		if (attackerPositionID != null)
			attackerPokemon = (PokemonCard) gameModel.getPosition(attackerPositionID).getTopCard();

		// Check boosts on attacker:
		if (attackerPokemon != null && attackerPokemon.hasCondition(PokemonCondition.DAMAGEINCREASE10))
			damageAmount = damageAmount + 10;

		// Check Misty:
		if (attackerPokemon != null && attackerPokemon.getName().contains("Misty") && gameModel.getGameModelParameters().isActivated_00296_Misty())
			damageAmount = damageAmount + 20;
		// Check Vermillion City Gym:
		if (gameModel.getGameModelParameters().isVermillionCityGymAttackModifier() && damageAmount > 0)
			damageAmount = damageAmount + 10;

		// Normalize damage:
		if (damageAmount < 0)
			damageAmount = 0;

		// Test if attacker pokemon is able to modify the outgoing damage:
		if (attackerPokemon != null) {
			PokemonCardScript attackerScript = (PokemonCardScript) attackerPokemon.getCardScript();
			damageAmount = attackerScript.modifyOutgoingDamage(damageAmount);
		}

		// Damage Pokemon:
		defenderPokemon.setDamageMarks(defenderPokemon.getDamageMarks() + damageAmount);

		// Normalize hitpoints:
		if (defenderPokemon.getDamageMarks() > defenderPokemon.getHitpoints())
			defenderPokemon.setDamageMarks(defenderPokemon.getHitpoints());

		// Apply knockout condition if hitpoints = damagepoints:
		boolean superRetal = false;
		boolean fireWall = false;
		boolean retal = false;
		if (defenderPokemon.getHitpoints() == defenderPokemon.getDamageMarks()) {
			if (defenderPokemon.hasCondition(PokemonCondition.FIRE_WALL))
				fireWall = true;
			if (defenderPokemon.hasCondition(PokemonCondition.SUPER_RETALIATION))
				superRetal = true;
			if (defenderPokemon.hasCondition(PokemonCondition.RETALIATION))
				retal = true;
			gameModel.getAttackAction().inflictConditionToPosition(targetPosition, PokemonCondition.KNOCKOUT);
			// Check if enemy had DESTINY:
			if (defenderPokemon.hasCondition(PokemonCondition.DESTINY) && attackerPositionID != null)
				gameModel.getAttackAction().inflictConditionToPosition(attackerPositionID, PokemonCondition.KNOCKOUT);
		}

		// Call pokemonIsDamaged() on defending pokemon script:
		PokemonCardScript script = (PokemonCardScript) defenderPokemon.getCardScript();
		script.pokemonIsDamaged(gameModel.getTurnNumber(), damageAmount, attackerPositionID);

		this.gameModel.sendTextMessageToAllPlayers(defenderPokemon.getName() + " takes " + damageAmount + " damage!", "");
		if (damageAmount > 0) {
			// Execute animation:
			Animation animation = new DamageAnimation(AnimationType.DAMAGE_POSITION, targetPosition, damageAmount);
			gameModel.sendAnimationToAllPlayers(animation);
		}
		this.gameModel.sendGameModelToAllPlayers("");

		// Check Koga:
		if (attackerPokemon != null && attackerPokemon.getName().contains("Koga") && gameModel.getGameModelParameters().isActivated_00385_Koga() && damageAmount > 0)
			this.gameModel.getAttackAction().inflictConditionToPosition(targetPosition, PokemonCondition.POISONED);

		if (retal && attackerPositionID != null && damageAmount > 0)
			gameModel.getAttackAction().inflictDamageToPosition(defenderPokemon.getElement(), defenderPokemon.getCurrentPosition().getPositionID(), attackerPositionID,
					damageAmount, true);
		if (fireWall && attackerPositionID != null && damageAmount > 0)
			gameModel.getAttackAction().inflictDamageToPosition(defenderPokemon.getElement(), defenderPokemon.getCurrentPosition().getPositionID(), attackerPositionID, 10, true);
		if (superRetal && attackerPositionID != null && damageAmount > 0
				&& gameModel.getAttackAction().flipACoin() == Coin.HEADS)
			gameModel.getAttackAction().inflictDamageToPosition(defenderPokemon.getElement(), defenderPokemon.getCurrentPosition().getPositionID(), attackerPositionID,
					damageAmount * 2, true);
	}
}
