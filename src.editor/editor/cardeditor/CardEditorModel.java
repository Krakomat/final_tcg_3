package editor.cardeditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Edition;
import model.enums.Rarity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class CardEditorModel {

	private ArrayList<Card> allCards;
	private Document dom;

	public static void decreaseCardID(Card c) {
		String id = c.getCardId();
		Integer idAsInt = Integer.parseInt(id);
		idAsInt--;
		id = String.valueOf(idAsInt);
		if ((idAsInt / 10000) == 0)
			id = "0" + id;
		if ((idAsInt) / 1000 == 0)
			id = "0" + id;
		if ((idAsInt / 100) == 0)
			id = "0" + id;
		if ((idAsInt / 10) == 0)
			id = "0" + id;

		c.setCardId(id);
	}

	public CardEditorModel() {
		allCards = new ArrayList<Card>();
	}

	public void readFromDatabaseFile(File f) throws IOException {
		// parse the xml file and get the dom object
		parseXmlFile(f);

		// get each effect element and create a Effect object
		parseDocument();
		Collections.sort(allCards);
	}

	private void parseXmlFile(File f) {
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(f.getAbsolutePath());

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void parseDocument() {
		allCards = new ArrayList<Card>();

		// get the root elememt
		Element docEle = dom.getDocumentElement();

		// get a nodelist of <PokemonCard> elements
		NodeList pokemonCardNodes = docEle.getElementsByTagName("PokemonCard");
		if (pokemonCardNodes != null && pokemonCardNodes.getLength() > 0) {
			for (int i = 0; i < pokemonCardNodes.getLength(); i++) {

				// get the employee element
				Element pCardElement = (Element) pokemonCardNodes.item(i);

				// get the Employee object
				PokemonCard p = parsePokemonCard(pCardElement);

				// add it to list
				allCards.add(p);
			}
		}

		// get a nodelist of <TrainerCard> elements
		NodeList trainerCardNodes = docEle.getElementsByTagName("TrainerCard");
		if (trainerCardNodes != null && trainerCardNodes.getLength() > 0) {
			for (int i = 0; i < trainerCardNodes.getLength(); i++) {

				// get the employee element
				Element pCardElement = (Element) trainerCardNodes.item(i);

				// get the Employee object
				TrainerCard p = parseTrainer(pCardElement);

				// add it to list
				allCards.add(p);
			}
		}

		// get a nodelist of <EnergyCard> elements
		NodeList energyCardNodes = docEle.getElementsByTagName("EnergyCard");
		if (energyCardNodes != null && energyCardNodes.getLength() > 0) {
			for (int i = 0; i < energyCardNodes.getLength(); i++) {

				// get the employee element
				Element pCardElement = (Element) energyCardNodes.item(i);

				// get the Employee object
				EnergyCard p = parseEnergy(pCardElement);

				// add it to list
				allCards.add(p);
			}
		}
	}

	private Card parseCard(Element parentElement) {
		Element cardElement = (Element) parentElement.getElementsByTagName("Card").item(0);
		if (cardElement != null) {
			Card c = new Card();
			c.setCardId(getTextValue(cardElement, "ID"));
			c.setName(getTextValue(cardElement, "Name"));
			c.setImagePath(getTextValue(cardElement, "Image"));
			c.setRarity(Rarity.valueOf(getTextValue(cardElement, "Rarity")));
			c.setCardType(CardType.valueOf(getTextValue(cardElement, "CardType")));
			c.setEdition(Edition.valueOf(getTextValue(cardElement, "Edition")));
			return c;
		} else
			return null;
	}

	private EnergyCard parseEnergy(Element eCardElement) {
		EnergyCard energyCard = new EnergyCard(parseCard(eCardElement));
		energyCard.setBasisEnergy(Boolean.valueOf(getTextValue(eCardElement, "BasicEnergy")));

		ArrayList<model.enums.Element> energyList = new ArrayList<model.enums.Element>();
		NodeList providedEnergyList = eCardElement.getElementsByTagName("ProvidedEnergy");
		for (int i = 0; i < providedEnergyList.getLength(); i++) {
			Element el = (Element) providedEnergyList.item(i);
			NodeList energy = el.getElementsByTagName("Element");
			for (int j = 0; j < energy.getLength(); j++) {
				model.enums.Element element = model.enums.Element.valueOf(energy.item(j).getTextContent());
				energyList.add(element);
			}
		}
		energyCard.setProvidedEnergy(energyList);

		return energyCard;
	}

	private TrainerCard parseTrainer(Element tCardElement) {
		TrainerCard trainerCard = new TrainerCard(parseCard(tCardElement));

		return trainerCard;
	}

	private PokemonCard parsePokemonCard(Element pCardElement) {
		PokemonCard pokemonCard = new PokemonCard(parseCard(pCardElement));

		pokemonCard.setElement(model.enums.Element.valueOf(getTextValue(pCardElement, "Type")));
		pokemonCard.setHitpoints(Integer.valueOf(getTextValue(pCardElement, "HP")));
		String weaknessString = getTextValue(pCardElement, "Weakness");
		if (weaknessString != null)
			pokemonCard.setWeakness(model.enums.Element.valueOf(weaknessString));
		else
			pokemonCard.setWeakness(null);

		String resistanceString = getTextValue(pCardElement, "Resistance");
		if (resistanceString != null)
			pokemonCard.setResistance(model.enums.Element.valueOf(resistanceString));
		else
			pokemonCard.setResistance(null);

		String evolvesFromString = getTextValue(pCardElement, "EvolvesFrom");
		if (evolvesFromString != null)
			pokemonCard.setEvolvesFrom(evolvesFromString);
		else
			pokemonCard.setEvolvesFrom("");

		ArrayList<model.enums.Element> energyList = new ArrayList<model.enums.Element>();
		NodeList retreatCostList = pCardElement.getElementsByTagName("RetreatCost");
		for (int i = 0; i < retreatCostList.getLength(); i++) {
			Element el = (Element) retreatCostList.item(i);
			NodeList energy = el.getElementsByTagName("Element");
			for (int j = 0; j < energy.getLength(); j++) {
				model.enums.Element element = model.enums.Element.valueOf(energy.item(j).getTextContent());
				energyList.add(element);
			}
		}
		pokemonCard.setRetreatCosts(energyList);

		return pokemonCard;
	}

	public void printInFile(ArrayList<Card> extract) {
		createDocument();
		createDOMTree(extract);
		printToFile();
	}

	/**
	 * Using JAXP in implementation independent manner create a document object using which we create a xml tree in memory
	 */
	private void createDocument() {

		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// create an instance of DOM
			dom = db.newDocument();

		} catch (ParserConfigurationException pce) {
			// dump it
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
			System.exit(1);
		}
	}

	/**
	 * The real workhorse which creates the XML structure
	 */
	private void createDOMTree(ArrayList<Card> extract) {

		// create the root element <Books>
		Element rootEle = dom.createElement("Cards");
		dom.appendChild(rootEle);

		// No enhanced for
		for (int i = 0; i < extract.size(); i++) {
			Card card = extract.get(i);
			if (card instanceof PokemonCard) {
				Element effEle = createPokemonCardElement((PokemonCard) card);
				rootEle.appendChild(effEle);
			} else if (card instanceof TrainerCard) {
				Element effEle = createTrainerCardElement((TrainerCard) card);
				rootEle.appendChild(effEle);
			} else {
				Element effEle = createEnergyCardElement((EnergyCard) card);
				rootEle.appendChild(effEle);
			}
		}
	}

	private Element createEnergyCardElement(EnergyCard card) {
		Element element = dom.createElement("EnergyCard");
		element.appendChild(createCardElement(card));

		Element basicEnergyElement = dom.createElement("BasicEnergy");
		basicEnergyElement.appendChild(dom.createTextNode(String.valueOf(card.isBasisEnergy())));
		element.appendChild(basicEnergyElement);

		Element providedEnergyElement = dom.createElement("ProvidedEnergy");
		for (int i = 0; i < card.getProvidedEnergy().size(); i++) {
			Element energy = dom.createElement("Element");
			Text energyText = dom.createTextNode(String.valueOf(card.getProvidedEnergy().get(i).toString()));
			energy.appendChild(energyText);
			providedEnergyElement.appendChild(energy);
		}
		element.appendChild(providedEnergyElement);

		return element;
	}

	private Element createTrainerCardElement(TrainerCard card) {
		Element element = dom.createElement("TrainerCard");
		element.appendChild(createCardElement(card));
		return element;
	}

	private Element createPokemonCardElement(PokemonCard card) {
		Element element = dom.createElement("PokemonCard");
		element.appendChild(createCardElement(card));

		Element pokemonTypeElement = dom.createElement("Type");
		pokemonTypeElement.appendChild(dom.createTextNode(card.getElement().toString()));
		element.appendChild(pokemonTypeElement);

		Element hitpointsElement = dom.createElement("HP");
		hitpointsElement.appendChild(dom.createTextNode(String.valueOf(card.getHitpoints())));
		element.appendChild(hitpointsElement);

		Element weaknessTypeElement = dom.createElement("Weakness");
		if (card.getWeakness() != null)
			weaknessTypeElement.appendChild(dom.createTextNode(card.getWeakness().toString()));
		else
			weaknessTypeElement.appendChild(dom.createTextNode(""));
		element.appendChild(weaknessTypeElement);

		Element resistanceTypeElement = dom.createElement("Resistance");
		if (card.getResistance() != null)
			resistanceTypeElement.appendChild(dom.createTextNode(card.getResistance().toString()));
		else
			resistanceTypeElement.appendChild(dom.createTextNode(""));
		element.appendChild(resistanceTypeElement);

		Element retreatCostElement = dom.createElement("RetreatCost");
		for (int i = 0; i < card.getRetreatCosts().size(); i++) {
			Element energy = dom.createElement("Element");
			Text energyText = dom.createTextNode(String.valueOf(card.getRetreatCosts().get(i).toString()));
			energy.appendChild(energyText);
			retreatCostElement.appendChild(energy);
		}
		element.appendChild(retreatCostElement);

		Element evolvesFromElement = dom.createElement("EvolvesFrom");
		evolvesFromElement.appendChild(dom.createTextNode(card.getEvolvesFrom()));
		element.appendChild(evolvesFromElement);

		return element;
	}

	private Element createCardElement(Card card) {
		Element element = dom.createElement("Card");

		Element nameElement = dom.createElement("Name");
		nameElement.appendChild(dom.createTextNode(card.getName()));
		element.appendChild(nameElement);

		Element idElement = dom.createElement("ID");
		idElement.appendChild(dom.createTextNode(card.getCardId()));
		element.appendChild(idElement);

		Element imagePathElement = dom.createElement("Image");
		imagePathElement.appendChild(dom.createTextNode(card.getImagePath()));
		element.appendChild(imagePathElement);

		Element rarityElement = dom.createElement("Rarity");
		rarityElement.appendChild(dom.createTextNode(card.getRarity().toString()));
		element.appendChild(rarityElement);

		Element cardTypeElement = dom.createElement("CardType");
		cardTypeElement.appendChild(dom.createTextNode(card.getCardType().toString()));
		element.appendChild(cardTypeElement);

		Element editionElement = dom.createElement("Edition");
		editionElement.appendChild(dom.createTextNode(card.getEdition().toString()));
		element.appendChild(editionElement);

		return element;
	}

	private void printToFile() {

		try {
			// print
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			// to generate output to console use this serializer
			// XMLSerializer serializer = new XMLSerializer(System.out, format);

			// to generate a file output use fileoutputstream instead of
			// system.out
			XMLSerializer serializer = new XMLSerializer(new FileOutputStream(new File("data/database.xml")), format);

			serializer.serialize(dom);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public void createCardID(Card c) {
		int size = this.allCards.size() + 1;
		String id = String.valueOf(size);
		if ((size / 10000) == 0)
			id = "0" + id;
		if ((size) / 1000 == 0)
			id = "0" + id;
		if ((size / 100) == 0)
			id = "0" + id;
		if ((size / 10) == 0)
			id = "0" + id;
		c.setCardId(id);
	}

	public String[] getPokemonNames() {
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < allCards.size(); i++) {
			if (allCards.get(i) instanceof PokemonCard && !temp.contains(allCards.get(i).getName()))
				temp.add(allCards.get(i).getName());
		}
		Collections.sort(temp);
		String[] s = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			s[i] = temp.get(i);
		}
		return s;
	}

	/**
	 * @return Returns the allCards.
	 */
	public ArrayList<Card> getAllCards() {
		return this.allCards;
	}

	/**
	 * @param allCards
	 *            The allCards to set.
	 */
	public void setAllCards(ArrayList<Card> allCards) {
		this.allCards = allCards;
	}

	public String[] getAllCardsAsString() {
		String[] s = new String[allCards.size()];
		for (int i = 0; i < allCards.size(); i++) {
			s[i] = allCards.get(i).toString();
		}
		return s;
	}

	/**
	 * I take a xml element and the tag name, look for the tag and get the text content i.e for <employee><name>John</name></employee> xml snippet if the Element
	 * points to employee node and tagName is name I will return John
	 * 
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element ele, String tagName) {
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
