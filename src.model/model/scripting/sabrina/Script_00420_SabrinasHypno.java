package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00420_SabrinasHypno extends PokemonCardScript {

	public Script_00420_SabrinasHypno(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Invigorate", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Pendulum Curse", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Invigorate"))
			this.Invigorate();
		else
			this.PendulumCurse();
	}

	private void Invigorate() {
		List<Card> cards = gameModel.getFullBenchPositions(Color.BLUE).size() == 5 ? new ArrayList<>() : gameModel.getPosition(PositionID.BLUE_DISCARDPILE).getPokemonCards();
		List<Card> cards2 = gameModel.getFullBenchPositions(Color.RED).size() == 5 ? new ArrayList<>() : gameModel.getPosition(PositionID.RED_DISCARDPILE).getPokemonCards();
		for (Card c : cards2)
			cards.add(c);

		if (cards.size() > 0) {
			Card c = getCardOwner().playerChoosesCards(cards, 1, true, "Choose a card to revive!").get(0);
			c = gameModel.getCard(c.getGameID());
			if ((c.getCurrentPosition().getPositionID() == PositionID.BLUE_DISCARDPILE && getCardOwner().getColor() == Color.BLUE)
					|| (c.getCurrentPosition().getPositionID() == PositionID.RED_DISCARDPILE && getCardOwner().getColor() == Color.RED)) {
				gameModel.getAttackAction().putBasicPokemonOnBench(getCardOwner(), (PokemonCard) c);
			} else {
				gameModel.getAttackAction().putBasicPokemonOnBench(getEnemyPlayer(), (PokemonCard) c);
			}
			PokemonCard pC = (PokemonCard) c;
			int marks = pC.getHitpoints() / 2;
			if (marks % 10 == 5)
				marks = marks - 5;
			pC.setDamageMarks(marks);
		} else
			gameModel.sendTextMessageToAllPlayers("There are no basic Pokemon in any players discard pile or the resp. benches are full!", "");
	}

	private void PendulumCurse() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int coinNumber = ((PokemonCard) gameModel.getPosition(defender).getTopCard()).getDamageMarks() / 10;
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips " + coinNumber + " coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(coinNumber);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 20, true);
	}
}
