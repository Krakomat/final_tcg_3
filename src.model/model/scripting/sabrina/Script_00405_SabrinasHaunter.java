package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00405_SabrinasHaunter extends PokemonCardScript {

	public Script_00405_SabrinasHaunter(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Night Spirits", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int coinNumber = getSabrinaCardsFromOwnField().size();
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips " + coinNumber + " coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(coinNumber);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 30, true);
	}

	private List<PositionID> getSabrinaCardsFromOwnField() {
		List<PositionID> posList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(getCardOwner().getColor())) {
			PokemonCard pCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (pCard.getName().equals("Sabrina's Gastly") || pCard.getName().equals("Sabrina's Haunter") || pCard.getName().equals("Sabrina's Gengar"))
				posList.add(posID);
		}
		return posList;
	}
}
