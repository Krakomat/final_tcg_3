package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00403_SabrinasGengar extends PokemonCardScript {

	public Script_00403_SabrinasGengar(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Pain Amplifier", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Call of the Night", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Pain Amplifier"))
			this.PainAmplifier();
		else
			this.CalloftheNight();
	}

	private void PainAmplifier() {
		List<PositionID> damagedPokemon = getDamagedCardsFromEnemyField();
		for (PositionID posID : damagedPokemon) {
			PokemonCard pokemon = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			pokemon.setDamageMarks(pokemon.getDamageMarks() + 10);

			if (pokemon.getDamageMarks() >= pokemon.getHitpoints() && !pokemon.hasCondition(PokemonCondition.KNOCKOUT)) {
				pokemon.setDamageMarks(pokemon.getHitpoints());
				gameModel.getAttackAction().inflictConditionToPosition(posID, PokemonCondition.KNOCKOUT);
			}
		}
		gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + "'s Pokemon got damage marks!", "");
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<PositionID> getDamagedCardsFromEnemyField() {
		List<PositionID> posList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(getEnemyPlayer().getColor())) {
			PokemonCard pCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (pCard.getDamageMarks() > 0)
				posList.add(posID);
		}
		return posList;
	}

	private void CalloftheNight() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		PokemonCard pokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		if (!pokemon.hasCondition(PokemonCondition.KNOCKOUT)) {
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS && gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				Player player = this.getEnemyPlayer();
				PositionID targetPosition = this.enemyActive();

				// Scoop up position:
				Position position = gameModel.getPosition(targetPosition);
				List<Card> cards = position.getCards();

				gameModel.sendCardMessageToAllPlayers(this.getEnemyPlayer().getName() + " shuffles " + position.getTopCard().getName() + "into his deck!", position.getTopCard(),
						"");
				PositionID playerDeck = enemyDeck();

				int size = cards.size();
				for (int i = 0; i < size; i++)
					gameModel.getAttackAction().moveCard(targetPosition, playerDeck, cards.get(i).getGameID(), true);
				gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his deck!", Sounds.SHUFFLE);
				gameModel.getPosition(playerDeck).shuffle();
				gameModel.sendGameModelToAllPlayers("");
				
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
			}
		}
	}
}
