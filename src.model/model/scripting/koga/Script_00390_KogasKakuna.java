package model.scripting.koga;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00390_KogasKakuna extends PokemonCardScript {

	public Script_00390_KogasKakuna(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Toxic Secretion", att1Cost);

		this.addPokemonPower("Emerge");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (gameModel.getGameModelParameters().activeEffect("00390", cardGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		return super.pokemonPowerCanBeExecuted(powerName);
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().activeEffect("00390", cardGameID())) {
			gameModel.getGameModelParameters().deactivateEffect("00390", cardGameID());
		}
	}

	@Override
	public void executePokemonPower(String powerName) {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			List<Card> cards = this.getKogasBeedrillCardsFromDeck();
			if (cards.isEmpty())
				this.gameModel.sendTextMessageToAllPlayers("Deck does not contain correct evolution cards!", "");
			else {
				Card c = this.getCardOwner().playerChoosesCards(cards, 1, true, "Choose a card for evoultion!").get(0);
				gameModel.getAttackAction().evolvePokemon(ownActive(), c.getGameID());
			}
			gameModel.getAttackAction().shufflePosition(ownDeck());
			gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		}
		gameModel.getGameModelParameters().activateEffect("00390", cardGameID());
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<Card> getKogasBeedrillCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if (pCard.getName().equals("Koga's Beedrill"))
				cardList.add(c);
		}
		return cardList;
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is poisoned!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.TOXIC);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
