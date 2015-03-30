package model.enums;

public enum PositionID {
	RED_ACTIVEPOKEMON, RED_BENCH_1, RED_BENCH_2, RED_BENCH_3, RED_BENCH_4, RED_BENCH_5, RED_HAND, RED_PRICE_1, RED_PRICE_2, RED_PRICE_3, RED_PRICE_4, RED_PRICE_5,
	RED_PRICE_6, RED_DECK, RED_DISCARDPILE, BLUE_ACTIVEPOKEMON, BLUE_BENCH_1, BLUE_BENCH_2, BLUE_BENCH_3, BLUE_BENCH_4, BLUE_BENCH_5, BLUE_HAND, BLUE_PRICE_1,
	BLUE_PRICE_2, BLUE_PRICE_3, BLUE_PRICE_4, BLUE_PRICE_5, BLUE_PRICE_6, BLUE_DECK, BLUE_DISCARDPILE;

	/**
	 * Returns true if the given position is an arena position.
	 * 
	 * @param posID
	 * @return
	 */
	public static boolean isArenaPosition(PositionID posID) {
		switch (posID) {
		case BLUE_ACTIVEPOKEMON:
			return true;
		case BLUE_BENCH_1:
			return true;
		case BLUE_BENCH_2:
			return true;
		case BLUE_BENCH_3:
			return true;
		case BLUE_BENCH_4:
			return true;
		case BLUE_BENCH_5:
			return true;
		case BLUE_DECK:
			return true;
		case RED_ACTIVEPOKEMON:
			return true;
		case RED_BENCH_1:
			return true;
		case RED_BENCH_2:
			return true;
		case RED_BENCH_3:
			return true;
		case RED_BENCH_4:
			return true;
		case RED_BENCH_5:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns the active position for the given color.
	 * 
	 * @param color
	 * @return
	 */
	public static PositionID getActivePokemon(Color color) {
		if (color == Color.BLUE)
			return PositionID.BLUE_ACTIVEPOKEMON;
		else
			return PositionID.RED_ACTIVEPOKEMON;
	}

	/**
	 * Returns the respective bench position for the given player and the given index. Returns null, if the given index is not in the range of 1 to 5.
	 * 
	 * @param color
	 * @param index
	 * @return
	 */
	public static PositionID getBenchPosition(Color color, int index) {
		switch (index) {
		case 1:
			return color == Color.BLUE ? BLUE_BENCH_1 : RED_BENCH_1;
		case 2:
			return color == Color.BLUE ? BLUE_BENCH_2 : RED_BENCH_2;
		case 3:
			return color == Color.BLUE ? BLUE_BENCH_3 : RED_BENCH_3;
		case 4:
			return color == Color.BLUE ? BLUE_BENCH_4 : RED_BENCH_4;
		case 5:
			return color == Color.BLUE ? BLUE_BENCH_5 : RED_BENCH_5;
		default:
			return null;
		}
	}

	/**
	 * Returns the hand position for the given color.
	 * 
	 * @param color
	 * @return
	 */
	public static PositionID getHandPosition(Color color) {
		if (color == Color.BLUE)
			return PositionID.BLUE_HAND;
		else
			return PositionID.RED_HAND;
	}
}