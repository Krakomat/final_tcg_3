package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00203_DarkCharizard extends PokemonCardScript {

	public Script_00203_DarkCharizard(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Nail Flick", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		this.addAttack("Continuous Fireball", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Nail Flick"))
			this.NailFlick();
		else
			this.ContinuousFireball();
	}

	private void NailFlick() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void ContinuousFireball() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		List<Card> realFireEnergy = realFireEnergy();
		int numberOfCoins = realFireEnergy.size();
		gameModel.sendTextMessageToAllPlayers(this.getEnemyPlayer().getName() + " flips " + numberOfCoins + " coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(numberOfCoins);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 50, true);

		if (numberHeads > 0) {
			// Pay energy:
			gameModel.sendTextMessageToAllPlayers(this.card.getName() + " discards " + numberHeads + " fire energy!", "");
			for (int i = 0; i < numberHeads; i++) {
				gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), realFireEnergy.get(i).getGameID());
			}
		}
	}

	private List<Card> realFireEnergy() {
		List<Card> erg = new ArrayList<>();
		List<Card> cardList = this.card.getCurrentPosition().getEnergyCards();
		for (Card c : cardList)
			if (c.getCardId().equals("00098"))
				erg.add(c);
		return erg;
	}
}
