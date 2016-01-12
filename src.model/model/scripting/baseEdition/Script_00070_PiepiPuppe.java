package model.scripting.baseEdition;

import model.database.Database;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00070_PiepiPuppe extends TrainerCardScript {

	public Script_00070_PiepiPuppe(TrainerCard card, PokemonGame gameModel) {
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
		PokemonCard doll = (PokemonCard) Database.createCard("00103");
		gameModel.registerCard(doll);
		doll.setPriceValueable(true); // no prices if defeated

		// Set card script:
		doll.setCardScript(new Script_00103_Doll(doll, gameModel));
		// Store this card at the server:
		gameModel.getGameModelParameters().activateEffect("00070", cardGameID());

		// Remove trainer card from hand:
		Position handPos = gameModel.getPosition(ownHand());
		boolean success = handPos.removeFromPosition(card);
		if (!success)
			System.err.println("Couldn't remove trainer card from position");
		this.card.setCurrentPosition(null);

		doll.setCurrentPositionLocal(handPos);
		handPos.getCards().add(doll);
		// Set doll onto bench:
		doll.getCardScript().playFromHand();
	}
}
