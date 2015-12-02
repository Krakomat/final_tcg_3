package arenaMode.model;

import java.io.File;
import java.util.List;

import com.jme3.asset.TextureKey;

import model.database.Deck;
import model.game.GameParameters;

public class ArenaFighter {
	private TextureKey characterModel, characterThumb;
	private String name;
	private ArenaFighterCode code;
	private Deck deck;
	private List<String> unlockableCards;

	public ArenaFighter(ArenaFighterCode code, String name, String deckName, TextureKey characterModel, TextureKey characterThumb, List<String> unlockableCards) {
		this.code = code;
		this.name = name;
		this.deck = Deck.readFromDatabaseFile(new File(GameParameters.ARENA_DECK_PATH + deckName));
		this.characterModel = characterModel;
		this.characterThumb = characterThumb;
		this.unlockableCards = unlockableCards;
	}

	public TextureKey getCharacterModel() {
		return characterModel;
	}

	public TextureKey getCharacterThumb() {
		return characterThumb;
	}

	public String getName() {
		return name;
	}

	public ArenaFighterCode getCode() {
		return code;
	}

	public Deck getDeck() {
		return deck;
	}

	public List<String> getUnlockableCards() {
		return unlockableCards;
	}
}
