package ai.treebot;

import java.util.List;

/**
 * Nodes of the tree.
 * 
 * @author Michael
 *
 */
public class GameTreeNode {
	private final int value;
	private final List<GameTreeMove> moves;
	private final GameTreeNode parentNode;

	public GameTreeNode(int value, List<GameTreeMove> moves, GameTreeNode parentNode) {
		this.value = value;
		this.moves = moves;
		this.parentNode = parentNode;
	}

	public int getValue() {
		return value;
	}

	public List<GameTreeMove> getMoves() {
		return moves;
	}

	public GameTreeNode getParentNode() {
		return parentNode;
	}
}
