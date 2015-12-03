package model.scripting.abstracts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;

/**
 * Script for a pokemon card.
 * 
 * @author Michael
 *
 */
public abstract class PokemonCardScript extends CardScript implements Cloneable {
	private Map<String, List<Element>> attackCosts;
	private List<String> attackNames;
	protected List<String> pokemonPowers;
	private Map<String, Integer> blockedAttacks;

	public PokemonCardScript(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		this.attackCosts = new HashMap<String, List<Element>>();
		this.attackNames = new ArrayList<>();
		this.pokemonPowers = new ArrayList<>();
		this.blockedAttacks = new HashMap<>();
	}

	@Override
	public PlayerAction canBePlayedFromHand() {
		// Check if card is in players hand:
		if (!this.cardInHand())
			return null;

		// Check conditions for playing the card from the hand:
		Color color = this.card.getCurrentPosition().getColor();
		if (this.card.getCardType() == CardType.BASICPOKEMON) {
			if (!(this.gameModel.getFullBenchPositions(color).size() == 5))
				return PlayerAction.PUT_ON_BENCH;
		} else if (this.card.getCardType() == CardType.STAGE1POKEMON || this.card.getCardType() == CardType.STAGE2POKEMON) {
			if (this.gameModel.getTurnNumber() > 1 && !this.gameModel.getPositionsForEvolving((PokemonCard) this.card, color).isEmpty()
					&& (this.gameModel.getGameModelParameters().getPower_Active_00153_Aerodactyl().isEmpty()
							|| (!this.gameModel.getGameModelParameters().getPower_Active_00153_Aerodactyl().isEmpty()
									&& !this.gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())))
				return PlayerAction.EVOLVE_POKEMON;
		} else
			throw new IllegalArgumentException("Error: Wrong CardType for the card in canBePlayedFromHand() of PokemonCardScript: " + this.card.getCardType());
		return null;
	}

	@Override
	public void playFromHand() {
		// Get the player:
		Player player = null;
		if (this.card.getCurrentPosition().getColor() == Color.BLUE)
			player = gameModel.getPlayerBlue();
		else if (this.card.getCurrentPosition().getColor() == Color.RED)
			player = gameModel.getPlayerRed();
		else
			throw new IllegalArgumentException("Error: Wrong Color for the position of the card!");

		// Execute action:
		if (this.card.getCardType() == CardType.BASICPOKEMON) {
			// Put basic pokemon on bench:
			this.gameModel.getAttackAction().putBasicPokemonOnBench(player, (PokemonCard) card);
		} else if (this.card.getCardType() == CardType.STAGE1POKEMON || this.card.getCardType() == CardType.STAGE2POKEMON) {
			// Evolve pokemon:
			List<PositionID> posList = this.gameModel.getPositionsForEvolving((PokemonCard) this.card, player.getColor());
			PositionID chosenPosID = player.playerChoosesPositions(posList, 1, true, "Choose a position to evolve into " + card.getName()).get(0);
			this.gameModel.getAttackAction().evolvePokemon(chosenPosID, card.getGameID());
		} else
			throw new IllegalArgumentException("Error: Wrong CardType for the card in playFromHand() of PokemonCardScript: " + this.card.getCardType());
	}

	/**
	 * Returns true, if the given attack can be executed, namely if the cost of
	 * the attack are available at the card, the pokemon is not paralyzed and if
	 * the attack itself is not blocked.
	 * 
	 * @param attackName
	 * @return
	 */
	public boolean attackCanBeExecuted(String attackName) {
		// Check if attack is blocked:
		if (this.blockedAttacks.containsKey(attackName))
			return false;

		// If pokemon is paralyzed or asleep, the it cannot attack:
		PokemonCard pCard = (PokemonCard) card;
		if (pCard.hasCondition(PokemonCondition.PARALYZED) || pCard.hasCondition(PokemonCondition.ASLEEP))
			return false;

		List<Element> attackCosts = this.attackCosts.get(attackName);
		if (attackCosts == null)
			throw new IllegalArgumentException("Error: Wrong attack name in attackCanBeExecuted of PokemonCardScript: " + attackName);

		Position position = this.card.getCurrentPosition();
		if (position == null)
			throw new IllegalArgumentException("Error: Position of the card is null in attackCanBeExecuted of PokemonCardScript!");

		// Check if costs are available at the position:
		ArrayList<Element> onField = (ArrayList<Element>) position.getEnergy();
		@SuppressWarnings("unchecked")
		ArrayList<Element> copy = (ArrayList<Element>) onField.clone();
		ArrayList<Element> colors = new ArrayList<Element>();
		ArrayList<Element> colorless = new ArrayList<Element>();
		for (int i = 0; i < attackCosts.size(); i++) {
			if (attackCosts.get(i).equals(Element.COLORLESS))
				colorless.add(attackCosts.get(i));
			else
				colors.add(attackCosts.get(i));
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
			// Try to pay with rainbow energy:
			if (!found) {
				for (int j = 0; j < copy.size(); j++) {
					if (copy.get(j).equals(Element.RAINBOW) && !found) {
						copy.remove(j);
						found = true;
					}
				}
			}
			if (!found)
				return false;
		}

		return colorless.size() <= copy.size();
	}

