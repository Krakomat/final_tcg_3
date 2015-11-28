package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00213_DarkWeezing extends PokemonCardScript {

	public Script_00213_DarkWeezing(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Mass Explosion", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Stun Gas", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Mass Explosion"))
			this.MassExplosion();
		else
			this.StunGas();
	}

	private void MassExplosion() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		List<PositionID> koffingList = getKoffingsFromPlay();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, koffingList.size() * 20, true);

		for (PositionID posID : koffingList) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, posID, 20, false);
		}
	}

	private List<PositionID> getKoffingsFromPlay() {
		List<PositionID> posList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(Color.BLUE)) {
			String name = gameModel.getPosition(posID).getTopCard().getName();
			if (name.equals("Koffing") || name.equals("Weezing") || name.equals("Dark Weezing"))
				posList.add(posID);
		}
		return posList;
	}

	private void StunGas() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is poisoned:
		gameModel.sendTextMessageToAllPlayers("Check coinflip for condition!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
		} else {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
		}
		gameModel.sendGameModelToAllPlayers("");
	}
}
