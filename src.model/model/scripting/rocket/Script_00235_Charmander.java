package model.scripting.rocket;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00235_Charmander extends PokemonCardScript {

	public Script_00235_Charmander(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		this.addAttack("Fire Tail", att1Cost);

		this.addPokemonPower("Gather Fire");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (gameModel.getGameModelParameters().getPower_Activated_00235_Charmander().contains(this.card.getGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (this.positionsWithFireEnergy().isEmpty())
			return false;
		return super.pokemonPowerCanBeExecuted(powerName);
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().getPower_Activated_00235_Charmander().contains(this.card.getGameID())) {
			gameModel.getGameModelParameters().getPower_Activated_00235_Charmander().remove(new Integer(this.card.getGameID()));
		}
	}

	@Override
	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();
		List<PositionID> posList = this.positionsWithFireEnergy();
		PositionID chosenPosition = player.playerChoosesPositions(posList, 1, true, "Choose a position to transfer a fire energy to " + this.card.getName() + "...")
				.get(0);
		this.gameModel.sendTextMessageToAllPlayers(player.getName() + " transfers a fire energy from "
				+ gameModel.getPosition(chosenPosition).getTopCard().getName() + " to " + this.card.getName() + "!", "");

		Card fireEnergy = null;
		for (Card c : gameModel.getPosition(chosenPosition).getEnergyCards()) {
			if (c.getCardId().equals("00098") && fireEnergy == null) {
				fireEnergy = c;
			}
		}

		gameModel.getAttackAction().moveCard(chosenPosition, this.card.getCurrentPosition().getPositionID(), fireEnergy.getGameID(), false);
		gameModel.getGameModelParameters().getPower_Activated_00235_Charmander().add(this.card.getGameID());

		// Execute animation:
		Animation animation = new CardMoveAnimation(chosenPosition, this.card.getCurrentPosition().getPositionID(), fireEnergy.getCardId(), Sounds.EQUIP);
		gameModel.sendAnimationToAllPlayers(animation);

		gameModel.sendGameModelToAllPlayers("");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}

	private List<PositionID> positionsWithFireEnergy() {
		List<PositionID> posList = gameModel.getFullArenaPositions(this.getCardOwner().getColor());
		posList.remove(this.card.getCurrentPosition().getPositionID());

		List<PositionID> returnList = new ArrayList<>();
		for (PositionID posID : posList) {
			for (Card c : gameModel.getPosition(posID).getEnergyCards()) {
				if (c.getCardId().equals("00098") && !returnList.contains(posID)) {
					returnList.add(posID);
				}
			}
		}
		return returnList;
	}
}
