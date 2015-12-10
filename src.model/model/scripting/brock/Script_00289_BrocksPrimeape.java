package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00289_BrocksPrimeape extends PokemonCardScript {

	public Script_00289_BrocksPrimeape(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Mega Trash", att1Cost);

		this.addPokemonPower("Scram");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 20, true);

		if (gameModel.getPosition(PositionID.STADIUM).size() > 0) {
			Card stadium = gameModel.getPosition(PositionID.STADIUM).getTopCard();
			gameModel.getAttackAction().discardCardToDiscardPile(PositionID.STADIUM, stadium.getGameID(), false);
			gameModel.getPosition(PositionID.STADIUM).setColor(Color.WHITE);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	public void pokemonIsDamaged(int turnNumber, int damage, PositionID source) {
		if (scramWillBeExecuted()) {
			Player player = this.getCardOwner();
			gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Scram!", this.card, "");
			gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles " + this.card.getName() + " into his deck!", "");

			// Scoop up position:
			PositionID targetPosition = this.getCurrentPositionID();
			Position position = gameModel.getPosition(targetPosition);
			List<Card> cards = position.getCards();

			PositionID playerDeck = ownDeck();

			int size = cards.size();
			for (int i = 0; i < size; i++)
				gameModel.getAttackAction().moveCard(targetPosition, playerDeck, cards.get(0).getGameID(), true);
			gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his deck!", Sounds.SHUFFLE);
			gameModel.getPosition(playerDeck).shuffle();
			gameModel.sendGameModelToAllPlayers("");

			if (gameModel.getFullArenaPositions(player.getColor()).isEmpty()) {
				gameModel.sendTextMessageToAllPlayers(player.getName() + " has no active pokemon anymore!", "");
				gameModel.playerLoses(player);
			} else {
				// Choose a new active pokemon in this case:
				PositionID newActive = player.playerChoosesPositions(gameModel.getFullArenaPositions(player.getColor()), 1, true, "Choose a new active pokemon!").get(0);
				Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
				gameModel.sendCardMessageToAllPlayers(player.getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
				gameModel.getAttackAction().movePokemonToPosition(newActive, ownActive());
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}

	private boolean scramWillBeExecuted() {
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;
		if (gameModel.getPlayerOnTurn().getColor() == this.getCardOwner().getColor())
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		if (((PokemonCard) this.card).getHitpoints() - ((PokemonCard) this.card).getDamageMarks() != 10)
			return false;
		return true;
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}
}
