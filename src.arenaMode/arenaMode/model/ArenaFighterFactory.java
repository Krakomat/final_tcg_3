package arenaMode.model;

import java.util.ArrayList;

import model.database.Database;

public class ArenaFighterFactory {
	public static ArenaFighter createFighter(ArenaFighterCode code) {
		switch (code) {
		case MAMORIA_BRENDAN:
			return new ArenaFighter(code, "Brendan", "RockTrainer_2.xml", Database.getAssetKey(ArenaFighterCode.MAMORIA_BRENDAN.toString()),
					Database.getAssetKey(ArenaFighterCode.MAMORIA_BRENDAN.toString() + "_THUMB"), new ArrayList<>());
		case MAMORIA_RED:
			return new ArenaFighter(code, "Red", "RockTrainer_1.xml", Database.getAssetKey(ArenaFighterCode.MAMORIA_RED.toString()),
					Database.getAssetKey(ArenaFighterCode.MAMORIA_RED.toString() + "_THUMB"), new ArrayList<>());
		case MAMORIA_BROCK:// TODO set deck
			return new ArenaFighter(code, "Rocko", "RockTrainer_1.xml", Database.getAssetKey(ArenaFighterCode.MAMORIA_BROCK.toString()),
					Database.getAssetKey(ArenaFighterCode.MAMORIA_BROCK.toString() + "_THUMB"), new ArrayList<>());
		default:
			break;
		}
		return null;
	}
}
