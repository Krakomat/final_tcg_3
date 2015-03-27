package network.server;

import java.util.HashMap;
import java.util.Map;

import network.tcp.borders.ServerMain;

/**
 * This class is responsible for creating new games and managing all current games.
 * 
 * @author Michael
 *
 */
public class PokemonGameManagerFactory {

	private static Map<Long, PokemonGameManager> GAME_MAP = new HashMap<Long, PokemonGameManager>();

	/**
	 * Creates a new {@link PokemonGameManager} and returns its id to the method caller.
	 * 
	 * @param serverMain
	 * 
	 * @param server
	 * @return
	 */
	public static long createNewGame(String name, String password, ServerMain serverMain) {
		long minIDMissing = 1L;
		for (Long k : GAME_MAP.keySet())
			if (k.longValue() == minIDMissing)
				minIDMissing++;
		// Create new instance:
		PokemonGameManagerImpl pkmnGameMngr = new PokemonGameManagerImpl(minIDMissing, name, password, serverMain);
		GAME_MAP.put(minIDMissing, pkmnGameMngr);
		return pkmnGameMngr.getGameID();
	}

	/**
	 * Returns the instance of the {@link PokemonGameManager} with the given id. Returns null, if no such instance with the id was found.
	 * 
	 * @param id
	 * @return
	 */
	public static PokemonGameManager getGame(long id) {
		PokemonGameManager pgm = GAME_MAP.get(id);
		return pgm;
	}
}
