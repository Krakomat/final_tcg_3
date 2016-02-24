package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00209_DarkMachamp extends PokemonCardScript {

	public Script_00209_DarkMachamp(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Mega Punch", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Fling", att2Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Fling")) {
			// Cannot be used if the enemy has no benched pokemon
			if (gameModel.getFullBenchPositions(getEnemyPlayer().getColor()).isEmpty())
				return false;
		}
		return super.attackCanBeExecuted(attackName);
	};

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Mega Punch"))
			this.MegaPunch();
		else
			this.Fling();
	}

	private void MegaPunch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}

	private void Fling() {
		Player player = this.getEnemyPlayer();
		PositionID targetPosition = this.enemyActive();

		// Scoop up position:
		Position position = gameModel.getPosition(targetPosition);
		List<Card> cards = position.getCards();

		gameModel.sendCardMessageToAllPlayers(this.getEnemyPlayer().getName() + " shuffles " + position.getTopCard().getName() + "into his deck!", position.getTopCard(), "");
		PositionID playerDeck = enemyDeck();

		int size = cards.size();
		for (int i = 0; i < size; i++)
			gameModel.getAttackAction().moveCard(targetPosition, playerDeck, cards.get(i).getGameID(), true);
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
