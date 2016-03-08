package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00329_LtSurge extends TrainerCardScript {

	public Script_00329_LtSurge(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		if (!getBasicPokemonInHand().isEmpty() && gameModel.getFullBenchPositions(getCardOwner().getColor()).size() < 5 && !(this.gameModel.getCurrentStadium() != null
				&& this.gameModel.getCurrentStadium().getCardId().equals("00468") && this.gameModel.getFullBenchPositions(getCardOwner().getColor()).size() == 4))
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		// Choose Basic pkmn from hand:
		Card c = getCardOwner().playerChoosesCards(getBasicPokemonInHand(), 1, true, "Choose a basic pokemon from your hand!").get(0);

		// Get the lowest bench position that is empty:
		PositionID benchPosition = null;
		for (int i = 5; i >= 1; i--) {
			Position pos = gameModel.getPosition(PositionID.getBenchPosition(getCardOwner().getColor(), i));
			if (pos.isEmpty())
				benchPosition = pos.getPositionID();
		}

		gameModel.getAttackAction().movePokemonToPosition(ownActive(), benchPosition);
		gameModel.getAttackAction().moveCard(ownHand(), ownActive(), c.getGameID(), true);
		c.setPlayedInTurn(gameModel.getTurnNumber());
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<Card> getBasicPokemonInHand() {
		List<Card> list = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownHand()).getPokemonCards())
			if (c.getCardType() == CardType.BASICPOKEMON)
				list.add(c);
		return list;
	}
}
