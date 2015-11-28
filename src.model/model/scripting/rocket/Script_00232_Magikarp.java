package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00232_Magikarp extends PokemonCardScript {

	public Script_00232_Magikarp(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Flop", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		this.addAttack("Rapid Evolution", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Flop"))
			this.Flop();
		else
			this.RapidEvolution();
	}

	private void Flop() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void RapidEvolution() {
		List<Card> cards = this.getGyaradosCardsFromDeck();
		if (cards.isEmpty())
			this.gameModel.sendTextMessageToAllPlayers("Deck does not contain correct evolution cards!", "");
		else {
			Card c = this.getCardOwner().playerChoosesCards(cards, 1, true, "Choose a card for evoultion!").get(0);
			gameModel.getAttackAction().evolvePokemon(ownActive(), c.getGameID());
		}

		gameModel.getAttackAction().shufflePosition(ownDeck());
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<Card> getGyaradosCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if (pCard.getCardType() == CardType.STAGE1POKEMON && (pCard.getName().equals("Gyarados") || pCard.getName().equals("Dark Gyarados")))
				cardList.add(c);
		}
		return cardList;
	}
}
