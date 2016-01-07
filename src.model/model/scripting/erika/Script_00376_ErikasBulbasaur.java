package model.scripting.erika;

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

public class Script_00376_ErikasBulbasaur extends PokemonCardScript {

	public Script_00376_ErikasBulbasaur(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Sleep Seed", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Errand-Running", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Sleep Seed"))
			this.SleepSeed();
		else
			this.ErrandRunning();
	}

	private void SleepSeed() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is asleep!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void ErrandRunning() {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			List<Card> cards = this.getTrainerCardsFromDeck();
			if (cards.isEmpty())
				this.gameModel.sendTextMessageToAllPlayers("Deck does not contain Trainer cards!", "");
			else {
				Card c = this.getCardOwner().playerChoosesCards(cards, 1, true, "Choose a Trainer card!").get(0);
				gameModel.sendCardMessageToAllPlayers(this.getCardOwner().getName() + " takes " + c.getName() + " into his hand!", c, "");
				gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), c.getGameID(), true);
			}
			gameModel.getAttackAction().shufflePosition(ownDeck());
			gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
			gameModel.sendGameModelToAllPlayers("");
		} else
			gameModel.sendTextMessageToAllPlayers("Errand-Running missed!", "");
	}

	private List<Card> getTrainerCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getTrainerCards()) {
			cardList.add(c);
		}
		return cardList;
	}
}
