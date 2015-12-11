package arenaMode.model;

import java.util.ArrayList;
import java.util.List;

import model.database.Database;

public class ArenaFighterFactory {
	public static ArenaFighter createFighter(ArenaFighterCode code) {
		List<String> lockedCards = new ArrayList<>();
		switch (code) {
		case MAMORIA_BRENDAN:
			lockedCards = new ArrayList<>();
			lockedCards.add("00277");
			lockedCards.add("00278");
			lockedCards.add("00279");
			lockedCards.add("00280");
			lockedCards.add("00281");
			lockedCards.add("00282");
			lockedCards.add("00283");
			lockedCards.add("00284");
			lockedCards.add("00291");
			lockedCards.add("00292");
			return new ArenaFighter(code, Database.getBot("Brendan"), Database.getAssetKey(ArenaFighterCode.MAMORIA_BRENDAN.toString()),
					Database.getAssetKey(ArenaFighterCode.MAMORIA_BRENDAN.toString() + "_THUMB"), lockedCards);
		case MAMORIA_RED:
			lockedCards = new ArrayList<>();
			lockedCards.add("00272");
			lockedCards.add("00273");
			lockedCards.add("00274");
			lockedCards.add("00275");
			lockedCards.add("00276");
			lockedCards.add("00285");
			lockedCards.add("00286");
			lockedCards.add("00288");
			lockedCards.add("00289");
			lockedCards.add("00290");
			return new ArenaFighter(code, Database.getBot("Red"), Database.getAssetKey(ArenaFighterCode.MAMORIA_RED.toString()),
					Database.getAssetKey(ArenaFighterCode.MAMORIA_RED.toString() + "_THUMB"), lockedCards);
		case MAMORIA_BROCK:
			lockedCards.add("00266");
			lockedCards.add("00267");
			lockedCards.add("00268");
			lockedCards.add("00269");
			lockedCards.add("00270");
			lockedCards.add("00271");
			lockedCards.add("00287");
			lockedCards.add("00293");
			return new ArenaFighter(code, Database.getBot("Brock"), Database.getAssetKey(ArenaFighterCode.MAMORIA_BROCK.toString()),
					Database.getAssetKey(ArenaFighterCode.MAMORIA_BROCK.toString() + "_THUMB"), lockedCards);
		case AZURIA_LYRA:
			lockedCards.add("00306");
			lockedCards.add("00307");
			lockedCards.add("00308");
			lockedCards.add("00309");
			lockedCards.add("00310");
			lockedCards.add("00311");
			lockedCards.add("00314");
			lockedCards.add("00318");
			lockedCards.add("00319");
			lockedCards.add("00320");
			lockedCards.add("00321");
			lockedCards.add("00322");
			lockedCards.add("00323");
			return new ArenaFighter(code, Database.getBot("Lyra"), Database.getAssetKey(ArenaFighterCode.AZURIA_LYRA.toString()),
					Database.getAssetKey(ArenaFighterCode.AZURIA_LYRA.toString() + "_THUMB"), lockedCards);
		case AZURIA_MAY:
			lockedCards.add("00301");
			lockedCards.add("00302");
			lockedCards.add("00303");
			lockedCards.add("00304");
			lockedCards.add("00305");
			lockedCards.add("00312");
			lockedCards.add("00313");
			lockedCards.add("00317");
			lockedCards.add("00325");
			return new ArenaFighter(code, Database.getBot("May"), Database.getAssetKey(ArenaFighterCode.AZURIA_MAY.toString()),
					Database.getAssetKey(ArenaFighterCode.AZURIA_MAY.toString() + "_THUMB"), lockedCards);
		case AZURIA_MISTY:
			lockedCards.add("00294");
			lockedCards.add("00295");
			lockedCards.add("00296");
			lockedCards.add("00297");
			lockedCards.add("00298");
			lockedCards.add("00299");
			lockedCards.add("00300");
			lockedCards.add("00315");
			lockedCards.add("00316");
			lockedCards.add("00324");
			return new ArenaFighter(code, Database.getBot("Misty"), Database.getAssetKey(ArenaFighterCode.AZURIA_MISTY.toString()),
					Database.getAssetKey(ArenaFighterCode.AZURIA_MISTY.toString() + "_THUMB"), lockedCards);
		case ORANIA_LEAF:
			lockedCards.add("00335");
			lockedCards.add("00336");
			lockedCards.add("00337");
			lockedCards.add("00338");
			lockedCards.add("00339");
			lockedCards.add("00348");
			lockedCards.add("00349");
			lockedCards.add("00350");
			return new ArenaFighter(code, Database.getBot("Leaf"), Database.getAssetKey(ArenaFighterCode.ORANIA_LEAF.toString()),
					Database.getAssetKey(ArenaFighterCode.ORANIA_LEAF.toString() + "_THUMB"), lockedCards);
		case ORANIA_NATE:
			lockedCards.add("00332");
			lockedCards.add("00333");
			lockedCards.add("00334");
			lockedCards.add("00340");
			lockedCards.add("00341");
			lockedCards.add("00342");
			lockedCards.add("00345");
			lockedCards.add("00346");
			lockedCards.add("00347");
			return new ArenaFighter(code, Database.getBot("Nate"), Database.getAssetKey(ArenaFighterCode.ORANIA_NATE.toString()),
					Database.getAssetKey(ArenaFighterCode.ORANIA_NATE.toString() + "_THUMB"), lockedCards);
		case ORANIA_LTSURGE:
			lockedCards.add("00326");
			lockedCards.add("00327");
			lockedCards.add("00328");
			lockedCards.add("00329");
			lockedCards.add("00330");
			lockedCards.add("00331");
			lockedCards.add("00343");
			lockedCards.add("00344");
			return new ArenaFighter(code, Database.getBot("Lt. Surge"), Database.getAssetKey(ArenaFighterCode.ORANIA_LTSURGE.toString()),
					Database.getAssetKey(ArenaFighterCode.ORANIA_LTSURGE.toString() + "_THUMB"), lockedCards);
		case PRISMANIA_ROSA:
			return new ArenaFighter(code, Database.getBot("Rosa"), Database.getAssetKey(ArenaFighterCode.PRISMANIA_ROSA.toString()),
					Database.getAssetKey(ArenaFighterCode.PRISMANIA_ROSA.toString() + "_THUMB"), lockedCards);
		case PRISMANIA_SERENA:
			return new ArenaFighter(code, Database.getBot("Serena"), Database.getAssetKey(ArenaFighterCode.PRISMANIA_SERENA.toString()),
					Database.getAssetKey(ArenaFighterCode.PRISMANIA_SERENA.toString() + "_THUMB"), lockedCards);
		case PRISMANIA_ERIKA:
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.PRISMANIA_ERIKA.toString()),
					Database.getAssetKey(ArenaFighterCode.PRISMANIA_ERIKA.toString() + "_THUMB"), lockedCards);
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
		erg.add(createFighter(ArenaFighterCode.AZURIA_LYRA));
		erg.add(createFighter(ArenaFighterCode.AZURIA_MAY));
		erg.add(createFighter(ArenaFighterCode.AZURIA_MISTY));
		erg.add(createFighter(ArenaFighterCode.ORANIA_LEAF));
		erg.add(createFighter(ArenaFighterCode.ORANIA_NATE));
		erg.add(createFighter(ArenaFighterCode.ORANIA_LTSURGE));
		erg.add(createFighter(ArenaFighterCode.PRISMANIA_ROSA));
		erg.add(createFighter(ArenaFighterCode.PRISMANIA_SERENA));
		erg.add(createFighter(ArenaFighterCode.PRISMANIA_ERIKA));
		return erg;
	}
}
