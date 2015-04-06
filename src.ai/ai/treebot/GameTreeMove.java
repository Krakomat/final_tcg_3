package ai.treebot;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.Position;
import common.utilities.Triple;

/**
 * Edges of the tree. Represent one single move.
 * 
 * @author Michael
 *
 */
public class GameTreeMove {

	private GameTreeNode resultingNode;
	private Triple<Position, Integer, String> move;
	private Queue<List<PositionID>> chosenPositionQueue;
	private Queue<List<Integer>> chosenCardsQueue; // -->GameID
	private Queue<List<Element>> chosenElementQueue;
	private Queue<List<String>> chosenAttackQueue;

	public GameTreeMove(GameTreeNode resultingNode, Triple<Position, Integer, String> triple, Queue<List<PositionID>> chosenPositionQueue,
			Queue<List<Integer>> chosenCardsQueue, Queue<List<Element>> chosenElementQueue, Queue<List<String>> chosenAttackQueue) {
		this.resultingNode = resultingNode;
		this.move = triple;
		this.chosenPositionQueue = chosenPositionQueue;
		this.chosenCardsQueue = chosenCardsQueue;
		this.chosenElementQueue = chosenElementQueue;
		this.chosenAttackQueue = chosenAttackQueue;
	}

	public GameTreeMove(Triple<Position, Integer, String> triple, Queue<List<PositionID>> chosenPositionQueue, Queue<List<Integer>> chosenCardsQueue,
			Queue<List<Element>> chosenElementQueue, Queue<List<String>> chosenAttackQueue) {
		this.resultingNode = null;
		this.move = triple;
		this.chosenPositionQueue = chosenPositionQueue;
		this.chosenCardsQueue = chosenCardsQueue;
		this.chosenElementQueue = chosenElementQueue;
		this.chosenAttackQueue = chosenAttackQueue;
	}

	public GameTreeMove(Triple<Position, Integer, String> triple) {
		this.resultingNode = null;
		this.move = triple;
		this.chosenPositionQueue = new LinkedList<>();
		this.chosenCardsQueue = new LinkedList<>();
		this.chosenElementQueue = new LinkedList<>();
		this.chosenAttackQueue = new LinkedList<>();
	}

	public String toString() {
		return move.toString() + "; " + chosenCardsQueue + "; " + chosenPositionQueue + "; " + chosenElementQueue + "; " + chosenAttackQueue;
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

	public GameTreeNode getResultingNode() {
		return resultingNode;
	}

	public void setResultingNode(GameTreeNode resultingNode) {
		this.resultingNode = resultingNode;
	}
}
