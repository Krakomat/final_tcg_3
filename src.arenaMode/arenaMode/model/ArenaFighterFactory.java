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
			lockedCards.add("00365");
			lockedCards.add("00366");
			lockedCards.add("00367");
			lockedCards.add("00368");
			lockedCards.add("00369");
			lockedCards.add("00379");
			lockedCards.add("00380");
			lockedCards.add("00381");
			return new ArenaFighter(code, Database.getBot("Rosa"), Database.getAssetKey(ArenaFighterCode.PRISMANIA_ROSA.toString()),
					Database.getAssetKey(ArenaFighterCode.PRISMANIA_ROSA.toString() + "_THUMB"), lockedCards);
		case PRISMANIA_SERENA:
			lockedCards.add("00357");
			lockedCards.add("00358");
			lockedCards.add("00359");
			lockedCards.add("00360");
			lockedCards.add("00361");
			lockedCards.add("00362");
			lockedCards.add("00363");
			lockedCards.add("00364");
			lockedCards.add("00370");
			lockedCards.add("00371");
			lockedCards.add("00372");
			lockedCards.add("00373");
			lockedCards.add("00375");
			lockedCards.add("00376");
			lockedCards.add("00377");
			lockedCards.add("00378");
			return new ArenaFighter(code, Database.getBot("Serena"), Database.getAssetKey(ArenaFighterCode.PRISMANIA_SERENA.toString()),
					Database.getAssetKey(ArenaFighterCode.PRISMANIA_SERENA.toString() + "_THUMB"), lockedCards);
		case PRISMANIA_ERIKA:
			lockedCards.add("00351");
			lockedCards.add("00352");
			lockedCards.add("00353");
			lockedCards.add("00354");
			lockedCards.add("00355");
			lockedCards.add("00356");
			lockedCards.add("00374");
			lockedCards.add("00382");
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.PRISMANIA_ERIKA.toString()),
					Database.getAssetKey(ArenaFighterCode.PRISMANIA_ERIKA.toString() + "_THUMB"), lockedCards);
		case FUCHSANIA_HILBERT:
			lockedCards.add("00394");
			lockedCards.add("00395");
			lockedCards.add("00396");
			lockedCards.add("00397");
			lockedCards.add("00398");
			lockedCards.add("00399");
			lockedCards.add("00400");
			return new ArenaFighter(code, Database.getBot("Hilbert"), Database.getAssetKey(ArenaFighterCode.FUCHSANIA_HILBERT.toString()),
					Database.getAssetKey(ArenaFighterCode.FUCHSANIA_HILBERT.toString() + "_THUMB"), lockedCards);
		case FUCHSANIA_KOGA:
			lockedCards.add("00383");
			lockedCards.add("00384");
			lockedCards.add("00385");
			lockedCards.add("00386");
			lockedCards.add("00387");
			lockedCards.add("00388");
			return new ArenaFighter(code, Database.getBot("Koga"), Database.getAssetKey(ArenaFighterCode.FUCHSANIA_KOGA.toString()),
					Database.getAssetKey(ArenaFighterCode.FUCHSANIA_KOGA.toString() + "_THUMB"), lockedCards);
		case FUCHSANIA_LUCAS:
			lockedCards.add("00389");
			lockedCards.add("00390");
			lockedCards.add("00391");
			lockedCards.add("00392");
			lockedCards.add("00393");
			lockedCards.add("00401");
			lockedCards.add("00402");
			return new ArenaFighter(code, Database.getBot("Lucas"), Database.getAssetKey(ArenaFighterCode.FUCHSANIA_LUCAS.toString()),
					Database.getAssetKey(ArenaFighterCode.FUCHSANIA_LUCAS.toString() + "_THUMB"), lockedCards);
		// TODO: Create bots with decks for new cards
		case SAFFRONIA_CALEM:
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.SAFFRONIA_CALEM.toString()),
					Database.getAssetKey(ArenaFighterCode.SAFFRONIA_CALEM.toString() + "_THUMB"), lockedCards);
		case SAFFRONIA_HILDA:
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.SAFFRONIA_HILDA.toString()),
					Database.getAssetKey(ArenaFighterCode.SAFFRONIA_HILDA.toString() + "_THUMB"), lockedCards);
		case SAFFRONIA_SABRINA:
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.SAFFRONIA_SABRINA.toString()),
					Database.getAssetKey(ArenaFighterCode.SAFFRONIA_SABRINA.toString() + "_THUMB"), lockedCards);
		case ZINNOBER_DAWN:
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.ZINNOBER_DAWN.toString()),
					Database.getAssetKey(ArenaFighterCode.ZINNOBER_DAWN.toString() + "_THUMB"), lockedCards);
		case ZINNOBER_ETHAN:
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.ZINNOBER_ETHAN.toString()),
					Database.getAssetKey(ArenaFighterCode.ZINNOBER_ETHAN.toString() + "_THUMB"), lockedCards);
		case ZINNOBER_PYRO:
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.ZINNOBER_PYRO.toString()),
					Database.getAssetKey(ArenaFighterCode.ZINNOBER_PYRO.toString() + "_THUMB"), lockedCards);
		case VERTANIA_GIOVANNI:
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.VERTANIA_GIOVANNI.toString()),
					Database.getAssetKey(ArenaFighterCode.VERTANIA_GIOVANNI.toString() + "_THUMB"), lockedCards);
		case VERTANIA_ROCKET_FEMALE:
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.VERTANIA_ROCKET_FEMALE.toString()),
					Database.getAssetKey(ArenaFighterCode.VERTANIA_ROCKET_FEMALE.toString() + "_THUMB"), lockedCards);
		case VERTANIA_ROCKET_MALE:
			return new ArenaFighter(code, Database.getBot("Erika"), Database.getAssetKey(ArenaFighterCode.VERTANIA_ROCKET_MALE.toString()),
					Database.getAssetKey(ArenaFighterCode.VERTANIA_ROCKET_MALE.toString() + "_THUMB"), lockedCards);
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
		erg.add(createFighter(ArenaFighterCode.FUCHSANIA_HILBERT));
		erg.add(createFighter(ArenaFighterCode.FUCHSANIA_LUCAS));
		erg.add(createFighter(ArenaFighterCode.FUCHSANIA_KOGA));
		erg.add(createFighter(ArenaFighterCode.SAFFRONIA_CALEM));
		erg.add(createFighter(ArenaFighterCode.SAFFRONIA_HILDA));
		erg.add(createFighter(ArenaFighterCode.SAFFRONIA_SABRINA));
		erg.add(createFighter(ArenaFighterCode.ZINNOBER_DAWN));
		erg.add(createFighter(ArenaFighterCode.ZINNOBER_ETHAN));
		erg.add(createFighter(ArenaFighterCode.ZINNOBER_PYRO));
		erg.add(createFighter(ArenaFighterCode.VERTANIA_ROCKET_FEMALE));
		erg.add(createFighter(ArenaFighterCode.VERTANIA_ROCKET_MALE));
		erg.add(createFighter(ArenaFighterCode.VERTANIA_GIOVANNI));
		return erg;
	}
}
