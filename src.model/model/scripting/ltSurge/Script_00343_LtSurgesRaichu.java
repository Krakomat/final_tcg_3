package model.scripting.ltSurge;

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
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00343_LtSurgesRaichu extends PokemonCardScript {

	public Script_00343_LtSurgesRaichu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Kerzap", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Thundertackle", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Kerzap"))
			this.Kerzap();
		else
			this.Thundertackle();
	}

	private void Kerzap() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		gameModel.sendTextMessageToAllPlayers("If heads then " + this.card.getName() + " hurts itself!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);

			// Remove lightning energy:
			PositionID discardPileID = null;
			Position position = gameModel.getPosition(attacker);
			if (position.getColor() == Color.BLUE)
				discardPileID = PositionID.BLUE_DISCARDPILE;
			else
				discardPileID = PositionID.RED_DISCARDPILE;
			ArrayList<Card> energyCards = gameModel.getPosition(attacker).getEnergyCards();
			for (int i = 0; i < energyCards.size(); i++) {
				if (energyCards.get(i).getCardId().equals("00100")) {
					gameModel.getAttackAction().moveCard(energyCards.get(i).getCurrentPosition().getPositionID(), discardPileID, energyCards.get(i).getGameID(), true);
				}
			}
			gameModel.sendGameModelToAllPlayers("");
		} else {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		}
	}

	private void Thundertackle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		} else {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 20, true);
		}
	}
}
