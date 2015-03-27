package network.client;

import model.database.Deck;
import model.enums.AccountType;

public abstract class AccountImpl implements Account {

	protected long id;
	protected String name, password;
	protected Deck deck;
	protected AccountType accountType;

	public AccountImpl(long id, String name, String password) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		deck = null;
		accountType = null; // is set in an upper level constructor
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

	@Override
	public AccountType getAccountType() {
		return this.accountType;
	}
}
