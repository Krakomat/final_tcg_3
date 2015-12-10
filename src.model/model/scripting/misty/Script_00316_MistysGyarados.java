package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00316_MistysGyarados extends PokemonCardScript {

	public Script_00316_MistysGyarados(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		this.addAttack("Tidal Wave", att1Cost);

		this.addPokemonPower("Rebellion");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		return false;
	};

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		if (rebellionCanBeExecuted()) {
			gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Rebellion!", card, "");
			if (gameModel.getAttackAction().flipCoinsCountHeads(2) == 0) {
				Player player = this.getCardOwner();

				gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles " + this.card.getName() + " into his deck!", "");

				gameModel.getAttackAction().moveCard(ownActive(), ownDeck(), this.card.getGameID(), true);

				// Discard other cards:
				Position position = gameModel.getPosition(ownActive());
				List<Card> cards = position.getCards();
				List<Card> copyList = new ArrayList<>();
				for (Card c : cards) {
					copyList.add(c);
				}
				for (Card c : copyList) {
					gameModel.getAttackAction().moveCard(ownActive(), ownDeck(), c.getGameID(), true);
				}
				gameModel.getAttackAction().shufflePosition(ownDeck());
				gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);

				if (gameModel.getFullArenaPositions(player.getColor()).isEmpty()) {
					gameModel.sendTextMessageToAllPlayers(player.getName() + " has no active pokemon anymore!", "");
					gameModel.playerLoses(player);
				} else {
					// Choose a new active pokemon in this case:
					PositionID newActive = player.playerChoosesPositions(gameModel.getFullArenaPositions(player.getColor()), 1, true, "Choose a new active pokemon!").get(0);
					Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
					gameModel.sendCardMessageToAllPlayers(player.getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
					gameModel.getAttackAction().movePokemonToPosition(newActive, ownActive());
					gameModel.sendGameModelToAllPlayers("");
				}
			} else
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 70, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 70, true);
	}

	private boolean rebellionCanBeExecuted() {
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;
		if (gameModel.getPlayerOnTurn().getColor() == this.getCardOwner().getColor())
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		if (!PositionID.isBenchPosition(this.getCurrentPositionID()))
			return false;
		return true;
	}
}
