package model.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.game.GameParameters;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class Deck {

	private String name;
	private List<String> cards;

	public Deck() {
		this.name = "";
		cards = new ArrayList<String>();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getCards() {
		return this.cards;
	}

	public void setCards(List<String> cards) {
		this.cards = cards;
	}

	/**
	 * Adds the card with the given id to this deck.
	 * 
	 * @param c
	 */
	public void addCard(String c) {
		cards.add(c);
	}

	/**
	 * Removes the card with the given id from this deck.
	 * 
	 * @param c
	 */
	public void removeCard(String c) {
		cards.remove(c);
	}

	/**
	 * Returns the number of cards containing in this deck.
	 * 
	 * @return
	 */
	public int size() {
		return cards.size();
	}

	/**
	 * Returns the card id at the given index.
	 * 
	 * @param i
	 * @return
	 */
	public String get(int i) {
		return cards.get(i);
	}

	/**
	 * Saves the given Deck under the given path, creating a <deckname>.xml file.
	 * 
	 * @param path
	 */
	public void saveDeck(String path) {
		System.out.println("Saving Deck " + name + ".xml ...");
		Document dom = createDocument();
		createDOMTree(dom);
		printToFile(dom, path);
		System.out.println("Saving Deck finished!");
	}

	/**
	 * Shows a dialog window that lets the user choose a deck to load.
	 * 
	 * @return
	 */
	public static Deck loadDeckDialog() {
		File[] files = finder(GameParameters.DECK_PATH);
		Object[] possibilities = new Object[files.length];
		for (int i = 0; i < files.length; i++) {
			String file = files[i].getName();
			possibilities[i] = file.substring(0, file.length() - 4);
		}

		String s = (String) JOptionPane.showInputDialog(null, "Choose the deck to load:", "Load Deck", JOptionPane.PLAIN_MESSAGE, null, possibilities,
				possibilities[1]);
		if (s != null) {
			// Search index:
			int index = -1;
			for (int i = 0; i < files.length; i++) {
				String file = files[i].getName().substring(0, (files[i].getName().length() - 4));
				if (file.equals(s))
					index = i;
			}
			// Load deck
			File deckFile = files[index];
			Deck deck = Deck.readFromDatabaseFile(deckFile);
			return deck;
		}
		return null;
	}

	private static File[] finder(String dirName) {
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".xml");
			}
		});

	}

	public static Deck readFromDatabaseFile(File f) {
		// parse the xml file and get the dom object
		Document dom = parseXmlFile(f);

		// get each effect element and create a Effect object
		Deck d = parseDocument(dom);
		return d;
	}

	private static Document parseXmlFile(File f) {
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

	private static Deck parseDocument(Document dom) {
		// get the root elememt
		Element docEle = dom.getDocumentElement();

		Deck deck = new Deck();

		String name = docEle.getAttribute("Name");
		deck.setName(name);

		ArrayList<String> cards = parseCards((Element) docEle.getElementsByTagName("Cards").item(0));
		deck.setCards(cards);

		return deck;
	}

	private static ArrayList<String> parseCards(Element cardElements) {
		ArrayList<String> cards = new ArrayList<String>();
		NodeList cardList = cardElements.getElementsByTagName("Card");
		for (int i = 0; i < cardList.getLength(); i++) {

			// get the employee element
			Element el = (Element) cardList.item(i);

			// get the Employee object
			String id = el.getAttribute("ID");

			Integer cardCount = Integer.parseInt(getTextValue(el, "Size"));
			// add it to list
			for (int j = 0; j < cardCount; j++) {
				cards.add(id);
			}
		}
		return cards;
	}

	/**
	 * Using JAXP in implementation independent manner create a document object using which we create a xml tree in memory
	 */
	private Document createDocument() {

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
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
			System.exit(1);
		}
		return null;
	}

	/**
	 * The real workhorse which creates the XML structure
	 */
	private void createDOMTree(Document dom) {

		// create the root element <Books>
		Element rootEle = dom.createElement("Deck");
		rootEle.setAttribute("Name", name);
		dom.appendChild(rootEle);

		Element cardsElement = createCardsElement(dom);
		rootEle.appendChild(cardsElement);
	}

	private Element createCardsElement(Document dom) {
		Element cardsElement = dom.createElement("Cards");
		CardLibrary cardLibrary = new CardLibrary(cards);
		// Transform HashMap to Set:
		Set<String> keys = cardLibrary.getCardSizeMap().keySet();
		// Get the Iterator:
		Iterator<String> keyIterator = keys.iterator();

		// Traverse the Keyset:
		List<String> keyList = new ArrayList<>();
		while (keyIterator.hasNext())
			keyList.add(keyIterator.next());
		// Sort cards:
		keyList.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int i = Integer.parseInt(o1);
				int j = Integer.parseInt(o2);
				return i < j ? -1 : (i > j) ? 1 : 0;
			}
		});

		for (String cardId : keyList) {
			Element cardElement = dom.createElement("Card");

			cardElement.setAttribute("ID", cardId);

			Element sizeElement = dom.createElement("Size");
			sizeElement.appendChild(dom.createTextNode("" + cardLibrary.getCardSize(cardId)));
			cardElement.appendChild(sizeElement);

			cardsElement.appendChild(cardElement);
		}
		return cardsElement;
	}

	private void printToFile(Document dom, String path) {

		try {
			// print
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			// to generate output to console use this serializer
			// XMLSerializer serializer = new XMLSerializer(System.out, format);

			// to generate a file output use fileoutputstream instead of
			// system.out
			XMLSerializer serializer = new XMLSerializer(new FileOutputStream(new File(path + name + ".xml")), format);

			serializer.serialize(dom);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * I take a xml element and the tag name, look for the tag and get the text content i.e for <employee><name>John</name></employee> xml snippet if the Element
	 * points to employee node and tagName is name I will return John
	 * 
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			if (el.getFirstChild() != null)
				textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
}
