package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00224_DarkKadabra extends PokemonCardScript {

	public Script_00224_DarkKadabra(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Mind Shock", att1Cost);

		this.addPokemonPower("Matter Exchange");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (gameModel.getGameModelParameters().getPower_Activated_00224_DarkKadabra().contains(this.card.getGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		// Dark Kadabra: Hand and Deck have to contain at least one card
		if (gameModel.getPosition(ownHand()).isEmpty() || gameModel.getPosition(ownDeck()).isEmpty())
			return false;
		return super.pokemonPowerCanBeExecuted(powerName);
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().getPower_Activated_00224_DarkKadabra().contains(this.card.getGameID())) {
			gameModel.getGameModelParameters().getPower_Activated_00224_DarkKadabra().remove(new Integer(this.card.getGameID()));
		}
	}

	@Override
	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();
		List<Card> cardList = gameModel.getPosition(ownHand()).getCards();
		Card discardCard = player.playerChoosesCards(cardList, 1, true, "Choose a card to discard!").get(0);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " discards " + discardCard.getName(), discardCard, "");
		gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), discardCard.getGameID(), true);
		gameModel.getGameModelParameters().getPower_Activated_00224_DarkKadabra().add(this.card.getGameID());
		gameModel.sendGameModelToAllPlayers("");

		gameModel.getAttackAction().playerDrawsCards(1, player);
		gameModel.sendGameModelToAllPlayers("");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, false);
	}
}
