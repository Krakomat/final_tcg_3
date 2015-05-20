package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00155_Dragonite extends PokemonCardScript {

	public Script_00155_Dragonite(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Slam", att1Cost);

		this.addPokemonPower("Step In");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (gameModel.getGameModelParameters().isPower_Activated_00155_Dragonite().contains(this.card.getGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() != this.getCardOwner().getColor())
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;
		if (!PositionID.isBenchPosition(pCard.getCurrentPosition().getPositionID()))
			return false;
		return true;
	}

	@Override
	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();
		Color color = player.getColor();
		PositionID activePos = color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
		PositionID benchPos = this.card.getCurrentPosition().getPositionID();

		// Message clients:
		Card active = gameModel.getPosition(activePos).getTopCard();
		Card bench = gameModel.getPosition(benchPos).getTopCard();
		List<Card> cardList = new ArrayList<>();
		cardList.add(active);
		cardList.add(bench);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + active.getName() + " with " + bench.getName() + "!", cardList, "");

		// Execute swap:
		gameModel.getAttackAction().swapPokemon(benchPos, activePos);
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
		gameModel.getGameModelParameters().isPower_Activated_00155_Dragonite().add(this.card.getGameID());
		gameModel.sendGameModelToAllPlayers("");
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().isPower_Activated_00155_Dragonite().contains(this.card.getGameID())) {
			gameModel.getGameModelParameters().isPower_Activated_00155_Dragonite().remove(new Integer(this.card.getGameID()));
		}
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Slam"))
			this.slam();
	}

	private void slam() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 2 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 40, true);
	}
}
