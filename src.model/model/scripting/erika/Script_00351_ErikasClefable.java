package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00351_ErikasClefable extends PokemonCardScript {

	public Script_00351_ErikasClefable(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Fairy Power", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Moon Impact", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Fairy Power"))
			this.FairyPower();
		else
			this.MoonImpact();
	}

	private void FairyPower() {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			boolean done = false;
			while (!done && gameModel.getFullArenaPositions(getCardOwner().getColor()).size() > 1) {
				boolean answer = getCardOwner().playerDecidesYesOrNo("Do you want to move another Pokemon to your hand?");
				if (answer) {
					// Scoop up position:
					Position position = gameModel.getPosition(getCardOwner()
							.playerChoosesPositions(gameModel.getFullArenaPositions(getCardOwner().getColor()), 1, true, "Choose a Pokemon to move to your hand!").get(0));
					List<Card> cards = position.getCards();

					gameModel.sendCardMessageToAllPlayers(this.getCardOwner().getName() + " moves " + position.getTopCard().getName() + "to his hand!", position.getTopCard(), "");

					int size = cards.size();
					for (int i = 0; i < size; i++)
						gameModel.getAttackAction().moveCard(position.getPositionID(), ownHand(), cards.get(0).getGameID(), true);
					gameModel.sendGameModelToAllPlayers("");
				} else
					done = true;
			}

			// Check if active position was scooped up - choose a new active pokemon in this case:
			if (gameModel.getPosition(ownActive()).isEmpty()) {
				PositionID newActive = getCardOwner().playerChoosesPositions(gameModel.getFullBenchPositions(getCardOwner().getColor()), 1, true, "Choose a new active pokemon!")
						.get(0);
				Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
				gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
				gameModel.getAttackAction().movePokemonToPosition(newActive, ownActive());
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}

	private void MoonImpact() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}
}
