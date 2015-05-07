package model.enums;

import java.util.ArrayList;
import java.util.Random;

import model.database.Booster;
import model.database.Card;
import model.database.Database;

public enum Edition {

	BASE, JUNGLE, FOSSIL, TOKEN;

	public static Booster createBooster(Edition edition) {
		ArrayList<Card> legendary = new ArrayList<Card>(); // >=1
		ArrayList<Card> holo = new ArrayList<Card>(); // >=1
		ArrayList<Card> rare = new ArrayList<Card>(); // >=1
		ArrayList<Card> uncommon = new ArrayList<Card>(); // >=3
		ArrayList<Card> common = new ArrayList<Card>(); // >=6

		for (int i = 0; i < Database.cards.size(); i++) {
			Card c = Database.createCard(Database.cards.get(i).getCardId());
			if (c.getRarity().equals(Rarity.COMMON))
				common.add(c);
			if (c.getRarity().equals(Rarity.UNCOMMON))
				uncommon.add(c);
			if (c.getRarity().equals(Rarity.RARE))
				rare.add(c);
			if (c.getRarity().equals(Rarity.HOLO))
				holo.add(c);
			if (c.getRarity().equals(Rarity.LEGENDARY))
				legendary.add(c);
		}

		Booster b = new Booster(edition);
		Random r = new Random();
		for (int i = 0; i < 6; i++) {
			int rand = r.nextInt(common.size());
			b.getCards().add(common.remove(rand));
		}
		for (int i = 0; i < 3; i++) {
			int rand = r.nextInt(uncommon.size());
			b.getCards().add(uncommon.remove(rand));
		}
		int rand = r.nextInt(100);
		if (rand <= 5) {
			rand = r.nextInt(legendary.size());
			b.getCards().add(legendary.remove(rand));
		} else if (rand <= 25) {
			rand = r.nextInt(holo.size());
			b.getCards().add(holo.remove(rand));
		} else {
			rand = r.nextInt(rare.size());
			b.getCards().add(rare.remove(rand));
		}

		return b;
	}
}