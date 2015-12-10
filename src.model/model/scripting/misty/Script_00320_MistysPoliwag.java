package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Triple;
import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00320_MistysPoliwag extends PokemonCardScript {

	public Script_00320_MistysPoliwag(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Bubbles", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		this.addAttack("Amnesia", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Bubbles"))
			this.Bubbles();
		else
			this.Amnesia();
	}

	private void Bubbles() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.TAILS) {
			gameModel.sendTextMessageToAllPlayers("Bubbles is blocked for " + getCardOwner().getName() + "'s next turn!", "");
			gameModel.getGameModelParameters().getBlockedAttacks().add(new Triple<Integer, String, Integer>(this.card.getGameID(), "Bubbles", 3));
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void Amnesia() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		if (!defendingPokemon.hasCondition(PokemonCondition.INVULNERABLE)) {
			Player player = this.getCardOwner();

			// Choose attack:
			List<Card> attackOwner = new ArrayList<>();
			for (int i = 0; i < ((PokemonCardScript) defendingPokemon.getCardScript()).getAttackNames().size(); i++)
				attackOwner.add(defendingPokemon);
			String chosenAttack = player
					.playerChoosesAttacks(attackOwner, ((PokemonCardScript) defendingPokemon.getCardScript()).getAttackNames(), 1, true, "Choose an attack to block!").get(0);

			gameModel.sendTextMessageToAllPlayers(chosenAttack + " of " + defendingPokemon.getName() + " is blocked!", "");

			gameModel.getGameModelParameters().getBlockedAttacks().add(new Triple<Integer, String, Integer>(defendingPokemon.getGameID(), chosenAttack, 2));
			gameModel.sendGameModelToAllPlayers("");
		} else
			gameModel.sendTextMessageToAllPlayers("Amnesia has no effect on " + defendingPokemon.getName() + "!", "");
	}
}
