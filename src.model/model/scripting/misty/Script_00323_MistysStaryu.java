package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00323_MistysStaryu extends PokemonCardScript {

	public Script_00323_MistysStaryu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Star Boomerang", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(player.getName() + " returns " + this.card.getName() + " to his hand!", "");

			gameModel.getAttackAction().moveCard(ownActive(), ownHand(), this.card.getGameID(), true);

			// Discard other cards:
			Position position = gameModel.getPosition(ownActive());
			List<Card> cards = position.getCards();
			List<Card> copyList = new ArrayList<>();
			for (Card c : cards) {
				copyList.add(c);
			}
			for (Card c : copyList) {
				gameModel.getAttackAction().moveCard(ownActive(), ownHand(), c.getGameID(), true);
			}

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
}
