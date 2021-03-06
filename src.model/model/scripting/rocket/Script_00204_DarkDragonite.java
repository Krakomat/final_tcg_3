package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00204_DarkDragonite extends PokemonCardScript {

	public Script_00204_DarkDragonite(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Giant Tail", att1Cost);

		this.addPokemonPower("Summon Minions");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public void playFromHand() {
		super.playFromHand();
		Player player = this.getCardOwner();
		if (this.gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() == 0 && !this.gameModel.getGameModelParameters().activeEffect("00164")
				&& gameModel.getFullBenchPositions(player.getColor()).size() < 5 && !(this.gameModel.getCurrentStadium() != null
						&& this.gameModel.getCurrentStadium().getCardId().equals("00468") && this.gameModel.getFullBenchPositions(getCardOwner().getColor()).size() == 4)) {
			if (!getBasicPokemonFromDeck().isEmpty()) {
				int number = gameModel.getFullBenchPositions(player.getColor()).size() == 4 || (this.gameModel.getCurrentStadium() != null
						&& this.gameModel.getCurrentStadium().getCardId().equals("00468") && this.gameModel.getFullBenchPositions(getCardOwner().getColor()).size() == 3) ? 1 : 2;

				List<Card> cards = player.playerChoosesCards(getBasicPokemonFromDeck(), number, false,
						"Choose up to " + number + " pokemon card(s) from your deck to put on your bench!");

				if (!cards.isEmpty()) {
					gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Summon Minions!", card, "");
					for (Card card : cards) {
						PokemonCard realCard = (PokemonCard) gameModel.getCard(card.getGameID());
						gameModel.getAttackAction().putBasicPokemonOnBench(player, realCard);
					}
				}
			} else
				gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + "'s deck does not contain basic pokemon cards!", "");

			// Shuffle deck:
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
			gameModel.getAttackAction().shufflePosition(ownDeck());
			this.gameModel.sendGameModelToAllPlayers("");
		}
	}

	private List<Card> getBasicPokemonFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if (pCard.getCardType() == CardType.BASICPOKEMON)
				cardList.add(c);
		}
		return cardList;
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Giant Tail does nothing!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 70, true);
		else
			gameModel.sendTextMessageToAllPlayers("Giant Tail does nothing!", "");
	}
}
