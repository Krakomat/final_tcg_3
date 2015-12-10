package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00321_MistysPsyduck extends PokemonCardScript {

	public Script_00321_MistysPsyduck(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("ESP", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " flips 3 coins...", "");
		int heads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		switch (heads) {
		case 0:
			gameModel.sendTextMessageToAllPlayers("ESP does nothing!", "");
			break;
		case 1:
			gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " draws a card!", "");
			this.gameModel.getAttackAction().playerDrawsCards(1, getCardOwner());
			break;
		case 2:
			PositionID attacker = this.card.getCurrentPosition().getPositionID();
			PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
			Element attackerElement = ((PokemonCard) this.card).getElement();
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
			break;
		case 3:
			metronom();
			break;
		}
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
}
