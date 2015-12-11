package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00336_LtSurgesPikachu extends PokemonCardScript {

	public Script_00336_LtSurgesPikachu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Charge", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Lightning Tail", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Charge"))
			this.Charge();
		else
			this.LightningTail();
	}

	private void Charge() {
		Position discardPile = gameModel.getPosition(ownDiscardPile());
		Card card = null;
		for (Card c : discardPile.getEnergyCards())
			if (c.getCardId().equals("00100"))
				card = c;
		if (card != null) {
			gameModel.getAttackAction().moveCard(ownDiscardPile(), getCurrentPositionID(), card.getGameID(), false);
			gameModel.sendGameModelToAllPlayers("");
		} else {
			gameModel.sendTextMessageToAllPlayers("No Lightning energy found!", "");
		}
	}

	private void LightningTail() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
