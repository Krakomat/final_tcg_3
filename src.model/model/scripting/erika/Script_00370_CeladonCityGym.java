package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00370_CeladonCityGym extends TrainerCardScript {

	public Script_00370_CeladonCityGym(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	public boolean stadiumCanBeActivatedOnField(Player player) {
		// Can be activated if player has a active pokemon with energy and erika in its name:
		Position activePos = gameModel.getPosition(active(player));
		if (activePos.getTopCard().getName().contains("Erika") && activePos.getEnergyCards().size() > 0)
			return true;
		return false;
	}

	public void executeStadiumActiveEffect(Player player) {
		List<Element> costs = new ArrayList<>();
		costs.add(Element.COLORLESS);
		gameModel.getAttackAction().playerPaysEnergy(player, costs, active(player));
		gameModel.getAttackAction().cureAllConditionsOnPosition(active(player));
		Card targetPokemon = gameModel.getPosition(active(player)).getTopCard();
		gameModel.sendCardMessageToAllPlayers("All conditions on " + targetPokemon.getName() + " are cured", targetPokemon, "");
		gameModel.sendGameModelToAllPlayers("");
	}

	protected PositionID active(Player player) {
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_ACTIVEPOKEMON;
		else
			return PositionID.RED_ACTIVEPOKEMON;
	}

	@Override
	public void playFromHand() {
		if (!gameModel.getPosition(PositionID.STADIUM).isEmpty()) {
			Card stadium = gameModel.getPosition(PositionID.STADIUM).getTopCard();
			// Discard previous stadium card:
			gameModel.getAttackAction().discardCardToDiscardPile(PositionID.STADIUM, stadium.getGameID(), false);
		}
		gameModel.getPosition(PositionID.STADIUM).setColor(this.getCardOwner().getColor());
		gameModel.getAttackAction().moveCard(getCurrentPositionID(), PositionID.STADIUM, this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");
	}
}
