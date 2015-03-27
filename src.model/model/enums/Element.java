package model.enums;

import java.util.ArrayList;
import java.util.List;

public enum Element {
	WATER, FIRE, COLORLESS, LIGHTNING, GRASS, PSYCHIC, ROCK;

	public static int valueOf(Element element) {
		if (element.equals(COLORLESS))
			return 1;
		if (element.equals(LIGHTNING))
			return 2;
		if (element.equals(FIRE))
			return 3;
		if (element.equals(WATER))
			return 4;
		if (element.equals(GRASS))
			return 5;
		if (element.equals(ROCK))
			return 6;
		if (element.equals(PSYCHIC))
			return 7;
		return 0;
	}

	public static Element getElement(int value) {
		if (value == 1)
			return COLORLESS;
		if (value == 2)
			return LIGHTNING;
		if (value == 3)
			return FIRE;
		if (value == 4)
			return WATER;
		if (value == 5)
			return GRASS;
		if (value == 6)
			return ROCK;
		if (value == 7)
			return PSYCHIC;
		return null;
	}

	/**
	 * Gives a list of all elements.
	 * 
	 * @return
	 */
	public static List<Element> getAllElements() {
		List<Element> eleList = new ArrayList<>();
		eleList.add(WATER);
		eleList.add(FIRE);
		eleList.add(LIGHTNING);
		eleList.add(GRASS);
		eleList.add(PSYCHIC);
		eleList.add(ROCK);
		eleList.add(COLORLESS);
		return eleList;
	}
}