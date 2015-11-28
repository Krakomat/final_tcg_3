package model.enums;

import java.util.ArrayList;
import java.util.List;

public enum Element {
	WATER, FIRE, COLORLESS, LIGHTNING, GRASS, PSYCHIC, ROCK, RAINBOW;

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
		if (element.equals(RAINBOW))
			return 8;
		return 0;
	}

	public static int compareElements(Element e1, Element e2) {
		int v1 = getValue(e1);
		int v2 = getValue(e2);
		return v1 < v2 ? -1 : (v1 > v2) ? 1 : 0;
	}

	private static int getValue(Element e) {
		switch (e) {
		case COLORLESS:
			return 1;
		case FIRE:
			return 2;
		case GRASS:
			return 3;
		case LIGHTNING:
			return 4;
		case PSYCHIC:
			return 5;
		case ROCK:
			return 6;
		case WATER:
			return 7;
		case RAINBOW:
			return 8;
		default:
			return -1;
		}
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
		if (value == 8)
			return RAINBOW;
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