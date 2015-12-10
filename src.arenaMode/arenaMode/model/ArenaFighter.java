package arenaMode.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jme3.asset.TextureKey;

import model.database.Deck;
import network.client.Account;

public class ArenaFighter {
	private TextureKey characterModel, characterThumb;
	private Account botAccount;
	private ArenaFighterCode code;
	private List<String> unlockableCards;

	public ArenaFighter(ArenaFighterCode code, Account botAccount, TextureKey characterModel, TextureKey characterThumb, List<String> unlockableCards) {
		this.code = code;
		this.botAccount = botAccount;
		this.characterModel = characterModel;
		this.characterThumb = characterThumb;
		this.unlockableCards = unlockableCards;
	}

	/**
	 * Returns a list of the card ids that the given account has yet to unlock.
	 * 
	 * @param account
	 * @return
	 */
	public List<String> getLockedCards(Account account) {
		List<String> unlockedCards = account.getUnlockedCards();
		List<String> lockedCards = new ArrayList<>();
		for (String cardID : unlockableCards) {
			if (!unlockedCards.contains(cardID))
				lockedCards.add(cardID);
		}
		return lockedCards;
	}

	/**
	 * Returns a list of the card ids that the given account has yet to unlock.
	 * 
	 * @param account
	 * @return
	 */
	public List<String> getUnlockedCards(Account account) {
		List<String> unlockedCards = account.getUnlockedCards();
		List<String> lockedCards = new ArrayList<>();
		for (String cardID : unlockableCards) {
			if (unlockedCards.contains(cardID))
				lockedCards.add(cardID);
		}
		return lockedCards;
	}

	public TextureKey getCharacterModel() {
		return characterModel;
	}

	public TextureKey getCharacterThumb() {
		return characterThumb;
	}

	public String getName() {
		return botAccount.getName();
	}

	public ArenaFighterCode getCode() {
		return code;
	}

	public Deck getDeck() {
		return botAccount.getDeck();
	}

	public List<String> getUnlockableCards() {
		return unlockableCards;
	}

	public String createReward(Account account) {
		if (this.getLockedCards(account).size() > 0) {
			Random r = new SecureRandom();
			int index = r.nextInt(this.getLockedCards(account).size());
			return this.getLockedCards(account).get(index);
		}
		return null;
	}
}
