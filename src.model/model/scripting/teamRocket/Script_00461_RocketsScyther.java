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

public class Script_00461_RocketsScyther extends PokemonCardScript {

	public Script_00461_RocketsScyther(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Shadow Images", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Blinding Scythe", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Shadow Images"))
			this.ShadowImages();
		else
			this.BlindingScythe();
	}

	public void moveToPosition(PositionID targetPosition) {
		PokemonCard attackingPokemon = (PokemonCard) this.card;
		if (PositionID.isBenchPosition(targetPosition) && attackingPokemon.hasCondition(PokemonCondition.SHADOW_IMAGE)) {
			gameModel.getAttackAction().cureCondition(targetPosition, PokemonCondition.SHADOW_IMAGE);
		}
	}

	private void ShadowImages() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();

		gameModel.sendTextMessageToAllPlayers(card.getName() + " maintains Shadow Images!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.SHADOW_IMAGE);
		gameModel.sendGameModelToAllPlayers("");
	}

	public int modifyIncomingDamage(int damage, Card attacker) {
		PokemonCard pokemon = (PokemonCard) this.card;
		if (pokemon.hasCondition(PokemonCondition.SHADOW_IMAGE)) {
			gameModel.sendCardMessageToAllPlayers("Check for Shadow Images on " + this.card.getName() + "!", card, "");
			Coin c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.HEADS) {
				return 0;
			}
		}
		return damage;
	}

	private void BlindingScythe() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
	}
}
