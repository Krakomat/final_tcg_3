package network.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import model.database.Card;
import model.database.CardLibrary;
import model.database.Database;
import model.database.Deck;
import model.enums.AccountType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ai.interfaces.BotBorder;
import arenaMode.model.ArenaFighterCode;

public class AccountStorageHelper {

	static Document parseXmlFile(File f) {
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(f.getAbsolutePath());
			return dom;
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}

	static ArrayList<Player> parseDocument(Document dom) {
		ArrayList<Player> accList = new ArrayList<Player>();

		// get the root elememt
		Element docEle = dom.getDocumentElement();

		// get a nodelist of <Accounts> elements
		NodeList accountNodes = docEle.getElementsByTagName("Account");
		if (accountNodes != null && accountNodes.getLength() > 0) {
			for (int i = 0; i < accountNodes.getLength(); i++) {
				Player a = parseAccount((Element) accountNodes.item(i));
				accList.add(a);
			}
		}
		return accList;
	}

	static Player parseSingleDocument(Document dom) {
		// get the root elememt
		Element docEle = dom.getDocumentElement();
		Player a = parseAccount(docEle);
		return a;
	}

	static Player parseAccount(Element parentElement) {
		if (parentElement != null) {
			long id = Long.valueOf(parentElement.getAttribute("ID"));
			String name = getTextValue(parentElement, "Name");
			String password = getTextValue(parentElement, "Password");
			String avatar = getTextValue(parentElement, "Avatar");
			String prizeCards = getTextValue(parentElement, "PrizeCards");
			AccountType accType = AccountType.valueOf(getTextValue(parentElement, "AccountType"));

			Deck deck = new Deck();

			Element deckElement = (Element) parentElement.getElementsByTagName("Deck").item(0);
			String deckName = deckElement.getAttribute("Name");
			deck.setName(deckName);
			deck.setCards(cardListToStringList(parseCards(deckElement)));

			Player acc = null;
			switch (accType) {
			case BOT_DUMMY:
				acc = BotBorder.createBot(id, name, password, avatar, Integer.parseInt(prizeCards), AccountType.BOT_DUMMY);
				break;
			case REAL_PLAYER:
				acc = PlayerImpl.createNewPlayer(id, name, password, avatar, Integer.parseInt(prizeCards));
				break;
			case BOT_TREE:
				acc = BotBorder.createBot(id, name, password, avatar, Integer.parseInt(prizeCards), AccountType.BOT_TREE);
				break;
			default:
				System.err.println("Wrong AccountType in method parseAccount() of class AccountStorageHelper");
				break;
			}
			acc.setDeck(deck);

			List<ArenaFighterCode> defeatedFighters = new ArrayList<>();
			Element arenaFighterElement = (Element) parentElement.getElementsByTagName("DefeatedArenaFighters").item(0);
			defeatedFighters = parseDefeatedFighters(arenaFighterElement);
			acc.setDefeatedArenaFighters(defeatedFighters);

			List<String> unlockedCards = new ArrayList<>();
			Element unlockedCardsElement = (Element) parentElement.getElementsByTagName("UnlockedCards").item(0);
			unlockedCards = parseUnlockedCards(unlockedCardsElement);
			acc.setUnlockedCards(unlockedCards);

			return acc;
		} else
			return null;
	}

	private static List<ArenaFighterCode> parseDefeatedFighters(Element arenaFighterElement) {
		List<ArenaFighterCode> arenaFighterCodes = new ArrayList<>();
		NodeList fighterList = arenaFighterElement.getElementsByTagName("DefeatedFighter");
		for (int i = 0; i < fighterList.getLength(); i++) {

			// get the employee element
			Element el = (Element) fighterList.item(i);

			// get the Employee object
			String id = el.getAttribute("ID");
			ArenaFighterCode code = ArenaFighterCode.valueOf(id);
			arenaFighterCodes.add(code);
		}
		return arenaFighterCodes;
	}

	private static List<String> parseUnlockedCards(Element unlockedCardsElement) {
		List<String> unlockedCards = new ArrayList<>();
		NodeList cardList = unlockedCardsElement.getElementsByTagName("UnlockedCard");
		for (int i = 0; i < cardList.getLength(); i++) {

			// get the employee element
			Element el = (Element) cardList.item(i);

			// get the Employee object
			String id = el.getAttribute("ID");
			unlockedCards.add(id);
		}
		return unlockedCards;
	}

	/**
	 * I take a xml element and the tag name, look for the tag and get the text content i.e for <employee><name>John</name></employee> xml snippet if the Element points to employee
	 * node and tagName is name I will return John
	 * 
	 * @param ele
	 * @param tagName
	 * @return
	 */
	static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			if (el.getFirstChild() != null)
				textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	static ArrayList<String> cardListToStringList(ArrayList<Card> cList) {
		ArrayList<String> sList = new ArrayList<String>();
		for (int i = 0; i < cList.size(); i++) {
			sList.add(cList.get(i).getCardId());
		}
		return sList;
	}

