package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00300_MistysTentacool extends PokemonCardScript {

	public Script_00300_MistysTentacool(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Misterious Light", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Jellyfish Pod", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Misterious Light"))
			this.MisteriousLight();
		else
			this.JellyfishPod();
	}

	private void MisteriousLight() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		// Flip coin to check if defending pokemon is asleep:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is asleep!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void JellyfishPod() {
		List<Card> cards = this.getTentacoolCardsFromDeck();
		if (cards.isEmpty())
			this.gameModel.sendTextMessageToAllPlayers("Deck does not contain correct pokemon cards!", "");
		else {
			List<Card> cList = this.getCardOwner().playerChoosesCards(cards, cards.size(), false, "Choose cards for your hand!");
			for (Card c : cList) {
				gameModel.sendCardMessageToAllPlayers(this.getCardOwner().getName() + " puts " + c.getName() + " in his hand!", c, "");
				gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), c.getGameID(), true);

				// Execute animation:
				Animation animation = new CardMoveAnimation(ownDeck(), ownHand(), c.getCardId(), "");
				gameModel.sendAnimationToAllPlayers(animation);
				gameModel.sendGameModelToAllPlayers("");
			}
		}

		gameModel.getAttackAction().shufflePosition(ownDeck());
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<Card> getTentacoolCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if ((pCard.getCardType() == CardType.BASICPOKEMON || pCard.getCardType() == CardType.STAGE1POKEMON || pCard.getCardType() == CardType.STAGE2POKEMON)
					&& (pCard.getName().contains("Brock")))
				cardList.add(c);
		}
		return cardList;
	}
}
