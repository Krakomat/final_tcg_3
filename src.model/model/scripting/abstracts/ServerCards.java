package model.scripting.abstracts;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all card ids, whose actions have to be determined by the server.
 * 
 * @author Michael
 *
 */
public class ServerCards {
	
	public static List<String> createInstance() {
		List<String> list = new ArrayList<>();
		list.add("00027"); // Porenta
		list.add("00077"); // PokemonHändler
		return list;
	}
}
