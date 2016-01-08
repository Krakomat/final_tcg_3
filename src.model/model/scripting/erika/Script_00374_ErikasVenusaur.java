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

public class Script_00374_ErikasVenusaur extends PokemonCardScript {

	public Script_00374_ErikasVenusaur(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Growth", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Wide Solarbeam", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Growth"))
			this.Growth();
		else
			this.WideSolarbeam();
	}

	private void Growth() {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			Position hand = gameModel.getPosition(ownHand());
			List<Card> cardList = new ArrayList<>();
			for (Card c : hand.getEnergyCards())
				if (c.getCardId().equals("00099"))
					cardList.add(c);
			if (!cardList.isEmpty()) {
				List<Card> moveList = getCardOwner().playerChoosesCards(cardList, 2, false, "Choose up to two cards to attach to " + this.card.getName());
				for (Card c : moveList)
					gameModel.getAttackAction().moveCard(ownHand(), getCurrentPositionID(), c.getGameID(), false);
				gameModel.sendGameModelToAllPlayers("");
			} else {
				gameModel.sendTextMessageToAllPlayers("No Lightning energy found!", "");
			}
		} else
			gameModel.sendTextMessageToAllPlayers("Growth was not successful!", "");
	}

	private void WideSolarbeam() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		if (gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()).size() > 2) {
			List<PositionID> fullBenchPos = gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor());
			PositionID pos1 = getCardOwner().playerChoosesPositions(fullBenchPos, 1, true, "Choose 2 bench pokemon to receive damage!").get(0);
			fullBenchPos.remove(pos1);
			PositionID pos2 = getCardOwner().playerChoosesPositions(fullBenchPos, 1, true, "Choose a bench pokemon to receive damage!").get(0);
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, pos1, 20, false);
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, pos2, 20, false);

		} else {
			for (PositionID benchPos : gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()))
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 20, false);
		}
	}
}
