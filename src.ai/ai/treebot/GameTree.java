package ai.treebot;

import java.util.ArrayList;
import java.util.List;
import network.server.PokemonGameManager;
import ai.util.AIUtilities;

import com.google.common.base.Preconditions;

import model.enums.Color;
import model.enums.PlayerAction;
import model.game.LocalPokemonGameModel;

/**
 * Represents a game tree for the TreeBot, so he can simulate turns and store them.
 * 
 * @author Michael
 *
 */
public class GameTree {
	static final int TREE_DEPTH = 20;

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
		System.err.println("RootNode value: " + rootNode.getValue());
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
		Preconditions.checkArgument(rootNode.getMoves().isEmpty(), "Error: root node has moves!");
		Color playerColor = gameModel.getPlayerOnTurn().getColor();
		// Get all possible moves:
		List<GameTreeMove> moves = this.aiUtilities.computePlayerActions(gameModel, new PlayerSimulator(playerColor), server);
		aiUtilities.filterActions(moves, PlayerAction.POKEMON_POWER); // TODO
		List<LocalPokemonGameModel> childModels = new ArrayList<>();

		// Simulate all moves and store them in the tree:
		for (GameTreeMove move : moves) {
			PlayerSimulator player = (PlayerSimulator) gameModel.getPlayerOnTurn();
			// Flush queues:
			player.flushQueues();
			TurnSimulator simulator = new TurnSimulator(gameModel.copy());
			System.err.println("Simulating " + move);
			player.setChosenCardsQueue(move.getChosenCardsQueue());
			player.setChosenPositionQueue(move.getChosenPositionQueue());
			player.setChosenElementQueue(move.getChosenElementQueue());
			player.setChosenAttackQueue(move.getChosenAttackQueue());
			aiUtilities.executeMove(move, simulator, player);
			LocalPokemonGameModel resultingGameModel = simulator.getGameModel();
			childModels.add(resultingGameModel);

			int modelValue = this.evaluator.evaluateGameModel(resultingGameModel);
			System.err.println("Computed value " + modelValue);
			// Create new Node:
			GameTreeNode resultingChildNode = new GameTreeNode(modelValue, new ArrayList<>(), rootNode);
			if (modelValue >= this.maximumNode.getValue())
				this.maximumNode = resultingChildNode;
			// Create Move object:
			GameTreeMove moveEdge = new GameTreeMove(resultingChildNode, move.getTriple(), player.getChosenPositionQueue(), player.getChosenCardsQueue(),
					player.getChosenElementQueue(), player.getChosenAttackQueue());

			// Add subtree to root node:
			rootNode.getMoves().add(moveEdge);
		}

		if (depth < TREE_DEPTH) {
			// For each child node call computeGameTree recursively:
			for (int i = 0; i < rootNode.getMoves().size(); i++) {
				GameTreeMove move = rootNode.getMoves().get(i);
				PlayerAction action = PlayerAction.valueOf(move.getTriple().getAction());
				if (!action.equals(PlayerAction.ATTACK_1) && !action.equals(PlayerAction.ATTACK_2)) {
					GameTreeNode childNode = move.getResultingNode();
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
		if (this.maximumNode.getParentNode() == null)
			return null; // maxNode = root
		GameTreeMove move = null;
		GameTreeNode node = this.maximumNode;
		while (node.getParentNode() != null) {
			GameTreeNode parent = node.getParentNode();
			// Search for edge:
			for (GameTreeMove m : parent.getMoves())
				if (m.getResultingNode() == node)
					move = m;
			node = node.getParentNode();
		}
		return move;
	}
}
