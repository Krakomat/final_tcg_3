package network.client;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import arenaMode.model.ArenaFighterCode;
import model.database.Database;
import model.database.Deck;
import model.enums.AccountType;

/**
 * Interface for an account. All account specific information are stored here.
 * 
 * @author Michael
 *
 */
public interface Account {
	/**
	 * Sets the deck of the account.
	 * 
	 * @param deck
	 */
	public void setDeck(Deck deck);

	/**
	 * Returns the id of the account.
	 * 
	 * @return
	 */
	public long getID();

	/**
	 * Returns the name of the account.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Returns the password of the account.
	 * 
	 * @return
	 */
	public String getPassword();

	/**
	 * Returns the deck of this account.
	 * 
	 * @return
	 */
	public Deck getDeck();

	/**
	 * Returns the type of this account.
	 * 
	 * @return
	 */
	public AccountType getAccountType();

	public int getPrizeCards();

	public void setPriceCards(int number);

	public List<ArenaFighterCode> getDefeatedArenaFighters();

	public void setDefeatedArenaFighters(List<ArenaFighterCode> defeatedArenaFighters);

	public List<String> getUnlockedCards();

	public void setUnlockedCards(List<String> unlockedCards);

	public String getAvatarPath();

	/**
	 * Reads the given folder and returns a list of accounts, that this file stores.
	 * 
	 * @param path
	 *            folder that contains account files
	 * @return
	 */
	public static List<Player> readFromDatabaseFile(String path) {
		List<Player> playerList = new ArrayList<>();
		File[] files = finder(path);

		for (int i = 0; i < files.length; i++) {
			// parse the xml file and get the dom object
			Document dom = AccountStorageHelper.parseXmlFile(files[i]);

			// get each effect element and create a Effect object
			Player account = AccountStorageHelper.parseSingleDocument(dom);
			playerList.add(account);
		}
		return playerList;
	}

	static File[] finder(String dirName) {
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".xml");
			}
		});

	}

	public static void saveAccount(Account account) {
		Document dom = AccountStorageHelper.createDocument();
		AccountStorageHelper.createDOMTree(account, dom);
		AccountStorageHelper.printToFile(dom, Database.ACCOUNT_FOLDER + account.getName() + ".xml");
	}

	public static Player loadAccount(String fileName) {
		File f = new File(Database.ACCOUNT_FOLDER + fileName);
		// parse the xml file and get the dom object
		Document dom = AccountStorageHelper.parseXmlFile(f);

		// get each effect element and create a Effect object
		Player account = AccountStorageHelper.parseSingleDocument(dom);
		return account;
	}
}
