package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00191_Slowpoke extends PokemonCardScript {

	public Script_00191_Slowpoke(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Spacing Out", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Scavenge", att2Cost);
	}

	@Override
	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Spacing Out") && ((PokemonCard) this.card).getDamageMarks() == 0)
			return false;
		else if (attackName.equals("Scavenge") && gameModel.getPosition(ownDiscardPile()).getTrainerCards().size() == 0)
			return false; // no trainer cards in discard pile
		else
			return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Spacing Out"))
			this.spacingOut();
		else
			this.scavenge();
	}

	private void spacingOut() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attacking = gameModel.getPosition(attacker).getTopCard();

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + attacking.getName() + " is healed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.getAttackAction().healPosition(attacker, 10);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void scavenge() {
		Player player = this.getCardOwner();

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		costs.add(Element.PSYCHIC);
		gameModel.getAttackAction().playerPaysEnergy(player, costs, card.getCurrentPosition().getPositionID());

		// Choose a trainer card:
		List<Card> trainerCards = gameModel.getPosition(ownDiscardPile()).getTrainerCards();
		Card chosenCard = player.playerChoosesCards(trainerCards, 1, true, "Choose a trainer card to recover!").get(0);
		// Message clients:
		gameModel.sendCardMessageToAllPlayers(player.getName() + " recovers " + chosenCard.getName() + " from his discard pile!", chosenCard, "");
		// Move trainer card:
		gameModel.getAttackAction().moveCard(ownDiscardPile(), ownHand(), chosenCard.getGameID(), true);
	}

	private PositionID ownDiscardPile() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DISCARDPILE;
		else
			return PositionID.RED_DISCARDPILE;
	}

	private PositionID ownHand() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_HAND;
		else
			return PositionID.RED_HAND;
	}
}