	/**
	 * Returns true, if the pokemon can be retreated.
	 * 
	 * @param attackName
	 * @return
	 */
	public boolean retreatCanBeExecuted() {
		PokemonCard pCard = (PokemonCard) card;
		Position pos = pCard.getCurrentPosition();

		// Return false, if retreat was already executed in this turn:
		if (gameModel.getRetreatExecuted())
			return false;

		// Return false, if the pokemon is not an active pokemon:
		if (pos.getPositionID() != PositionID.BLUE_ACTIVEPOKEMON && pos.getPositionID() != PositionID.RED_ACTIVEPOKEMON)
			return false;

		// Return false, if there aren't bench pokemon to swap with:
		if (gameModel.getFullBenchPositions(this.getCardOwner().getColor()).isEmpty())
			return false;

		// A Pokemon that is Asleep or Paralyzed should not be allowed to
		// retreat:
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;

		int costs = pCard.getRetreatCosts().size();
		for (Card c : gameModel.getAllCards()) {
			if (c instanceof PokemonCard)
				costs = ((PokemonCardScript) c.getCardScript()).modifyRetreatCosts(costs, this.getCardOwner().getColor());
		}

		return pos.getEnergy().size() >= costs;
	}

	/**
	 * Is called whenever it is checked if a pokemon may retreat, as well before
	 * the retreat process is started. The user may modify the retreat costs(see
	 * Dodrios pokemon power).
	 * 
	 * @param retreatCosts
	 * @param color
	 * @return
	 */
	public int modifyRetreatCosts(int retreatCosts, Color color) {
		// Override this, when needed
		return retreatCosts;
	}

