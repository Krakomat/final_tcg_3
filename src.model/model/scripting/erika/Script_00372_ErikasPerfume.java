package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00372_ErikasPerfume extends TrainerCardScript {

	public Script_00372_ErikasPerfume(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		playerRevealsHand(getEnemyPlayer());

		if (getBasicPokemonCardsFromHand().size() > 0) {
			List<Card> basicPokemon = getCardOwner().playerChoosesCards(getBasicPokemonCardsFromHand(), 5 - gameModel.getFullBenchPositions(getEnemyPlayer().getColor()).size(),
					false, "Put any number of basic pokemon onto your opponents bench!");

			for (Card c : basicPokemon) {
				PokemonCard pCard = (PokemonCard) c;
				PokemonCardScript script = (PokemonCardScript) pCard.getCardScript();
				script.playFromHand();
			}
		}
	}

	private List<Card> getBasicPokemonCardsFromHand() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(enemyHand()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if (pCard.getCardType() == CardType.BASICPOKEMON)
				cardList.add(c);
		}
		return cardList;
	}

	private void playerRevealsHand(Player player) {
		Player opponent = null;
		Position handPosition = null;
		if (player.getColor() == Color.BLUE) {
			opponent = gameModel.getPlayerRed();
			handPosition = gameModel.getPosition(PositionID.BLUE_HAND);
		} else {
			opponent = gameModel.getPlayerBlue();
			handPosition = gameModel.getPosition(PositionID.RED_HAND);
		}

		opponent.playerChoosesCards(handPosition.getCards(), 12, false, "Look at your opponents hand...");
	}
}
