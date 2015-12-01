package model.scripting.rocket;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00211_DarkSlowbro extends PokemonCardScript {

	public Script_00211_DarkSlowbro(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Fickle Attack", att1Cost);

		this.addPokemonPower("Reel In");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public void playFromHand() {
		super.playFromHand();

		if (this.gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty() && gameModel.getPosition(ownDiscardPile()).getPokemonCards().size() > 0) {
			Player player = this.getCardOwner();
			List<Card> cards = player.playerChoosesCards(gameModel.getPosition(ownDiscardPile()).getPokemonCards(), 3, false,
					"Choose up to 3 pokemon cards from your discard pile!");

			if (!cards.isEmpty()) {
				gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Reel In!", card, "");
				for (Card card : cards) {
					gameModel.sendTextMessageToAllPlayers(player.getName() + " recovers " + card.getName() + " from his discard pile!", "");
					gameModel.getAttackAction().moveCard(ownDiscardPile(), ownHand(), card.getGameID(), true);

					// Execute animation:
					Animation animation = new CardMoveAnimation(ownDiscardPile(), ownHand(), card.getCardId(), "");
					gameModel.sendAnimationToAllPlayers(animation);
					gameModel.sendGameModelToAllPlayers("");
				}
			}
		}
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Fickle Attack does nothing!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		else
			gameModel.sendTextMessageToAllPlayers("Fickle Attack does nothing!", "");
	}
}
