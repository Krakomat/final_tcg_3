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
					Database.getAssetKey(ArenaFighterCode.MAMORIA_BRENDAN.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.MAMORIA_RED.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.MAMORIA_BROCK.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.AZURIA_LYRA.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.AZURIA_MAY.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.AZURIA_MISTY.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.ORANIA_LEAF.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.ORANIA_NATE.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.ORANIA_LTSURGE.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.PRISMANIA_ROSA.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.PRISMANIA_SERENA.toString() + "THUMB"), lockedCards);
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
					Database.getAssetKey(ArenaFighterCode.PRISMANIA_ERIKA.toString() + "THUMB"), lockedCards);
		case FUCHSANIA_HILBERT:
			lockedCards.add("00394");
			lockedCards.add("00395");
			lockedCards.add("00396");
			lockedCards.add("00397");
			lockedCards.add("00398");
			lockedCards.add("00399");
			lockedCards.add("00400");
			return new ArenaFighter(code, Database.getBot("Hilbert"), Database.getAssetKey(ArenaFighterCode.FUCHSANIA_HILBERT.toString()),
					Database.getAssetKey(ArenaFighterCode.FUCHSANIA_HILBERT.toString() + "THUMB"), lockedCards);
		case FUCHSANIA_KOGA:
			lockedCards.add("00383");
			lockedCards.add("00384");
			lockedCards.add("00385");
			lockedCards.add("00386");
			lockedCards.add("00387");
			lockedCards.add("00388");
			return new ArenaFighter(code, Database.getBot("Koga"), Database.getAssetKey(ArenaFighterCode.FUCHSANIA_KOGA.toString()),
					Database.getAssetKey(ArenaFighterCode.FUCHSANIA_KOGA.toString() + "THUMB"), lockedCards);
		case FUCHSANIA_LUCAS:
			lockedCards.add("00389");
			lockedCards.add("00390");
			lockedCards.add("00391");
			lockedCards.add("00392");
			lockedCards.add("00393");
			lockedCards.add("00401");
			lockedCards.add("00402");
			return new ArenaFighter(code, Database.getBot("Lucas"), Database.getAssetKey(ArenaFighterCode.FUCHSANIA_LUCAS.toString()),
					Database.getAssetKey(ArenaFighterCode.FUCHSANIA_LUCAS.toString() + "THUMB"), lockedCards);
		case SAFFRONIA_CALEM:
			lockedCards.add("00408");
			lockedCards.add("00409");
			lockedCards.add("00410");
			lockedCards.add("00411");
			lockedCards.add("00412");
			lockedCards.add("00413");
			lockedCards.add("00414");
			lockedCards.add("00424");
			lockedCards.add("00425");
			lockedCards.add("00426");
			lockedCards.add("00427");
			lockedCards.add("00428");
			lockedCards.add("00429");
			lockedCards.add("00430");
			return new ArenaFighter(code, Database.getBot("Calem"), Database.getAssetKey(ArenaFighterCode.SAFFRONIA_CALEM.toString()),
					Database.getAssetKey(ArenaFighterCode.SAFFRONIA_CALEM.toString() + "THUMB"), lockedCards);
		case SAFFRONIA_HILDA:
			lockedCards.add("00405");
			lockedCards.add("00406");
			lockedCards.add("00407");
			lockedCards.add("00419");
			lockedCards.add("00420");
			lockedCards.add("00421");
			lockedCards.add("00422");
			lockedCards.add("00423");
			lockedCards.add("00431");
			return new ArenaFighter(code, Database.getBot("Hilda"), Database.getAssetKey(ArenaFighterCode.SAFFRONIA_HILDA.toString()),
					Database.getAssetKey(ArenaFighterCode.SAFFRONIA_HILDA.toString() + "THUMB"), lockedCards);
		case SAFFRONIA_SABRINA:
			lockedCards.add("00403");
			lockedCards.add("00404");
			lockedCards.add("00415");
			lockedCards.add("00416");
			lockedCards.add("00417");
			lockedCards.add("00418");
			return new ArenaFighter(code, Database.getBot("Sabrina"), Database.getAssetKey(ArenaFighterCode.SAFFRONIA_SABRINA.toString()),
					Database.getAssetKey(ArenaFighterCode.SAFFRONIA_SABRINA.toString() + "THUMB"), lockedCards);
		case ZINNOBER_DAWN:
			lockedCards.add("00436");
			lockedCards.add("00437");
			lockedCards.add("00438");	
			lockedCards.add("00439");	
			lockedCards.add("00440");
			lockedCards.add("00442");	
			lockedCards.add("00450");
			lockedCards.add("00451");
			lockedCards.add("00452");
			lockedCards.add("00453");
			lockedCards.add("00454");
			lockedCards.add("00455");
			lockedCards.add("00456");
			lockedCards.add("00458");
			return new ArenaFighter(code, Database.getBot("Dawn"), Database.getAssetKey(ArenaFighterCode.ZINNOBER_DAWN.toString()),
					Database.getAssetKey(ArenaFighterCode.ZINNOBER_DAWN.toString() + "THUMB"), lockedCards);
		case ZINNOBER_ETHAN:
			lockedCards.add("00433");
			lockedCards.add("00434");
			lockedCards.add("00435");	
			lockedCards.add("00441");	
			lockedCards.add("00447");
			lockedCards.add("00448");	
			lockedCards.add("00449");
			lockedCards.add("00457");
			return new ArenaFighter(code, Database.getBot("Ethan"), Database.getAssetKey(ArenaFighterCode.ZINNOBER_ETHAN.toString()),
					Database.getAssetKey(ArenaFighterCode.ZINNOBER_ETHAN.toString() + "THUMB"), lockedCards);
		case ZINNOBER_PYRO:
			lockedCards.add("00432");
			lockedCards.add("00443");
			lockedCards.add("00444");
			lockedCards.add("00445");
			lockedCards.add("00446");
			return new ArenaFighter(code, Database.getBot("Blaine"), Database.getAssetKey(ArenaFighterCode.ZINNOBER_PYRO.toString()),
					Database.getAssetKey(ArenaFighterCode.ZINNOBER_PYRO.toString() + "THUMB"), lockedCards);
		case VERTANIA_GIOVANNI:
			lockedCards.add("00478");
			lockedCards.add("00479");
			lockedCards.add("00480");
			lockedCards.add("00481");
			lockedCards.add("00482");
			lockedCards.add("00483");
			lockedCards.add("00484");
			lockedCards.add("00485");
			lockedCards.add("00486");
			lockedCards.add("00487");
			lockedCards.add("00488");
			lockedCards.add("00489");
			lockedCards.add("00490");
			lockedCards.add("00491");
			lockedCards.add("00492");
			lockedCards.add("00493");
			lockedCards.add("00494");
			lockedCards.add("00495");
			return new ArenaFighter(code, Database.getBot("Giovanni"), Database.getAssetKey(ArenaFighterCode.VERTANIA_GIOVANNI.toString()),
					Database.getAssetKey(ArenaFighterCode.VERTANIA_GIOVANNI.toString() + "THUMB"), lockedCards);
		case VERTANIA_ROCKET_FEMALE:
			lockedCards.add("00459");
			lockedCards.add("00468");
			lockedCards.add("00469");
			lockedCards.add("00470");
			lockedCards.add("00471");
			lockedCards.add("00473");
			lockedCards.add("00474");
			lockedCards.add("00475");
			lockedCards.add("00476");
			lockedCards.add("00477");
			return new ArenaFighter(code, Database.getBot("Rocket Female"), Database.getAssetKey(ArenaFighterCode.VERTANIA_ROCKET_FEMALE.toString()),
					Database.getAssetKey(ArenaFighterCode.VERTANIA_ROCKET_FEMALE.toString() + "THUMB"), lockedCards);
		case VERTANIA_ROCKET_MALE:
			lockedCards.add("00460");
			lockedCards.add("00461");
			lockedCards.add("00462");
			lockedCards.add("00463");
			lockedCards.add("00464");
			lockedCards.add("00465");
			lockedCards.add("00466");
			lockedCards.add("00467");
			lockedCards.add("00472");
			return new ArenaFighter(code, Database.getBot("Rocket Male"), Database.getAssetKey(ArenaFighterCode.VERTANIA_ROCKET_MALE.toString()),
					Database.getAssetKey(ArenaFighterCode.VERTANIA_ROCKET_MALE.toString() + "THUMB"), lockedCards);
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
