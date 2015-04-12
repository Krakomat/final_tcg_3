package model.scripting.jungle;

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

public class Script_00112_Pidgeot extends PokemonCardScript {

	public Script_00112_Pidgeot(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Wing Attack", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Hurricane", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Wing Attack"))
			this.wingAttack();
		else
			this.hurricane();
	}

	private void wingAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}

	private void hurricane() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		// Check if cards will be returned to player hand:
		PokemonCard defenderPokemon = (PokemonCard) this.gameModel.getPosition(defender).getTopCard();
		if (!defenderPokemon.hasCondition(PokemonCondition.KNOCKOUT)) {
			// Return to hand:
			Player enemy = this.getEnemyPlayer();
			this.gameModel.sendTextMessageToAllPlayers(defenderPokemon.getName() + " will be returned to " + enemy.getName() + " hand!", "");

			// Scoop up position:
			Position position = gameModel.getPosition(defender);
			List<Card> cards = position.getCards();

			PositionID playerHand = null;
			if (position.getColor() == Color.BLUE)
				playerHand = PositionID.BLUE_HAND;
			else
				playerHand = PositionID.RED_HAND;

			for (int i = 0; i < cards.size(); i++)
				gameModel.getAttackAction().moveCard(defender, playerHand, cards.get(i).getGameID(), true);
			gameModel.getAttackAction().checkAndResolveFullHand(playerHand, enemy);

			gameModel.sendGameModelToAllPlayers("");

			if (gameModel.getFullArenaPositions(position.getColor()).size() > 0) {
				// Choose a new active pokemon:
				PositionID newActive = enemy.playerChoosesPositions(gameModel.getFullArenaPositions(position.getColor()), 1, true, "Choose a new active pokemon!")
						.get(0);
				Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
				gameModel.sendCardMessageToAllPlayers(enemy.getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
				gameModel.getAttackAction().movePokemonToPosition(newActive, defender);
			} else {
				// Enemy loses the game:
				this.gameModel.sendTextMessageToAllPlayers(enemy.getName() + " has no active pokemon anymore!", "");
				gameModel.playerLoses(enemy);
			}
		}
	}
}
