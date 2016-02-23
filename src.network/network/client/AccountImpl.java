package network.client;

import java.util.ArrayList;
import java.util.List;

import arenaMode.model.ArenaFighterCode;
import model.database.Deck;
import model.enums.AccountType;

public abstract class AccountImpl implements Account {

	protected long id;
	protected String name, password;
	protected Deck deck;
	protected int prizeCards;
	protected AccountType accountType;
	protected List<ArenaFighterCode> defeatedArenaFighters;
	protected List<String> unlockedCards;

	public AccountImpl(long id, String name, String password, int prizeCards) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		this.prizeCards = prizeCards;
		deck = null;
		accountType = null; // is set in an upper level constructor
		unlockedCards = new ArrayList<>();
		defeatedArenaFighters = new ArrayList<>();
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Deck getDeck() {
		return deck;
	}

	@Override
	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public int getPrizeCards() {
		return prizeCards;
	}

	public void setPriceCards(int number) {
		this.prizeCards = number;
	}

	@Override
	public AccountType getAccountType() {
		return this.accountType;
	}

	public List<ArenaFighterCode> getDefeatedArenaFighters() {
		return defeatedArenaFighters;
	}

	public void setDefeatedArenaFighters(List<ArenaFighterCode> defeatedArenaFighters) {
		this.defeatedArenaFighters = defeatedArenaFighters;
	}

	public List<String> getUnlockedCards() {
		return unlockedCards;
	}

	public void setUnlockedCards(List<String> unlockedCards) {
		this.unlockedCards = unlockedCards;
	}
}
