package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Pair;
import common.utilities.Triple;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.game.PokemonGameModelImpl;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.CardScriptFactory;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00415_SabrinasAlakazam extends PokemonCardScript {

	public Script_00415_SabrinasAlakazam(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Mega Burn", att1Cost);

		this.addPokemonPower("Psylink");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);

		// Block attack:
		gameModel.getGameModelParameters().getBlockedAttacks().add(new Triple<Integer, String, Integer>(this.card.getGameID(), "Mega Burn", 3));
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (!PositionID.isActivePosition(getCurrentPositionID()))
			return false;
		if (this.getExecutableAttacks().isEmpty())
			return false;

		return super.pokemonPowerCanBeExecuted(powerName);
	}

	public void executePokemonPower(String powerName) {
		List<Pair<String, Integer>> executableAttacks = getExecutableAttacks();

		// Choose a pokemon to copy an attack from:
		List<PositionID> pokemonList = new ArrayList<>();
		for (Pair<String, Integer> pair : executableAttacks) {
			Card c = gameModel.getCard(pair.getValue());
			if (!pokemonList.contains(c.getCurrentPosition().getPositionID()))
				pokemonList.add(c.getCurrentPosition().getPositionID());
		}
		PositionID copyPokemonPos = getCardOwner().playerChoosesPositions(pokemonList, 1, true, "Choose a Pokemon to copy an attack from!").get(0);

		// Choose an attack to copy from the chosen pokemon:
		PokemonCard pokemon = (PokemonCard) gameModel.getPosition(copyPokemonPos).getTopCard();
		List<String> attacks = new ArrayList<>();
		List<Card> attackOwner = new ArrayList<>();
		PokemonCardScript script = (PokemonCardScript) CardScriptFactory.getInstance().createScript(pokemon.getCardId(), this.card, gameModel);
		for (String attack : script.getAttackNames()) {
			if (copiedAttackCanBeExecuted(attack, pokemon.getGameID())) {
				attacks.add(attack);
				attackOwner.add(pokemon);
			}
		}
		String attackName = getCardOwner().playerChoosesAttacks(attackOwner, attacks, 1, true, "Choose an attack to copy!").get(0);
		gameModel.sendCardMessageToAllPlayers(this.card.getName() + " copies " + attackName + " from " + pokemon.getName() + "!", pokemon, "");

		// Set card script:
		this.card.setCardScript(script);
		script.setCard(card);

		// Clone attack:
		((PokemonCardScript) this.card.getCardScript()).executeAttack(attackName);

		// Reset card script:
		this.card.setCardScript(this);

		// End turn:
		if (gameModel instanceof PokemonGameModelImpl)
			((PokemonGameModelImpl) gameModel).getPokemonGameManager().endTurn(getCardOwner());
	}

	private List<Pair<String, Integer>> getExecutableAttacks() {
		List<Pair<String, Integer>> erg = new ArrayList<>();
		for (PositionID posID : getPsychoCardsFromOwnField()) {
			PokemonCard pokemon = (PokemonCard) gameModel.getPosition(posID).getTopCard();

			PokemonCardScript script = (PokemonCardScript) CardScriptFactory.getInstance().createScript(pokemon.getCardId(), this.card, gameModel);

			for (String attack : script.getAttackNames()) {
				if (copiedAttackCanBeExecuted(attack, pokemon.getGameID()))
					erg.add(new Pair<String, Integer>(attack, pokemon.getGameID()));
			}
		}

		return erg;
	}

	private boolean copiedAttackCanBeExecuted(String attack, Integer pokemonGameID) {
		PokemonCard pokemon = (PokemonCard) gameModel.getCard(pokemonGameID);

		// Replace Card Script:
		PokemonCardScript script = (PokemonCardScript) CardScriptFactory.getInstance().createScript(pokemon.getCardId(), this.card, gameModel);
		this.card.setCardScript(script);

		boolean erg = script.attackCanBeExecuted(attack);

		// Restore Card Script:
		this.card.setCardScript(this);
		return erg;
	}

	private List<PositionID> getPsychoCardsFromOwnField() {
		List<PositionID> posList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullBenchPositions(getCardOwner().getColor())) {
			PokemonCard pCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (pCard.getElement() == Element.PSYCHIC)
				posList.add(posID);
		}
		return posList;
	}
}
