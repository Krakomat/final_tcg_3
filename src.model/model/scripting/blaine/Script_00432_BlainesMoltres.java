package model.scripting.blaine;

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

public class Script_00432_BlainesMoltres extends PokemonCardScript {

	public Script_00432_BlainesMoltres(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		this.addAttack("Phoenix Flame", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 90, true);

		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.TAILS) {
			Player player = this.getCardOwner();
			PositionID targetPosition = this.card.getCurrentPosition().getPositionID();

			// Scoop up position:
			Position position = gameModel.getPosition(targetPosition);
			List<Card> cards = position.getCards();

			gameModel.sendCardMessageToAllPlayers(player.getName() + " shuffles " + position.getTopCard().getName() + "into his deck!", position.getTopCard(), "");
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
}
