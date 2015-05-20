package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00119_Vileplume extends PokemonCardScript {

	public Script_00119_Vileplume(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Petal Dance", att1Cost);

		this.addPokemonPower("Heal");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 3 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(4);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 40, true);
		gameModel.sendTextMessageToAllPlayers(this.card.getName() + " is confused!", "");
		this.gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.CONFUSED);
		this.gameModel.sendGameModelToAllPlayers("");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Can be executed when Vileplume is in play and isn't asleep, confused or paralyzed, there is some damaged pokemon on the owners side of the field and its
		// owner is on turn and finally this pokemon power was not used this turn
		PokemonCard pCard = (PokemonCard) this.card;
		Player player = this.getCardOwner();

		if (gameModel.getGameModelParameters().isPower_Activated_00119_Vileplume().contains(this.card.getGameID()))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() != this.getCardOwner().getColor())
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;

		for (PositionID posID : gameModel.getFullArenaPositions(player.getColor())) {
			Position pos = gameModel.getPosition(posID);
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			if (pokemon.getDamageMarks() > 0)
				return true;
		}
		return false;
	}

	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();

		// Flip coin to check if defending pokemon is allowed to retreat the next turn:
		gameModel.sendTextMessageToAllPlayers("If heads then " + player.getName() + " can remove one damage counter from an own pokemon!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c, "");
		if (c == Coin.HEADS) {
			List<PositionID> damagedPositions = new ArrayList<>();
			for (PositionID posID : gameModel.getFullArenaPositions(player.getColor())) {
				Position pos = gameModel.getPosition(posID);
				PokemonCard pokemon = (PokemonCard) pos.getTopCard();
				if (pokemon.getDamageMarks() > 0)
					damagedPositions.add(posID);
			}
			PositionID chosenPosition = player.playerChoosesPositions(damagedPositions, 1, true, "Choose a pokemon to heal!").get(0);
			this.gameModel.getAttackAction().healPosition(chosenPosition, 10);
		}

		gameModel.getGameModelParameters().isPower_Activated_00119_Vileplume().add(this.card.getGameID());
		gameModel.sendGameModelToAllPlayers("");
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().isPower_Activated_00119_Vileplume().contains(this.card.getGameID())) {
			gameModel.getGameModelParameters().isPower_Activated_00119_Vileplume().remove(new Integer(this.card.getGameID()));
		}
	}
}
