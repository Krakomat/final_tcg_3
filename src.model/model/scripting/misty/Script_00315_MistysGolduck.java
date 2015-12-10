package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00315_MistysGolduck extends PokemonCardScript {

	public Script_00315_MistysGolduck(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Electro Beam", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Super Removal", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Electro Beam"))
			this.ElectroBeam();
		else
			this.SuperRemoval();
	}

	private void ElectroBeam() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);

		// Discard all energy cards:
		if (gameModel.getAttackAction().flipACoin() == Coin.TAILS)
			gameModel.getAttackAction().removeAllEnergyFromPosition(attacker);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void SuperRemoval() {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			Player player = getCardOwner();
			for (PositionID posID : gameModel.getFullArenaPositions(getEnemyPlayer().getColor())) {
				Position pos = gameModel.getPosition(posID);
				if (pos.getEnergyCards().size() > 0 && !((PokemonCard) pos.getTopCard()).hasCondition(PokemonCondition.BROCKS_PROTECTION)) {
					Card c = player.playerChoosesCards(gameModel.getPosition(posID).getEnergyCards(), 1, true, "Choose one energy card to remove!").get(0);
					gameModel.getAttackAction().moveCard(posID, enemyDiscardPile(), c.getGameID(), true);
					gameModel.sendGameModelToAllPlayers("");
				}
			}
		} else
			gameModel.sendTextMessageToAllPlayers("Super Removal does nothing!", "");
	}
}
