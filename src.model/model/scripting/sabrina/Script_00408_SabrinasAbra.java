package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00408_SabrinasAbra extends PokemonCardScript {

	public Script_00408_SabrinasAbra(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Energy Loop", att1Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Energy Loop")) {
			if (psychicEnergy().isEmpty())
				return false;
		}
		return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		Card c = getCardOwner().playerChoosesCards(psychicEnergy(), 1, true, "Choose a card to return to your hand from " + this.card.getName() + "!").get(0);
		gameModel.getAttackAction().moveCard(attacker, ownHand(), c.getGameID(), true);

		gameModel.sendCardMessageToAllPlayers(this.card.getName() + " gives an Energy Card to " + getCardOwner().getName() + "'s hand!", c, "");
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<Card> psychicEnergy() {
		List<Card> posList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownActive()).getCards()) {
			if (c.getCardId().equals("00101"))
				posList.add(c);
		}
		return posList;
	}
}
