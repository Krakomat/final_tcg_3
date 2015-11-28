package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00234_Abra extends PokemonCardScript {

	public Script_00234_Abra(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Vanish", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Psyshock", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Vanish"))
			this.Vanish();
		else
			this.Psyshock();
	}

	private void Vanish() {
		Player player = this.getCardOwner();

		gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles " + this.card.getName() + " into his deck!", "");

		gameModel.getAttackAction().moveCard(ownActive(), ownDeck(), this.card.getGameID(), true);
		gameModel.getAttackAction().shufflePosition(ownDeck());

		// Discard other cards:
		Position position = gameModel.getPosition(ownActive());
		List<Card> cards = position.getCards();
		List<Card> copyList = new ArrayList<>();
		for (Card c : cards) {
			copyList.add(c);
		}
		for (Card c : copyList) {
			gameModel.getAttackAction().moveCard(ownActive(), ownDiscardPile(), c.getGameID(), true);
		}

		gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);

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

	private void Psyshock() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
