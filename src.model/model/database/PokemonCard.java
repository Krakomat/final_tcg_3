package model.database;

import java.util.ArrayList;
import java.util.List;

import model.enums.CardType;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class PokemonCard extends Card {

	private Element element;
	private int hitpoints;
	private Element weakness;
	private Element resistance;
	private List<Element> retreatCosts;
	private String evolvesFrom;

	private int damageMarks;
	private List<DynamicPokemonCondition> conditions;
	private Element currentWeakness;
	private Element currentResistance;
	/** True, when the opponent gets a price card after defeating this pokemon */
	private boolean priceValueable;

	private List<String> attackNames, pokemonPowerNames; // only used at the client. Are set, when this object is serialized

	public PokemonCard() {
		damageMarks = 0;
		conditions = new ArrayList<DynamicPokemonCondition>();
		retreatCosts = new ArrayList<Element>();
		setCardType(CardType.BASICPOKEMON);// Wert wird später geändert
		setElement(Element.COLORLESS);// Wert wird später geändert
		pokemonPowerNames = new ArrayList<>();
		attackNames = new ArrayList<>();
	}

	public PokemonCard(Card c) {
		super();
		damageMarks = 0;
		conditions = new ArrayList<DynamicPokemonCondition>();
		retreatCosts = new ArrayList<Element>();
		setCardType(CardType.BASICPOKEMON);// Wert wird später geändert
		setElement(Element.COLORLESS);// Wert wird später geändert
		this.cardId = c.getCardId();
		this.name = c.getName();
		this.imagePath = c.getImagePath();
		this.cardType = c.getCardType();
		this.rarity = c.getRarity();
		this.edition = c.getEdition();
		this.cardScript = c.getCardScript();
		pokemonPowerNames = new ArrayList<>();
		attackNames = new ArrayList<>();
	}

	@Override
	public Card copy() {
		PokemonCard c = (PokemonCard) super.copy();
		c.setElement(element);
		c.setHitpoints(hitpoints);
		c.setWeakness(weakness);
		c.setResistance(resistance);
		List<Element> eList = new ArrayList<>();
		for (Element element : this.retreatCosts)
			eList.add(element);
		c.setRetreatCosts(eList);
		c.setEvolvesFrom(evolvesFrom);
		c.setDamageMarks(damageMarks);
		c.setCurrentWeakness(currentWeakness);
		c.setCurrentResistance(currentResistance);
		c.setPriceValueable(priceValueable);
		List<String> aList = new ArrayList<>();
		for (String string : this.attackNames)
			aList.add(string);
		c.setAttackNames(aList);
		List<String> pList = new ArrayList<>();
		for (String string : this.pokemonPowerNames)
			pList.add(string);
		c.setPokemonPowerNames(pList);
		List<DynamicPokemonCondition> cList = new ArrayList<>();
		for (DynamicPokemonCondition condition : this.conditions)
			cList.add(condition.copy());
		c.setConditions(cList);

		return c;
	}

	public void resetDynamicAttributes() {
		damageMarks = 0;
		conditions = new ArrayList<DynamicPokemonCondition>();
		currentWeakness = weakness;
		currentResistance = resistance;
		((PokemonCardScript) this.cardScript).clearBlockedAttacks();
	}

	public ArrayList<PokemonCondition> nextTurn() {
		// Update conditions:
		ArrayList<PokemonCondition> curedConditions = new ArrayList<PokemonCondition>();
		int size = conditions.size();
		for (int i = size - 1; i >= 0; i--) {
			DynamicPokemonCondition condition = conditions.get(i);
			condition.setRemainingTurns(condition.getRemainingTurns() - 1);
			if (condition.getRemainingTurns() == 0) {
				conditions.remove(condition);
				curedConditions.add(condition.getCondition());
			}
		}
		return curedConditions;
	}

	/**
	 * @return Returns the element.
	 */
	public Element getElement() {
		return this.element;
	}

	/**
	 * @param element
	 *            The element to set.
	 */
	public void setElement(Element element) {
		this.element = element;
	}

	/**
	 * @return Returns the hitpoints.
	 */
	public int getHitpoints() {
		return this.hitpoints;
	}

	/**
	 * @param hitpoints
	 *            The hitpoints to set.
	 */
	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}

	/**
	 * @return Returns the weakness.
	 */
	public Element getWeakness() {
		return this.weakness;
	}

	/**
	 * @param weakness
	 *            The weakness to set.
	 */
	public void setWeakness(Element weakness) {
		this.weakness = weakness;
		this.currentWeakness = weakness;
	}

	/**
	 * @return Returns the resistance.
	 */
	public Element getResistance() {
		return this.resistance;
	}

	/**
	 * @param resistance
	 *            The resistance to set.
	 */
	public void setResistance(Element resistance) {
		this.resistance = resistance;
		this.currentResistance = resistance;
	}

	/**
	 * @return Returns the retreatCosts.
	 */
	public List<Element> getRetreatCosts() {
		return this.retreatCosts;
	}

	/**
	 * @param retreatCosts
	 *            The retreatCosts to set.
	 */
	public void setRetreatCosts(List<Element> retreatCosts) {
		this.retreatCosts = retreatCosts;
	}

	/**
	 * @return Returns the evolvesFrom.
	 */
	public String getEvolvesFrom() {
		return this.evolvesFrom;
	}

	/**
	 * @param evolvesFrom
	 *            The evolvesFrom to set.
	 */
	public void setEvolvesFrom(String evolvesFrom) {
		this.evolvesFrom = evolvesFrom;
	}

	/**
	 * @return Returns the damageMarks.
	 */
	public int getDamageMarks() {
		return this.damageMarks;
	}

	/**
	 * @param damageMarks
	 *            The damageMarks to set.
	 */
	public void setDamageMarks(int damageMarks) {
		this.damageMarks = damageMarks;
	}

	/**
	 * @return Returns the conditions.
	 */
	public List<DynamicPokemonCondition> getConditions() {
		return this.conditions;
	}

	/**
	 * @param conditions
	 *            The conditions to set.
	 */
	public void setConditions(List<DynamicPokemonCondition> conditions) {
		this.conditions = conditions;
	}

	public boolean hasCondition(PokemonCondition condition) {
		for (int i = 0; i < conditions.size(); i++) {
			if (conditions.get(i).getCondition().equals(condition))
				return true;
		}
		return false;
	}

	/**
	 * Returns true, if this pokemon is asleep, confused, paralyzed, poisoned or toxic.
	 * 
	 * @return
	 */
	public boolean hasNegativeCondition() {
		for (int i = 0; i < conditions.size(); i++) {
			if (conditions.get(i).getCondition().equals(PokemonCondition.ASLEEP) || conditions.get(i).getCondition().equals(PokemonCondition.CONFUSED)
					|| conditions.get(i).getCondition().equals(PokemonCondition.PARALYZED) || conditions.get(i).getCondition().equals(PokemonCondition.POISONED)
					|| conditions.get(i).getCondition().equals(PokemonCondition.TOXIC))
				return true;
		}
		return false;

	}

	/**
	 * Returns true if the pokemon can be retreated.
	 * 
	 * @param position
	 * @return
	 */
	public boolean getRetreatCostAvailable(Position position) {
		ArrayList<Element> onField = (ArrayList<Element>) position.getEnergy();
		@SuppressWarnings("unchecked")
		ArrayList<Element> copy = (ArrayList<Element>) onField.clone();
		ArrayList<Element> colors = new ArrayList<Element>();
		ArrayList<Element> colorless = new ArrayList<Element>();
		for (int i = 0; i < retreatCosts.size(); i++) {
			if (retreatCosts.get(i).equals(Element.COLORLESS))
				colorless.add(retreatCosts.get(i));
			else
				colors.add(retreatCosts.get(i));
		}

		for (int i = 0; i < colors.size(); i++) {
			Element e = colors.get(i);
			boolean found = false;
			for (int j = 0; j < copy.size(); j++) {
				if (e.equals(copy.get(j)) && !found) {
					copy.remove(j);
					found = true;
				}
			}
			if (!found)
				return false;
		}

		return colorless.size() <= copy.size();
	}

	public void cureCondition(PokemonCondition cond) {
		int size = conditions.size();
		for (int i = size - 1; i >= 0; i--) {
			DynamicPokemonCondition condition = conditions.get(i);
			if (condition.getCondition().equals(cond)) {
				conditions.remove(condition);
			}
		}
	}

	public boolean isPriceValueable() {
		return priceValueable;
	}

	public void setPriceValueable(boolean priceValueable) {
		this.priceValueable = priceValueable;
	}

	public Element getCurrentWeakness() {
		return currentWeakness;
	}

	public void setCurrentWeakness(Element currentWeakness) {
		this.currentWeakness = currentWeakness;
	}

	public Element getCurrentResistance() {
		return currentResistance;
	}

	public void setCurrentResistance(Element currentResistance) {
		this.currentResistance = currentResistance;
	}

	public List<String> getAttackNames() {
		return attackNames;
	}

	public void setAttackNames(List<String> attackNames) {
		this.attackNames = attackNames;
	}

	public List<String> getPokemonPowerNames() {
		return pokemonPowerNames;
	}

	public void setPokemonPowerNames(List<String> pokemonPowerNames) {
		this.pokemonPowerNames = pokemonPowerNames;
	}

}