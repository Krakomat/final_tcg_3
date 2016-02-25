package draftTournament.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Preconditions;

import model.database.Card;
import model.enums.Element;

public class DraftTournamentDatabase {
	private List<Element> elements;
	private Random random;

	public DraftTournamentDatabase() {
		random = new SecureRandom();
		elements = Element.getAllElements();
		elements.remove(Element.COLORLESS);
	}

	public List<Element> getRandomElementList(int number) {
		Preconditions.checkArgument(Element.getAllElements().size() - 1 > 0);
		Preconditions.checkArgument(number <= Element.getAllElements().size() - 1);

		List<Integer> indices = this.createIndices(number, Element.getAllElements().size() - 2);
		List<Element> erg = new ArrayList<>();
		for (Integer index : indices)
			erg.add(elements.get(index));
		return erg;
	}

	private List<Integer> createIndices(int number, int maxIndex) {
		Preconditions.checkArgument(number <= maxIndex);

		List<Integer> indices = new ArrayList<>();
		int full = 0;
		while (full < number) {
			int index = random.nextInt(maxIndex);
			if (!indices.contains(index)) {
				indices.add(index);
				full++;
			}
		}
		return indices;
	}

	public List<Card> getRandomCardList(int i) {
		// TODO Auto-generated method stub
		return null;
	}
}
