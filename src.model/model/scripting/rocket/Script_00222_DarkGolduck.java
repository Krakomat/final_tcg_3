package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00222_DarkGolduck extends PokemonCardScript {

	public Script_00222_DarkGolduck(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Third Eye", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Super Psy", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Third Eye"))
			this.ThirdEye();
		else
			this.SuperPsy();
	}

	private void ThirdEye() {
		Player player = this.getCardOwner();

		Card chosenEnergyCard = gameModel.getCard(player.playerChoosesCards(this.card.getCurrentPosition().getEnergyCards(), 1, true, "Discard one energy card!")
				.get(0).getGameID());
		gameModel.sendCardMessageToAllPlayers(player.getName() + " discards " + chosenEnergyCard.getName(), chosenEnergyCard, "");
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), chosenEnergyCard.getGameID());
		gameModel.sendGameModelToAllPlayers("");

		int deckSize = gameModel.getPosition(ownDeck()).size();
		int drawSize = deckSize < 3 ? deckSize : 3;
		gameModel.sendTextMessageToAllPlayers(player.getName() + " draws " + drawSize + " cards!", "");
		gameModel.getAttackAction().playerDrawsCards(drawSize, player);
	}

	private void SuperPsy() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);
	}
}
