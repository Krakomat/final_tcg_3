package ai.standart;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import network.client.Player;
import network.server.PokemonGameManager;
import common.utilities.Triple;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import model.interfaces.Position;
import ai.interfaces.Bot;
import ai.util.AIUtilities;

public class StandardBot implements Bot {
	private AIUtilities aiUtilities;
	private LocalPokemonGameModel gameModel;

	public StandardBot() {
		this.aiUtilities = new AIUtilities();
		this.gameModel = null;
	}

	@Override
	public void updateGameModel(LocalPokemonGameModel gameModel) {
		this.gameModel = gameModel;
	}

	@Override
	public void startGame() {

	}

	@Override
	public void makeMove(PokemonGameManager server, Player player) {
		this.aiUtilities.sleep(4000);
		// (Position, positionIndex, Action)
		List<Triple<Position, Integer, String>> actionList = this.aiUtilities.computePlayerActions(this.gameModel, player);
		List<Triple<Position, Integer, String>> attackList = this.aiUtilities.filterActions(actionList, PlayerAction.ATTACK_1, PlayerAction.ATTACK_2,
				PlayerAction.RETREAT_POKEMON);
		this.aiUtilities.filterActions(attackList, PlayerAction.RETREAT_POKEMON);
		if (!actionList.isEmpty()) {
			Random r = new SecureRandom();
			int index = r.nextInt(actionList.size());
			int handCardIndex = actionList.get(index).getValue();
			server.playerPlaysCard(player, handCardIndex);
		} else if (!attackList.isEmpty()) {
			Random r = new SecureRandom();
			int index = r.nextInt(attackList.size());
			Triple<Position, Integer, String> attackTriple = attackList.get(index);
			if (attackTriple.getAction().equals(PlayerAction.ATTACK_1.toString()))
				server.executeAttack(player, ((PokemonCard) attackTriple.getKey().getTopCard()).getAttackNames().get(0));
			else if (attackTriple.getAction().equals(PlayerAction.ATTACK_2.toString()))
				server.executeAttack(player, ((PokemonCard) attackTriple.getKey().getTopCard()).getAttackNames().get(1));
		} else
			server.endTurn(player);
	}

	@Override
	public List<Card> choosesCards(List<Card> cards, int amount, boolean exact) {
		List<Card> chosenCards = new ArrayList<Card>();
		for (int i = 0; i < amount && i < cards.size(); i++)
			chosenCards.add(cards.get(i));
		return chosenCards;
	}

	@Override
	public List<PositionID> choosesPositions(List<PositionID> positionList, int amount, boolean exact) {
		ArrayList<PositionID> chosenPositions = new ArrayList<PositionID>();
		for (int i = 0; i < amount && i < positionList.size(); i++)
			chosenPositions.add(positionList.get(i));
		return chosenPositions;
	}

	@Override
	public List<Element> choosesElements(List<Element> elements, int amount, boolean exact) {
		return null;
	}

	@Override
	public List<String> choosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Card> paysEnergyCosts(List<Element> costs, List<Card> energyCards) {
		// TODO Auto-generated method stub
		return null;
	}
}
