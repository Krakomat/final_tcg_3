package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00453_BlainesMankey extends PokemonCardScript {

	public Script_00453_BlainesMankey(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Pranks", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		this.addAttack("Fury Swipes", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Pranks"))
			this.Pranks();
		else
			this.FurySwipes();
	}

	private void Pranks() {
		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Pranks does nothing!", "");
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			Position discardPile = gameModel.getPosition(enemyDiscardPile());
			if (discardPile.isEmpty())
				gameModel.sendTextMessageToAllPlayers("Discard Pile does not contain any cards! Pranks does nothing!", "");
			else {
				Card c = getCardOwner().playerChoosesCards(discardPile.getCards(), 1, true, "Choose a card to put onto " + getEnemyPlayer().getName() + "'s deck...").get(0);
				gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " moves " + c.getName() + " onto " + getEnemyPlayer().getName() + "'s deck!", c, "");
				gameModel.getAttackAction().moveCard(enemyDiscardPile(), enemyDeck(), c.getGameID(), true);
				gameModel.sendGameModelToAllPlayers("");
			}
		} else
			gameModel.sendTextMessageToAllPlayers("Pranks does nothing!", "");
	}

	private void FurySwipes() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 3 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 10, true);
	}
}
