package model.scripting.baseEdition;

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

public class Script_00038_Quaputzi extends PokemonCardScript {

	public Script_00038_Quaputzi(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		this.addAttack("Amnesia", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Doubleslap", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Amnesia"))
			this.amnesie();
		else
			this.duplexhieb();
	}

	private void amnesie() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		if (!defendingPokemon.hasCondition(PokemonCondition.INVULNERABLE)) {
			Player player = this.getCardOwner();

			// Choose attack:
			List<Card> attackOwner = new ArrayList<>();
			for (int i = 0; i < ((PokemonCardScript) defendingPokemon.getCardScript()).getAttackNames().size(); i++)
				attackOwner.add(defendingPokemon);
			String chosenAttack = player.playerChoosesAttacks(attackOwner, ((PokemonCardScript) defendingPokemon.getCardScript()).getAttackNames(), 1, true,
					"Choose an attack to block!").get(0);

			gameModel.sendTextMessageToAllPlayers(chosenAttack + " of " + defendingPokemon.getName() + " is blocked!", "");

			// Block attack:
			PokemonCard pokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
			PokemonCardScript cardScript = (PokemonCardScript) pokemon.getCardScript();
			cardScript.blockAttack(chosenAttack, 2);

			gameModel.sendGameModelToAllPlayers("");
		} else
			gameModel.sendTextMessageToAllPlayers("Amnesia has no effect on " + defendingPokemon.getName() + "!", "");
	}

	private void duplexhieb() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 2 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(2);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 30, true);
	}
}
