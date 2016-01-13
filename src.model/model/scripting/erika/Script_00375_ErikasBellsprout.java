package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00375_ErikasBellsprout extends PokemonCardScript {

	public Script_00375_ErikasBellsprout(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Stretch Vine", att1Cost);

		this.addPokemonPower("Soak Up!");
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Stretch Vine") && gameModel.getFullBenchPositions(getEnemyPlayer().getColor()).isEmpty())
			return false;
		return super.attackCanBeExecuted(attackName);
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (gameModel.getGameModelParameters().activeEffect("00375", cardGameID()))
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
		if (gameModel.getGameModelParameters().activeEffect("00375", cardGameID())) {
			gameModel.getGameModelParameters().deactivateEffect("00375", cardGameID());
		}
	}

	@Override
	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();
		gameModel.getGameModelParameters().activateEffect("00375", cardGameID());

		if (getPositionsToMoveEnergyFrom().isEmpty())
			gameModel.sendTextMessageToAllPlayers("There are no valid Pokemon to move grass energy from!", "");
		else {
			boolean answer = player.playerDecidesYesOrNo("Move an energy card to Erika's Bellsprout?");
			if (answer) {
				int i = 0;
				do {
					PositionID posID = player.playerChoosesPositions(getPositionsToMoveEnergyFrom(), 1, true, "Choose a Pokemon to transfer grass energy from!").get(0);
					List<Card> cardList = new ArrayList<>();
					cardList.add(card);

					// Move energy:
					Position position = gameModel.getPosition(posID);
					PokemonCard pokemon = (PokemonCard) position.getTopCard();
					cardList.add(pokemon);
					this.gameModel.sendCardMessageToAllPlayers(player.getName() + " moves a grass energy from " + pokemon.getName() + " to " + this.card.getName() + "!", cardList,
							"");

					Card card = null;
					for (Card c : position.getEnergyCards())
						if (c.getCardId().equals("00099"))
							card = c;
					if (card != null) {
						gameModel.getAttackAction().moveCard(posID, getCurrentPositionID(), card.getGameID(), false);

						Animation animation = new CardMoveAnimation(posID, getCurrentPositionID(), card.getCardId(), Sounds.EQUIP);
						gameModel.sendAnimationToAllPlayers(animation);
					}
					i++;
					answer = player.playerDecidesYesOrNo("Move another energy card to Erika's Bellsprout?");
				} while (i <= 1 && answer && !getPositionsToMoveEnergyFrom().isEmpty());
			}
		}
	}

	private List<PositionID> getPositionsToMoveEnergyFrom() {
		List<PositionID> erg = new ArrayList<>();
		for (PositionID arenaPos : gameModel.getFullArenaPositions(this.getCardOwner().getColor())) {
			if (arenaPos != this.getCurrentPositionID() && positionContainsGrass(gameModel.getPosition(arenaPos)))
				erg.add(arenaPos);
		}
		return erg;
	}

	private boolean positionContainsGrass(Position pos) {
		for (Card c : pos.getBasicEnergyCards())
			if (c.getCardId().equals("00099"))
				return true;
		return false;
	}

	@Override
	public void executeAttack(String attackName) {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {
			PositionID benchDefender = player.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true, "Choose a pokemon that receives the damage!")
					.get(0);
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchDefender, 10, false);
		}
	}
}
