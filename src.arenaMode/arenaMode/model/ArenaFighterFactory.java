package arenaMode.model;

import java.util.ArrayList;
import java.util.List;

import model.database.Database;

public class ArenaFighterFactory {
	public static ArenaFighter createFighter(ArenaFighterCode code) {
		switch (code) {
		case MAMORIA_BRENDAN:
			return new ArenaFighter(code, "Brendan", "Rock Solid.xml", Database.getAssetKey(ArenaFighterCode.MAMORIA_BRENDAN.toString()),
					Database.getAssetKey(ArenaFighterCode.MAMORIA_BRENDAN.toString() + "_THUMB"), new ArrayList<>());
		case MAMORIA_RED:
			return new ArenaFighter(code, "Red", "Fighting Spirit.xml", Database.getAssetKey(ArenaFighterCode.MAMORIA_RED.toString()),
					Database.getAssetKey(ArenaFighterCode.MAMORIA_RED.toString() + "_THUMB"), new ArrayList<>());
		case MAMORIA_BROCK:// TODO set deck
			List<String> lockedCards = new ArrayList<>();
			lockedCards.add("00266");
			lockedCards.add("00267");
			lockedCards.add("00268");
			lockedCards.add("00269");
			lockedCards.add("00270");
			lockedCards.add("00271");
			lockedCards.add("00272");
			lockedCards.add("00273");
			lockedCards.add("00274");
			lockedCards.add("00275");
			lockedCards.add("00276");
			lockedCards.add("00277");
			lockedCards.add("00278");
			lockedCards.add("00279");
			lockedCards.add("00280");
			lockedCards.add("00281");
			lockedCards.add("00282");
			lockedCards.add("00283");
			lockedCards.add("00284");
			lockedCards.add("00285");
			lockedCards.add("00286");
			lockedCards.add("00287");
			lockedCards.add("00288");
			lockedCards.add("00289");
			lockedCards.add("00290");
			lockedCards.add("00291");
			lockedCards.add("00292");
			lockedCards.add("00293");
			return new ArenaFighter(code, "Rocko", "Fighting Spirit.xml", Database.getAssetKey(ArenaFighterCode.MAMORIA_BROCK.toString()),
					Database.getAssetKey(ArenaFighterCode.MAMORIA_BROCK.toString() + "_THUMB"), lockedCards);
		default:
			break;
		}
		return null;
	}

	public static List<ArenaFighter> getAllArenaFighters() {
		List<ArenaFighter> erg = new ArrayList<>();
		erg.add(createFighter(ArenaFighterCode.MAMORIA_RED));
		erg.add(createFighter(ArenaFighterCode.MAMORIA_BRENDAN));
		erg.add(createFighter(ArenaFighterCode.MAMORIA_BROCK));
		return erg;
	}
}
