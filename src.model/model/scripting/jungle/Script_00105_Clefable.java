package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00105_Clefable extends PokemonCardScript {

	public Script_00105_Clefable(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Metronome", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Minimize", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Metronome"))
			this.metronom();
		else
			this.minimize();
	}

	private void metronom() {
		Player player = this.getCardOwner();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		PokemonCardScript defendingCardScript = (PokemonCardScript) defendingPokemon.getCardScript();
		PokemonCardScript copy = null;
		try {
			copy = (PokemonCardScript) defendingCardScript.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		// Choose attack:
		List<Card> attackOwner = new ArrayList<>();
		for (int i = 0; i < defendingCardScript.getAttackNames().size(); i++)
			attackOwner.add(defendingPokemon);

		String attackName = player.playerChoosesAttacks(attackOwner, defendingCardScript.getAttackNames(), 1, true, "Choose an attack to copy!").get(0);
		gameModel.sendTextMessageToAllPlayers(player.getName() + " chooses " + attackName + "!", "");

		// Set card script for piepi:
		this.card.setCardScript(copy);
		copy.setCard(card);

		// Clone attack without payments:
		gameModel.getGameModelParameters().setNoEnergyPayment(true);
		((PokemonCardScript) this.card.getCardScript()).executeAttack(attackName);
		gameModel.getGameModelParameters().setNoEnergyPayment(false);

		// Reset card script:
		this.card.setCardScript(this);
	}

	private void minimize() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();
		gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " is protects itself!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.HARDEN20);
		gameModel.sendGameModelToAllPlayers("");
	}
}
