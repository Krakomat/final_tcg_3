package model.scripting.giovanni;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00486_GiovannisMeowth extends PokemonCardScript {

	public Script_00486_GiovannisMeowth(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("False Charity", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Double Scratch", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("False Charity"))
			this.FalseCharity();
		else
			this.DoubleScratch();
	}

	private void FalseCharity() {
		Card topCard = gameModel.getPosition(enemyDeck()).getTopCard();
		gameModel.sendCardMessageToAllPlayers("The top card on " + getEnemyPlayer().getName() + "'s deck is " + topCard.getName(), topCard, "");

		if (topCard instanceof TrainerCard) {
			gameModel.getAttackAction().moveCard(enemyDeck(), enemyDiscardPile(), topCard.getGameID(), true);
			Animation animation = new CardMoveAnimation(enemyDeck(), enemyDiscardPile(), card.getCardId(), "");
			gameModel.sendAnimationToAllPlayers(animation);
		} else {
			gameModel.getAttackAction().moveCard(enemyDeck(), enemyHand(), topCard.getGameID(), true);
			Animation animation = new CardMoveAnimation(enemyDeck(), enemyHand(), card.getCardId(), "");
			gameModel.sendAnimationToAllPlayers(animation);
		}

		gameModel.sendGameModelToAllPlayers("");
	}

	private void DoubleScratch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 2 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(2);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 20, true);
	}
}
