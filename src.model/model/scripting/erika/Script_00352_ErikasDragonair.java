package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00352_ErikasDragonair extends PokemonCardScript {

	public Script_00352_ErikasDragonair(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Blizzard", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Take Away", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Blizzard"))
			this.Blizzard();
		else
			this.TakeAway();
	}

	private void Blizzard() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			List<PositionID> enemyBench = gameModel.getFullBenchPositions(enemy.getColor());
			for (PositionID benchPos : enemyBench)
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);
		} else {
			List<PositionID> ownBench = gameModel.getFullBenchPositions(player.getColor());
			for (PositionID benchPos : ownBench)
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);
		}
	}

	private void TakeAway() {
		// Scoop up position:
		Position position = gameModel.getPosition(ownActive());
		List<Card> cards = position.getCards();

		gameModel.sendCardMessageToAllPlayers(this.getCardOwner().getName() + " shuffles " + position.getTopCard().getName() + "into his deck!", position.getTopCard(), "");

		int size = cards.size();
		for (int i = 0; i < size; i++)
			gameModel.getAttackAction().moveCard(ownActive(), ownDeck(), cards.get(i).getGameID(), true);
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getPosition(enemyDeck()).shuffle();
		gameModel.sendGameModelToAllPlayers("");

		// Check if active position was scooped up - choose a new active pokemon in this case:
		if (gameModel.getPosition(ownActive()).isEmpty()) {
			if (gameModel.getFullArenaPositions(getCardOwner().getColor()).isEmpty()) {
				gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " has no active pokemon anymore!", "");
				gameModel.playerLoses(getCardOwner());
				return;
			} else {
				PositionID newActive = getCardOwner().playerChoosesPositions(gameModel.getFullBenchPositions(getCardOwner().getColor()), 1, true, "Choose a new active pokemon!")
						.get(0);
				Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
				gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
				gameModel.getAttackAction().movePokemonToPosition(newActive, ownActive());
				gameModel.sendGameModelToAllPlayers("");
			}
		}

		// Scoop up position:
		position = gameModel.getPosition(enemyActive());
		cards = position.getCards();

		gameModel.sendCardMessageToAllPlayers(this.getEnemyPlayer().getName() + " shuffles " + position.getTopCard().getName() + "into his deck!", position.getTopCard(), "");

		size = cards.size();
		for (int i = 0; i < size; i++)
			gameModel.getAttackAction().moveCard(enemyActive(), enemyDeck(), cards.get(i).getGameID(), true);
		gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getPosition(enemyDeck()).shuffle();
		gameModel.sendGameModelToAllPlayers("");

		// Check if active position was scooped up - choose a new active pokemon in this case:
		if (gameModel.getPosition(enemyActive()).isEmpty()) {
			if (gameModel.getFullArenaPositions(getEnemyPlayer().getColor()).isEmpty()) {
				gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " has no active pokemon anymore!", "");
				gameModel.playerLoses(getEnemyPlayer());
				return;
			} else {
				PositionID newActive = getEnemyPlayer()
						.playerChoosesPositions(gameModel.getFullBenchPositions(getEnemyPlayer().getColor()), 1, true, "Choose a new active pokemon!").get(0);
				Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
				gameModel.sendCardMessageToAllPlayers(getEnemyPlayer().getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
				gameModel.getAttackAction().movePokemonToPosition(newActive, enemyActive());
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}
}
