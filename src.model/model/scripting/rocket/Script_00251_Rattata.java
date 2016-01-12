package model.scripting.rocket;

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
import model.scripting.abstracts.PokemonCardScript;

public class Script_00251_Rattata extends PokemonCardScript {

	public Script_00251_Rattata(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		this.addPokemonPower("Trickery");
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Quick Attack", att1Cost);
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;
		Player enemy = this.getEnemyPlayer();

		if (gameModel.getGameModelParameters().activeEffect("00251", cardGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (this.gameModel.getFullArenaPositions(enemy.getColor()).size() < 2)
			return false;
		if (this.gameModel.getPosition(ownDeck()).size() == 0 || !gameModel.getAttackCondition().playerHasPrizecardsLeft(this.getCardOwner()))
			return false;
		return super.pokemonPowerCanBeExecuted(powerName);
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().activeEffect("00251", cardGameID())) {
			gameModel.getGameModelParameters().deactivateEffect("00251", cardGameID());
		}
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 10 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	@Override
	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();
		ArrayList<PositionID> pricePositions = gameModel.getGameField().getNonEmptyPriceList(player.getColor());

		PositionID chosenPrizePosition = player.playerChoosesPositions(pricePositions, 1, true, "Choose prize card...").get(0);
		this.gameModel.sendTextMessageToAllPlayers(player.getName() + " switches prize card with his top deck card!", "");

		// Swap with top deck card:
		Card prize = gameModel.getPosition(chosenPrizePosition).removeTopCard();
		Card topDeck = gameModel.getPosition(ownDeck()).removeTopCard();
		gameModel.getAttackAction().moveCard(chosenPrizePosition, ownDeck(), prize.getGameID(), true);
		gameModel.getAttackAction().moveCard(ownDeck(), chosenPrizePosition, topDeck.getGameID(), true);

		gameModel.getGameModelParameters().activateEffect("00251", cardGameID());
		gameModel.sendGameModelToAllPlayers("");
	}
}
