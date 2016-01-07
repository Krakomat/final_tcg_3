package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00377_ErikasClefairy extends PokemonCardScript {

	public Script_00377_ErikasClefairy(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Lunar Power", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Moon Kick", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Lunar Power"))
			this.LunarPower();
		else
			this.MoonKick();
	}

	private void LunarPower() {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			List<Card> cards = this.getEvolutionCardsFromDeck();
			if (cards.isEmpty())
				this.gameModel.sendTextMessageToAllPlayers("Deck does not contain correct evolution cards!", "");
			else {
				Card c = this.getCardOwner().playerChoosesCards(cards, 1, true, "Choose a card for evoultion!").get(0);
				List<PositionID> posList = getChoosablePositions((PokemonCard) c);
				PositionID posID = null;
				if (posList.size() > 1)
					posID = this.getCardOwner().playerChoosesPositions(getChoosablePositions((PokemonCard) c), 1, true, "Choose a pokemon to evolve!").get(0);
				else
					posID = posList.get(0);
				gameModel.getAttackAction().evolvePokemon(posID, c.getGameID());
			}
			gameModel.getAttackAction().shufflePosition(ownDeck());
			gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
			gameModel.sendGameModelToAllPlayers("");
		} else
			gameModel.sendTextMessageToAllPlayers("Lunar Power missed!", "");
	}

	private List<Card> getEvolutionCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if ((pCard.getCardType() == CardType.STAGE1POKEMON || pCard.getCardType() == CardType.STAGE2POKEMON) && this.getChoosablePositions(pCard).size() > 0)
				cardList.add(c);
		}
		return cardList;
	}

	private List<PositionID> getChoosablePositions(PokemonCard c) {
		List<PositionID> erg = new ArrayList<>();
		for (PositionID benchPos : gameModel.getFullBenchPositions(this.getCardOwner().getColor())) {
			Card benchPokemon = gameModel.getPosition(benchPos).getTopCard();
			if (c.getEvolvesFrom().equals(benchPokemon.getName()))
				erg.add(benchPos);
		}
		return erg;
	}

	private void MoonKick() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
