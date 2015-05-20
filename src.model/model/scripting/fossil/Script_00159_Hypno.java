package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00159_Hypno extends PokemonCardScript {

	public Script_00159_Hypno(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Prophecy", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Dark Mind", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Prophecy"))
			this.prophecy();
		else
			this.darkMind();
	}

	private void prophecy() {
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " rearranges the top 3 cards from his deck!", "");
		gameModel.getAttackAction().rearrangeCardsFromPosition(ownDeck(), 3);
		
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " rearranges the top 3 cards from his opponents deck!", "");
		gameModel.getAttackAction().rearrangeCardsFromPosition(enemyDeck(), 3);
	}

	private void darkMind() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {
			PositionID benchDefender = player.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true,
					"Choose a pokemon that receives the damage!").get(0);
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchDefender, 10, true);
		}
	}
	
	private PositionID ownDeck() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DECK;
		else
			return PositionID.RED_DECK;
	}

	private PositionID enemyDeck() {
		Player enemy = this.getEnemyPlayer();
		if (enemy.getColor() == Color.BLUE)
			return PositionID.BLUE_DECK;
		else
			return PositionID.RED_DECK;
	}
}
