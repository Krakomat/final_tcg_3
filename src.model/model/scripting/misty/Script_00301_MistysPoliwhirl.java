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

public class Script_00301_MistysPoliwhirl extends PokemonCardScript {

	public Script_00301_MistysPoliwhirl(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Rapids", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Water Punch", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Rapids"))
			this.Rapids();
		else
			this.WaterPunch();
	}

	private void Rapids() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Position defendingPosition = gameModel.getPosition(defender);
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Check for energy at enemy position:
		if (defendingPosition.getEnergyCards().size() > 0 && !((PokemonCard) defendingPosition.getTopCard()).hasCondition(PokemonCondition.BROCKS_PROTECTION)) {
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				// Choose one of them and remove it:
				List<Card> cardList = new ArrayList<>();
				for (Card c : defendingPosition.getEnergyCards())
					cardList.add(c);

				Card chosenEnergyCard = player.playerChoosesCards(cardList, 1, true, "Choose one energy card to remove!").get(0);
				PositionID discardPile = enemyDiscardPile();

				gameModel.getAttackAction().moveCard(defender, discardPile, chosenEnergyCard.getGameID(), true);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}

	private void WaterPunch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 2 coins...", "");
		int numberHeads = this.countWaterEnergy();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30 + numberHeads * 10, true);
	}

	private int countWaterEnergy() {
		Position pos = gameModel.getPosition(getCurrentPositionID());
		int counter = 0;
		for (Card c : pos.getEnergyCards())
			if (c.getCardId().equals("00102"))
				counter++;
		return counter;
	}
}
