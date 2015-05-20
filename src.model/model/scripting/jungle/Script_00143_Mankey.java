package model.scripting.jungle;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Preconditions;

import network.client.Player;
import model.database.Card;
import model.database.Database;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00143_Mankey extends PokemonCardScript {

	public Script_00143_Mankey(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Scratch", att1Cost);
		this.addPokemonPower("Peek");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (gameModel.getGameModelParameters().isPower_Activated_00143_Mankey().contains(this.card.getGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() != this.getCardOwner().getColor())
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;

		if (gameModel.getPosition(PositionID.BLUE_DECK).isEmpty() && gameModel.getPosition(PositionID.RED_DECK).isEmpty()
				&& gameModel.getPosition(getEnemyHand()).isEmpty() && gameModel.getPosition(PositionID.BLUE_PRICE_1).isEmpty()
				&& gameModel.getPosition(PositionID.BLUE_PRICE_2).isEmpty() && gameModel.getPosition(PositionID.BLUE_PRICE_3).isEmpty()
				&& gameModel.getPosition(PositionID.BLUE_PRICE_4).isEmpty() && gameModel.getPosition(PositionID.BLUE_PRICE_5).isEmpty()
				&& gameModel.getPosition(PositionID.BLUE_PRICE_6).isEmpty() && gameModel.getPosition(PositionID.RED_PRICE_1).isEmpty()
				&& gameModel.getPosition(PositionID.RED_PRICE_2).isEmpty() && gameModel.getPosition(PositionID.RED_PRICE_3).isEmpty()
				&& gameModel.getPosition(PositionID.RED_PRICE_4).isEmpty() && gameModel.getPosition(PositionID.RED_PRICE_5).isEmpty()
				&& gameModel.getPosition(PositionID.RED_PRICE_6).isEmpty())
			return false;

		return true;
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();
		List<String> choiceList = new ArrayList<>();
		List<Card> choiceCards = new ArrayList<>();

		if (!gameModel.getPosition(PositionID.BLUE_DECK).isEmpty())
			choiceList.add(PositionID.BLUE_DECK.toString());
		if (!gameModel.getPosition(PositionID.RED_DECK).isEmpty())
			choiceList.add(PositionID.RED_DECK.toString());
		if (!gameModel.getPosition(getEnemyHand()).isEmpty())
			choiceList.add(getEnemyHand().toString());
		if (!gameModel.getPosition(PositionID.BLUE_PRICE_1).isEmpty())
			choiceList.add(PositionID.BLUE_PRICE_1.toString());
		if (!gameModel.getPosition(PositionID.BLUE_PRICE_2).isEmpty())
			choiceList.add(PositionID.BLUE_PRICE_2.toString());
		if (!gameModel.getPosition(PositionID.BLUE_PRICE_3).isEmpty())
			choiceList.add(PositionID.BLUE_PRICE_3.toString());
		if (!gameModel.getPosition(PositionID.BLUE_PRICE_4).isEmpty())
			choiceList.add(PositionID.BLUE_PRICE_4.toString());
		if (!gameModel.getPosition(PositionID.BLUE_PRICE_5).isEmpty())
			choiceList.add(PositionID.BLUE_PRICE_5.toString());
		if (!gameModel.getPosition(PositionID.BLUE_PRICE_6).isEmpty())
			choiceList.add(PositionID.BLUE_PRICE_6.toString());
		if (!gameModel.getPosition(PositionID.RED_PRICE_1).isEmpty())
			choiceList.add(PositionID.RED_PRICE_1.toString());
		if (!gameModel.getPosition(PositionID.RED_PRICE_2).isEmpty())
			choiceList.add(PositionID.RED_PRICE_2.toString());
		if (!gameModel.getPosition(PositionID.RED_PRICE_3).isEmpty())
			choiceList.add(PositionID.RED_PRICE_3.toString());
		if (!gameModel.getPosition(PositionID.RED_PRICE_4).isEmpty())
			choiceList.add(PositionID.RED_PRICE_4.toString());
		if (!gameModel.getPosition(PositionID.RED_PRICE_5).isEmpty())
			choiceList.add(PositionID.RED_PRICE_5.toString());
		if (!gameModel.getPosition(PositionID.RED_PRICE_6).isEmpty())
			choiceList.add(PositionID.RED_PRICE_6.toString());

		for (int i = 0; i < choiceList.size(); i++)
			choiceCards.add(Database.createCard("00000"));

		Preconditions.checkArgument(!choiceList.isEmpty());
		PositionID chosenPosition = PositionID.valueOf(player.playerChoosesAttacks(choiceCards, choiceList, 1, true, "Choose a position to look at:").get(0));
		List<Card> revealedCard = new ArrayList<>();

		if (chosenPosition != PositionID.BLUE_HAND && chosenPosition != PositionID.RED_HAND)
			revealedCard.add(gameModel.getPosition(chosenPosition).getTopCard());
		else {
			Random r = new SecureRandom();
			int index = r.nextInt(gameModel.getPosition(chosenPosition).size());
			revealedCard.add(gameModel.getPosition(chosenPosition).getCardAtIndex(index));
		}
		player.playerChoosesCards(revealedCard, 1, false, "Revealed card on " + chosenPosition);
		gameModel.getGameModelParameters().isPower_Activated_00143_Mankey().add(this.card.getGameID());
		gameModel.sendGameModelToAllPlayers("");
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().isPower_Activated_00143_Mankey().contains(this.card.getGameID())) {
			gameModel.getGameModelParameters().isPower_Activated_00143_Mankey().remove(new Integer(this.card.getGameID()));
		}
	}

	public PositionID getEnemyHand() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.RED_HAND;
		else
			return PositionID.BLUE_HAND;
	}
}
