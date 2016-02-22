package model.scripting.koga;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00400_KogasZubat extends PokemonCardScript {

	public Script_00400_KogasZubat(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Group Attack", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		int maxBench = (this.gameModel.getCurrentStadium() != null && this.gameModel.getCurrentStadium().getCardId().equals("00468")
				&& this.gameModel.getFullBenchPositions(getCardOwner().getColor()).size() == 4) ? 4 : 5;

		if (gameModel.getFullBenchPositions(getCardOwner().getColor()).size() < maxBench && getKogasZubatCardsFromDeck().size() > 0) {
			int max = Math.min(maxBench - gameModel.getFullBenchPositions(getCardOwner().getColor()).size(), getKogasZubatCardsFromDeck().size());
			List<Card> zubatList = getCardOwner().playerChoosesCards(getKogasZubatCardsFromDeck(), max, false, "Choose any number of cards from your deck!");

			if (zubatList.isEmpty()) {
				// Put basic pokemon on bench:
				for (Card c : zubatList) {
					Card zubat = gameModel.getCard(c.getGameID());
					this.gameModel.getAttackAction().putBasicPokemonOnBench(getCardOwner(), (PokemonCard) zubat);
				}

				// Shuffle deck:
				gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
				gameModel.getAttackAction().shufflePosition(ownDeck());
				this.gameModel.sendGameModelToAllPlayers("");

			}
		}

		// Damage:
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 * getKogasZubatCardsFromPlay().size(), true);
	}

	private List<Card> getKogasZubatCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if (pCard.getName().equals("Koga's Zubat"))
				cardList.add(c);
		}
		return cardList;
	}

	private List<Card> getKogasZubatCardsFromPlay() {
		List<Card> cardList = new ArrayList<>();
		for (PositionID c : gameModel.getFullArenaPositions(getCardOwner().getColor())) {
			Card pCard = gameModel.getPosition(c).getTopCard();
			if (pCard.getName().equals("Koga's Zubat"))
				cardList.add(pCard);
		}
		return cardList;
	}
}