	static ArrayList<Card> parseCards(Element cardElements) {
		ArrayList<Card> cards = new ArrayList<Card>();
		NodeList cardList = cardElements.getElementsByTagName("Card");
		for (int i = 0; i < cardList.getLength(); i++) {

			// get the employee element
			Element el = (Element) cardList.item(i);

			// get the Employee object
			String id = el.getAttribute("ID");

			Integer cardCount = Integer.parseInt(AccountStorageHelper.getTextValue(el, "Size"));
			// add it to list
			for (int j = 0; j < cardCount; j++) {
				Card card = Database.createCard(id);
				cards.add(card);
			}
		}
		return cards;
	}

	/**
	 * Using JAXP in implementation independent manner create a document object using which we create a xml tree in memory
	 */
	static Document createDocument() {

		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// create an instance of DOM
			Document dom = db.newDocument();
			return dom;
		} catch (ParserConfigurationException pce) {
			// dump it
			System.err.println("Error while trying to instantiate DocumentBuilder " + pce);
			System.exit(1);
		}
		return null;
	}

	/**
	 * The real workhorse which creates the XML structure
	 */
	static void createDOMTree(List<Player> accList, Document dom) {

		// create the root element <Books>
		Element rootEle = dom.createElement("Accounts");
		dom.appendChild(rootEle);

		// No enhanced for
		for (int i = 0; i < accList.size(); i++) {
			Account acc = accList.get(i);
			Element effEle = createAccountElement(acc, dom);
			rootEle.appendChild(effEle);
		}
	}

	/**
	 * The real workhorse which creates the XML structure
	 */
	static void createDOMTree(Account account, Document dom) {
		Element effEle = createAccountElement(account, dom);
		dom.appendChild(effEle);
	}

	static Element createAccountElement(Account acc, Document dom) {
		Element element = dom.createElement("Account");
		element.setAttribute("ID", "" + acc.getID());

		Element nameElement = dom.createElement("Name");
		nameElement.appendChild(dom.createTextNode(acc.getName()));
		element.appendChild(nameElement);

		Element passwordElement = dom.createElement("Password");
		passwordElement.appendChild(dom.createTextNode(acc.getPassword()));
		element.appendChild(passwordElement);

		Element avatarElement = dom.createElement("Avatar");
		avatarElement.appendChild(dom.createTextNode(acc.getAvatarPath()));
		element.appendChild(avatarElement);

		Element prizeCardsElement = dom.createElement("PrizeCards");
		prizeCardsElement.appendChild(dom.createTextNode(String.valueOf(acc.getPrizeCards())));
		element.appendChild(prizeCardsElement);

		Element accTypeElement = dom.createElement("AccountType");
		accTypeElement.appendChild(dom.createTextNode(acc.getAccountType().toString()));
		element.appendChild(accTypeElement);

		Element deckElement = dom.createElement("Deck");
		deckElement.setAttribute("Name", acc.getDeck().getName());

		CardLibrary deckLibrary = new CardLibrary(acc.getDeck().getCards());
		// Transform HashMap to Set:
		Set<String> deckKeys = deckLibrary.getCardSizeMap().keySet();
		// Get the Iterator:
		Iterator<String> deckKeyIterator = deckKeys.iterator();

		// Traverse the Keyset:
		while (deckKeyIterator.hasNext()) {
			String cardId = deckKeyIterator.next();
			Element cardElement = dom.createElement("Card");

			cardElement.setAttribute("ID", cardId);

			Element sizeElement = dom.createElement("Size");
			sizeElement.appendChild(dom.createTextNode("" + deckLibrary.getCardSize(cardId)));
			cardElement.appendChild(sizeElement);

			deckElement.appendChild(cardElement);
		}
		element.appendChild(deckElement);

		Element defeatedFightersElement = dom.createElement("DefeatedArenaFighters");
		for (ArenaFighterCode code : acc.getDefeatedArenaFighters()) {
			Element defeatedFighter = dom.createElement("DefeatedFighter");
			defeatedFighter.setAttribute("ID", code.toString());
			defeatedFightersElement.appendChild(defeatedFighter);
		}
		element.appendChild(defeatedFightersElement);

		Element unlockedCardsElement = dom.createElement("UnlockedCards");
		for (String cardID : acc.getUnlockedCards()) {
			Element unlockedCard = dom.createElement("UnlockedCard");
			unlockedCard.setAttribute("ID", cardID);
			unlockedCardsElement.appendChild(unlockedCard);
		}
		element.appendChild(unlockedCardsElement);

		return element;
	}

	static void printToFile(Document dom, String accountDataPath) {
		Transformer transformer = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
		Result output = new StreamResult(new File(accountDataPath));
		Source input = new DOMSource(dom);

		try {
			transformer.transform(input, output);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

}
