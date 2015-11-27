package model.scripting.fossil;

import model.database.Database;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00198_MysteriousFossil extends TrainerCardScript {

	public Script_00198_MysteriousFossil(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the players bench is not full
		if (gameModel.getFullBenchPositions(getCardOwner().getColor()).size() < 5)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Generate token:
		PokemonCard fossil = (PokemonCard) Database.createCard("00199");
		gameModel.registerCard(fossil);
		fossil.setPriceValueable(true); // no prices if defeated

		// Set card script:
		fossil.setCardScript(new Script_00199_FossilToken(fossil, gameModel, (TrainerCard) card));

		// Remove trainer card from hand:
		Position handPos = gameModel.getPosition(ownHand());
		boolean success = handPos.removeFromPosition(card);
		if (!success)
			System.err.println("Couldn't remove trainer card from position");
		this.card.setCurrentPosition(null);

		// Insert doll into hand:
		fossil.setCurrentPosition(handPos);
		handPos.addToPosition(fossil);

		// Set doll onto bench:
		fossil.getCardScript().playFromHand();
	}
}
