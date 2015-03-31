package model.scripting.baseEdition;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00089_Beleber extends TrainerCardScript {

	public Script_00089_Beleber(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the players bench is not full and there is at least one basic pokemon in the players discard pile.
		if (gameModel.getFullBenchPositions(getCardOwner().getColor()).size() < 5)
			if (gameModel.getAttackCondition().positionHasBasicPokemon(ownDiscardPile()))
				return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		List<Card> basicPokemon = gameModel.getBasicPokemonOnPosition(ownDiscardPile());

		PokemonCard chosenCard = (PokemonCard) player.playerChoosesCards(basicPokemon, 1, true, "Choose a pokemon to revive!").get(0);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " revives " + chosenCard.getName(), chosenCard, "");

		// Put on bench:
		gameModel.getAttackAction().putBasicPokemonOnBench(player, chosenCard);

		// Apply damage marks(do not use inflictDamage of attackAction):
		int hp = chosenCard.getHitpoints();
		hp = hp / 2;
		if (hp % 2 == 1)
			hp = hp - 5; // round down to nearest 10
		chosenCard.setDamageMarks(hp);

		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
	}

	private PositionID ownDiscardPile() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DISCARDPILE;
		else
			return PositionID.RED_DISCARDPILE;
	}
}
