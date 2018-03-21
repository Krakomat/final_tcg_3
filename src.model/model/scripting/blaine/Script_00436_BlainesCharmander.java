package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00436_BlainesCharmander extends PokemonCardScript {

	public Script_00436_BlainesCharmander(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		this.addAttack("Kindle", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Slash", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Kindle"))
			this.Kindle();
		else
			this.Slash();
	}

	private void Kindle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		Card chosenEnergyCard = getCardOwner()
				.playerChoosesCards(this.card.getCurrentPosition().getEnergyCards(), 1, true, "Select one energy card to discard from " + this.card.getName() + "!").get(0);
		PositionID discardPile = ownDiscardPile();
		gameModel.getAttackAction().moveCard(attacker, discardPile, chosenEnergyCard.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		Position defendingPosition = gameModel.getPosition(defender);
		// Check for energy at enemy position:
		if (defendingPosition.getEnergyCards().size() > 0 && !((PokemonCard) defendingPosition.getTopCard()).hasCondition(PokemonCondition.BROCKS_PROTECTION)) {
			// Choose one of them and remove it:
			List<Card> cardList = new ArrayList<>();
			for (Card c : defendingPosition.getEnergyCards())
				cardList.add(c);

			chosenEnergyCard = getCardOwner().playerChoosesCards(cardList, 1, true, "Choose one energy card to remove!").get(0);
			discardPile = enemyDiscardPile();

			gameModel.getAttackAction().moveCard(defender, discardPile, chosenEnergyCard.getGameID(), true);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void Slash() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
