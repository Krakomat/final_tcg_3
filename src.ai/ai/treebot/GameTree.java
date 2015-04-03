package ai.treebot;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import network.server.PokemonGameManager;
import ai.util.AIUtilities;

import com.google.common.base.Preconditions;

import model.enums.Color;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import model.interfaces.Position;
import common.utilities.Triple;

/**
 * Represents a game tree for the TreeBot, so he can simulate turns and store them.
 * 
 * @author Michael
 *
 */
public class GameTree {
	static final int TREE_DEPTH = 20;

	/**
	 * Nodes of the tree.
	 * 
	 * @author Michael
	 *
	 */
	private class GameTreeNode {
		private final int value;
		private final List<GameTreeMove> moves;
		private final GameTreeNode parentNode;

		GameTreeNode(int value, List<GameTreeMove> moves, GameTreeNode parentNode) {
			this.value = value;
			this.moves = moves;
			this.parentNode = parentNode;
		}
	}

	/**
	 * Edges of the tree. Represent one single move.
	 * 
	 * @author Michael
	 *
	 */
	class GameTreeMove {

		private GameTreeNode resultingNode;
		private Triple<Position, Integer, String> move;
		private Queue<List<PositionID>> chosenPositionQueue;
		private Queue<List<Integer>> chosenCardsQueue; // -->GameID
		private Queue<List<Element>> chosenElementQueue;
		private Queue<List<String>> chosenAttackQueue;

		GameTreeMove(GameTreeNode resultingNode, Triple<Position, Integer, String> move, Queue<List<PositionID>> chosenPositionQueue,
				Queue<List<Integer>> chosenCardsQueue, Queue<List<Element>> chosenElementQueue, Queue<List<String>> chosenAttackQueue) {
			super();
			this.resultingNode = resultingNode;
			this.move = move;
			this.chosenPositionQueue = chosenPositionQueue;
			this.chosenCardsQueue = chosenCardsQueue;
			this.chosenElementQueue = chosenElementQueue;
			this.chosenAttackQueue = chosenAttackQueue;
		}

		public Triple<Position, Integer, String> getTriple() {
			return move;
		}

		public Queue<List<PositionID>> getChosenPositionQueue() {
			return chosenPositionQueue;
		}

		public Queue<List<Integer>> getChosenCardsQueue() {
			return chosenCardsQueue;
		}

		public Queue<List<Element>> getChosenElementQueue() {
			return chosenElementQueue;
		}

		public Queue<List<String>> getChosenAttackQueue() {
			return chosenAttackQueue;
		}
	}

	private GameTreeNode rootNode;
	private GameModelEvaluator evaluator;
	private AIUtilities aiUtilities;
	private GameTreeNode maximumNode;

	/**
	 * Constructs a game tree. The given model will NOT be modified!
	 * 
	 * @param gameModel
	 * @param evaluator
	 * @param server
	 *            insert the REAL server here!
	 */
	public GameTree(LocalPokemonGameModel gameModel, GameModelEvaluator evaluator, PokemonGameManager server) {
		this.aiUtilities = new AIUtilities();
		this.evaluator = evaluator;
		this.rootNode = new GameTreeNode(evaluator.evaluateGameModel(gameModel), new ArrayList<>(), null);
		System.err.println("RootNode value: " + rootNode.value);
		this.maximumNode = this.rootNode;
		System.err.println("Starting to compute game tree of depth " + TREE_DEPTH + "...");
		long start = System.currentTimeMillis();
		this.computeGameTree(rootNode, gameModel, server, 0);
		System.err.println("Finished computations in " + (System.currentTimeMillis() - start) + " ms");
	}

	/**
	 * Computes a GameTree with the given node as a root. This node is not allowed to have children before computations.
	 * 
	 * @param rootNode
	 */
	private void computeGameTree(GameTreeNode rootNode, LocalPokemonGameModel gameModel, PokemonGameManager server, int depth) {
		System.err.println("Calling computeGameTree for depth " + depth);
		Preconditions.checkArgument(rootNode.moves.isEmpty(), "Error: root node has moves!");
		Color playerColor = gameModel.getPlayerOnTurn().getColor();
		// Get all possible moves:
		List<Triple<Position, Integer, String>> moves = this.aiUtilities.computePlayerActions(gameModel, new PlayerSimulator(playerColor), server);
		aiUtilities.filterActions(moves, PlayerAction.POKEMON_POWER); // TODO
		List<LocalPokemonGameModel> childModels = new ArrayList<>();

		// Simulate all moves and store them in the tree:
		for (Triple<Position, Integer, String> move : moves) {
			PlayerSimulator player = (PlayerSimulator) gameModel.getPlayerOnTurn();
			// Flush queues:
			player.flushQueues();
			TurnSimulator simulator = new TurnSimulator(gameModel.copy());
			System.err.println("Simulating " + move);
			aiUtilities.executeMove(move, simulator, player);
			LocalPokemonGameModel resultingGameModel = simulator.getGameModel();
			childModels.add(resultingGameModel);

			int modelValue = this.evaluator.evaluateGameModel(resultingGameModel);
			System.err.println("Computed value " + modelValue);
			// Create new Node:
			GameTreeNode resultingChildNode = new GameTreeNode(modelValue, new ArrayList<>(), rootNode);
			if (modelValue > this.maximumNode.value)
				this.maximumNode = resultingChildNode;
			// Create Move object:
			GameTreeMove moveEdge = new GameTreeMove(resultingChildNode, move, player.getChosenPositionQueue(), player.getChosenCardsQueue(),
					player.getChosenElementQueue(), player.getChosenAttackQueue());

			// Add subtree to root node:
			rootNode.moves.add(moveEdge);
		}

		if (depth < TREE_DEPTH) {
			// For each child node call computeGameTree recursively:
			for (int i = 0; i < rootNode.moves.size(); i++) {
				GameTreeMove move = rootNode.moves.get(i);
				PlayerAction action = PlayerAction.valueOf(move.move.getAction());
				if (!action.equals(PlayerAction.ATTACK_1) && !action.equals(PlayerAction.ATTACK_2)) {
					GameTreeNode childNode = move.resultingNode;
					this.computeGameTree(childNode, childModels.get(i), server, depth + 1);
				}
			}
		}
	}

	/**
	 * Returns the next move that should be made by the bot. If the bot should end his turn, then null is returned here!
	 * 
	 * @return
	 */
	public GameTreeMove computeMove() {
		Preconditions.checkArgument(this.maximumNode != null);
		if (this.maximumNode.parentNode == null)
			return null; // maxNode = root
		GameTreeMove move = null;
		GameTreeNode node = this.maximumNode;
		while (node.parentNode != null) {
			GameTreeNode parent = node.parentNode;
			// Search for edge:
			for (GameTreeMove m : parent.moves)
				if (m.resultingNode == node)
					move = m;
			node = node.parentNode;
		}
		return move;
	}
}
