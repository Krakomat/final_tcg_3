package model.database;

import model.enums.PokemonCondition;

public class DynamicPokemonCondition {
	private PokemonCondition condition;
	private int remainingTurns;

	public DynamicPokemonCondition(PokemonCondition condition, int remTurns) {
		this.condition = condition;
		this.remainingTurns = remTurns;
	}

	/**
	 * For serialization only.
	 */
	public DynamicPokemonCondition() {

	}

	public PokemonCondition getCondition() {
		return this.condition;
	}

	public void setCondition(PokemonCondition condition) {
		this.condition = condition;
	}

	public int getRemainingTurns() {
		return this.remainingTurns;
	}

	public void setRemainingTurns(int remainingTurns) {
		this.remainingTurns = remainingTurns;
	}
}
