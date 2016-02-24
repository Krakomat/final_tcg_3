package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00427_SabrinasGastly extends PokemonCardScript {

	public Script_00427_SabrinasGastly(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Lick", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Fade Out", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Lick"))
			this.Lick();
		else
			this.FadeOut();
	}

	private void Lick() {
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

	private void FadeOut() {
		PositionID posID = this.getCurrentPositionID();
		// Scoop up position:
		Position position = gameModel.getPosition(posID);
		List<Card> cards = position.getCards();

		gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " chooses " + position.getTopCard().getName() + "!", position.getTopCard(), "");
		PositionID hand = ownHand();

		int size = cards.size();
		for (int i = 0; i < size; i++) {
			Card c = cards.get(i);
			if (c == this.card || c instanceof EnergyCard)
				gameModel.getAttackAction().moveCard(posID, hand, c.getGameID(), true);
			else
				gameModel.getAttackAction().moveCard(posID, ownDiscardPile(), c.getGameID(), true);
		}
		gameModel.sendGameModelToAllPlayers("");

		if (gameModel.getFullArenaPositions(getCardOwner().getColor()).isEmpty()) {
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " has no active pokemon anymore!", "");
			gameModel.playerLoses(getCardOwner());
		} else if (gameModel.getPosition(this.ownActive()).isEmpty()) {
			// Choose a new active pokemon in this case:
			PositionID newActive = getCardOwner().playerChoosesPositions(gameModel.getFullArenaPositions(getCardOwner().getColor()), 1, true, "Choose a new active pokemon!")
					.get(0);
			Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
			gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
			gameModel.getAttackAction().movePokemonToPosition(newActive, this.ownActive());
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
