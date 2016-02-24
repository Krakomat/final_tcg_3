package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00192_Tentacool extends PokemonCardScript {

	public Script_00192_Tentacool(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Acid", att1Cost);

		this.addPokemonPower("Cowardice");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;
		Player player = this.getCardOwner();

		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (this.gameModel.getFullArenaPositions(player.getColor()).size() < 2)
			return false;
		if (pCard.getPlayedInTurn() == gameModel.getTurnNumber())
			return false;

		return super.pokemonPowerCanBeExecuted(powerName);
	}

	@Override
	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();
		PositionID targetPosition = this.card.getCurrentPosition().getPositionID();

		// Scoop up position:
		Position position = gameModel.getPosition(targetPosition);
		List<Card> cards = position.getCards();
		PokemonCard basicPokemon = (PokemonCard) this.card;

		if (basicPokemon != null) {
			gameModel.sendCardMessageToAllPlayers(player.getName() + " returns " + basicPokemon.getName() + " to his hand!", basicPokemon, "");
			PositionID playerHand = null;
			PositionID playerDiscard = null;
			if (position.getColor() == Color.BLUE) {
				playerHand = PositionID.BLUE_HAND;
				playerDiscard = PositionID.BLUE_DISCARDPILE;
			} else {
				playerHand = PositionID.RED_HAND;
				playerDiscard = PositionID.RED_DISCARDPILE;
			}
			gameModel.getAttackAction().moveCard(targetPosition, playerHand, basicPokemon.getGameID(), false);
			int size = cards.size();
			for (int i = 0; i < size; i++)
				gameModel.getAttackAction().moveCard(targetPosition, playerDiscard, cards.get(i).getGameID(), true);
		} else
			System.err.println("No Basic pokemon on position in method scoopUpPosition");
		gameModel.sendGameModelToAllPlayers("");

		// Check if active position was scooped up - choose a new active pokemon in this case:
		if (targetPosition == ownActive()) {
			PositionID newActive = player.playerChoosesPositions(gameModel.getFullArenaPositions(position.getColor()), 1, true, "Choose a new active pokemon!").get(
					0);
			Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
			gameModel.sendCardMessageToAllPlayers(player.getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
			gameModel.getAttackAction().movePokemonToPosition(newActive, ownActive());
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Acid"))
			this.acid();
	}

	private void acid() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}
}