	/**
	 * Returns true, if the pokemon's power can be executed.
	 * 
	 * @param powerName
	 * @return
	 */
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		if (gameModel.getPlayerOnTurn().getColor() != this.getCardOwner().getColor())
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		return true;
	}

	/**
	 * Executes the given attack. No need to check, if it is allowed to execute
	 * the attack.
	 * 
	 * @param attackName
	 */
	public abstract void executeAttack(String attackName);

	/**
	 * Retreats the pokemon. No need to check, if it is allowed to execute
	 * retreat.
	 */
	public void executeRetreat() {
		PokemonCard pCard = (PokemonCard) card;
		// Get the player:
		Player player = null;
		if (this.card.getCurrentPosition().getColor() == Color.BLUE)
			player = gameModel.getPlayerBlue();
		else if (this.card.getCurrentPosition().getColor() == Color.RED)
			player = gameModel.getPlayerRed();
		else
			throw new IllegalArgumentException("Error: Wrong Color for the position of the card!");

		// Calculate & Pay costs:
		int costs = pCard.getRetreatCosts().size();
		// Call modifyRetreatCosts() on all pokemon cards in the game
		for (Card c : gameModel.getAllCards()) {
			if (c instanceof PokemonCard)
				costs = ((PokemonCardScript) c.getCardScript()).modifyRetreatCosts(costs, this.getCardOwner().getColor());
		}
		if (costs > 0)
			gameModel.getAttackAction().playerPaysEnergy(player, pCard.getRetreatCosts(),
					player.getColor() == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON);

		boolean retreatAllowed = true;
		// If active pokemon is confused check if it can be retreated by
		// flipping a coin:
		if (pCard.hasCondition(PokemonCondition.CONFUSED)) {
			gameModel.sendTextMessageToAllPlayers("Coinflip: " + pCard.getName() + " can't return when tails", "");
			Coin c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.TAILS)
				retreatAllowed = false;
		}

		if (retreatAllowed) {
			// Choose bench position:
			PositionID chosenPosition = player.playerChoosesPositions(gameModel.getFullBenchPositions(player.getColor()), 1, true, "Choose a new active pokemon!").get(0);

			// Swap pokemon:
			Card newActive = gameModel.getPosition(chosenPosition).getTopCard();
			List<Card> cardList = new ArrayList<>();
			cardList.add(pCard);
			cardList.add(newActive);
			gameModel.setRetreatExecuted(true);
			gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + pCard.getName() + " with " + newActive.getName(), cardList, "");
			gameModel.getAttackAction().swapPokemon(pCard.getCurrentPosition().getPositionID(), chosenPosition);
			gameModel.sendGameModelToAllPlayers("");

			// Check all scripts, if there is any interaction after retreating a
			// pokemon (see 00205_DarkDugrtrio):
			for (Card c : gameModel.getAllCards()) {
				if (c instanceof PokemonCard)
					((PokemonCardScript) c.getCardScript()).pokemonRetreated(pCard.getCurrentPosition().getPositionID());
			}

		} else
			gameModel.sendTextMessageToAllPlayers("Retreat failed!", "");
	}

	/**
	 * Executes the given Pokemon power. No need to check, if it is allowed to
	 * execute the pokemon power.
	 * 
	 * @param powerName
	 */
	public void executePokemonPower(String powerName) {
		// Only override this, when needed
	}

	/**
	 * Registers a new Pokemon power.
	 * 
	 * @param powerName
	 */
	protected void addPokemonPower(String powerName) {
		this.pokemonPowers.add(powerName);
	}

	/**
	 * Registers a new attack.
	 * 
	 * @param attName
	 * @param attCosts
	 */
	protected void addAttack(String attName, List<Element> attCosts) {
		if (!this.attackNames.contains(attName)) {
			this.attackCosts.put(attName, attCosts);
			this.attackNames.add(attName);
		} else
			throw new IllegalArgumentException("Error: Attack " + attName + " is already contained in this script!");
	}

	/**
	 * Returns the position in the attackList of the given attack name. Returns
	 * -1, if the name is not contained in the list.
	 * 
	 * @param attName
	 * @return
	 */
	public int getAttackNumber(String attName) {
		return this.attackNames.indexOf(attName);
	}

	/**
	 * Returns the attack costs for the given attack. Returns null, if a wrong
	 * attack name was given to this script.
	 * 
	 * @param attackName
	 * @return
	 */
	public List<Element> getAttackCosts(String attackName) {
		if (!this.attackCosts.containsKey(attackName))
			return null;
		List<Element> costs = new ArrayList<>();
		for (Element element : this.attackCosts.get(attackName))
			costs.add(element);
		return costs;
	}

	/**
	 * Returns the available attack names of this script.
	 * 
	 * @return
	 */
	public List<String> getAttackNames() {
		return attackNames;
	}

	/**
	 * Returns the position in the pokemonPowerList of the given PokemonPower
	 * name. Returns -1, if the name is not contained in the list.
	 * 
	 * @param attName
	 * @return
	 */
	public int getPokemonPowerNumber(String attName) {
		return this.pokemonPowers.indexOf(attName);
	}

	/**
	 * Returns the available pokemon power names of this script.
	 * 
	 * @return
	 */
	public List<String> getPokemonPowerNames() {
		return pokemonPowers;
	}

	/**
	 * Blocks the given attack for the given amount of rounds.
	 * 
	 * @param chosenAttack
	 * @param rounds
	 */
	public void blockAttack(String chosenAttack, int rounds) {
		blockedAttacks.put(chosenAttack, rounds);
	}

	/**
	 * Unblocks all blocked attacks.
	 */
	public void clearBlockedAttacks() {
		this.blockedAttacks = new HashMap<>();
	}

	/**
	 * Decreases the amount of rounds attacks are blocked by 1. Removes blocked
	 * attacks, if the round number is 0.
	 */
	public void decreaseBlockingTimeForAttacks() {
		List<String> removedAttacks = new ArrayList<>();
		Map<String, Integer> replacedAttacks = new HashMap<>();

		for (String att : blockedAttacks.keySet()) {
			int rounds = blockedAttacks.get(att);
			rounds = rounds - 1;
			if (rounds == 0)
				removedAttacks.add(att);
			else
				replacedAttacks.put(att, rounds);
		}

		for (String att : removedAttacks)
			this.blockedAttacks.remove(att);

		for (String att : replacedAttacks.keySet())
			this.blockedAttacks.replace(att, replacedAttacks.get(att));
	}

	/**
	 * Is called when the owner pokemon of this script is attacked.
	 * 
	 * @param turnNumber
	 * @param damage
	 * @param source
	 *            position of the pokemon that attacked this pokemon, may be
	 *            null!
	 */
	public void pokemonIsDamaged(int turnNumber, int damage, PositionID source) {
		// Override when needed!
	}

	/**
	 * Is called whenever an arbitrary pokemon is going to receive the given
	 * amount of damage. Some pokemon powers may be able to reduce incoming
	 * damage.
	 * 
	 * @param damage
	 * @param attacker
	 */
	public int modifyIncomingDamage(int damage, Card attacker, PositionID defender) {
		// Override when needed!
		return damage;
	}

	/**
	 * Is called when the owner pokemon of this script is going to apply the
	 * given amount of damage. Some pokemons powers may be able to increasy
	 * outgoing damage.
	 * 
	 * @param damageAmount
	 */
	public int modifyOutgoingDamage(int damageAmount) {
		// Override when needed!
		return damageAmount;
	}

	/**
	 * Is called when the owner pokemon of this script got a condition.
	 * 
	 * @param turnNumberm
	 * @param condition
	 */
	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		// Override when needed!
	}

	/**
	 * Is called when the owner pokemon of this script got its conditions
	 * removed.
	 * 
	 * @param turnNumber
	 * @param condition
	 */
	public void pokemonGotConditionsRemoved(int turnNumber) {
		// Override when needed!
	}

	/**
	 * Is called whenever a pokemon retreated!
	 * 
	 * @param newPositionOfRetreatedPkmn
	 */
	public void pokemonRetreated(PositionID newPositionOfRetreatedPkmn) {
		// Override when needed!
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Is called BEFORE the given pokemon is going to receive a condition.
	 * Returns true, if the given condition is allowed to be applied or false,
	 * if the condition will not be applied.
	 * 
	 * @param condition
	 * @return
	 */
	public boolean allowIncomingCondition(PokemonCondition condition) {
		// Override when needed!
		return true;
	}
}