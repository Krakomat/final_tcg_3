package model.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CardLibrary {

	protected List<String> cards;
	protected HashMap<String, Integer> cardSizeMap;

	public CardLibrary(List<String> cards) {
		this.cards = cards;
		initCardMap();
	}

	private void initCardMap() {
		cardSizeMap = new HashMap<String, Integer>();
		for (int i = 0; i < cards.size(); i++) {
			String id = cards.get(i);
			if (cardSizeMap.containsKey(id)) {
				// Copy of Card already in Library
				Integer prevSize = cardSizeMap.get(id);
				cardSizeMap.put(id, prevSize + 1);
			} else
				cardSizeMap.put(id, 1);
		}
	}

	/**
	 * Adds the given card to this library.
	 * 
	 * @param c
	 */
	public void addCard(String id) {
		cards.add(id);
		if (cardSizeMap.containsKey(id)) {
			// Copy of Card already in Library
			Integer prevSize = cardSizeMap.get(id);
			cardSizeMap.put(id, prevSize + 1);
		} else
			cardSizeMap.put(id, 1);
	}

	/**
	 * Removes the given card from this library.
	 * 
	 * @param c
	 */
	public void removeCard(String id) {
		boolean removed = false;
		for (int i = 0; i < cards.size() && !removed; i++) {
			if (cards.get(i).equals(id)) {
				cards.remove(i);
				removed = true;
			}
		}

		if (cardSizeMap.get(id) == 1)
			// Delete the whole mapping for this card:
			cardSizeMap.remove(id);
		else {
			// Decrease size of the removed card
			Integer prevSize = cardSizeMap.get(id);
			cardSizeMap.put(id, prevSize - 1);
		}
	}

	/**
	 * Returns the number of pieces in the library of the given card.
	 * 
	 * @param c
	 * @return
	 */
	public int getCardSize(String id) {
		Integer value = cardSizeMap.get(id);
		if (value == 0)
			return 0;
		return value;
	}

	public List<String> getCards() {
		return this.cards;
	}

	public String get(int i) {
		return cards.get(i);
	}

	public int size() {
		return cards.size();
	}

	public HashMap<String, Integer> getCardSizeMap() {
		return cardSizeMap;
	}

	public List<String> getUniqueCards() {
		Set<String> keyset = this.cardSizeMap.keySet();
		List<String> keyList = new ArrayList<>();
		for (String key : keyset)
			keyList.add(key);
		return keyList;
	}
}
