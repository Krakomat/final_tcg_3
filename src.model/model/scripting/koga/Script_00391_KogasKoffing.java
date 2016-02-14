package model.scripting.koga;

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

public class Script_00391_KogasKoffing extends PokemonCardScript {

	public Script_00391_KogasKoffing(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Smokescreen", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Obscuring Gas", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Smokescreen"))
			this.Smokescreen();
		else
			this.ObscuringGas();
	}

	private void Smokescreen() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is blinded!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.BLIND);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void ObscuringGas() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			Player player = this.getCardOwner();

			gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles " + this.card.getName() + " into his deck!", "");

			gameModel.getAttackAction().moveCard(ownActive(), ownDeck(), this.card.getGameID(), true);

			// Discard other cards:
			Position position = gameModel.getPosition(ownActive());
			List<Card> cards = position.getCards();
			List<Card> copyList = new ArrayList<>();
			for (Card c : cards) {
				copyList.add(c);
			}
			for (Card c : copyList) {
				gameModel.getAttackAction().moveCard(ownActive(), ownDeck(), c.getGameID(), true);
			}
			gameModel.getAttackAction().shufflePosition(ownDeck());
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
	}
}
