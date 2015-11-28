package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00187_Krabby extends PokemonCardScript {

	public Script_00187_Krabby(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Call for Family", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Irongrip", att2Cost);
	}

	@Override
	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Call for Family") && gameModel.getFullBenchPositions(this.getCardOwner().getColor()).size() == 5)
			return false;
		else
			return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Call for Family"))
			this.callforFamily();
		else
			this.irongrip();
	}

	private void callforFamily() {
		ArrayList<Card> basicPokemon = gameModel.getBasicPokemonOnPosition(ownDeck());
		List<Card> bellsproutBasicPokemon = new ArrayList<>();
		for (Card c : basicPokemon)
			if (c instanceof PokemonCard && c.getName().equals("Krabby"))
				bellsproutBasicPokemon.add(c);

		if (bellsproutBasicPokemon.size() > 0) {
			Card chosenCard = this.getCardOwner().playerChoosesCards(bellsproutBasicPokemon, 1, true, "Choose a pokemon to put on your bench!").get(0);
			Card realCard = gameModel.getCard(chosenCard.getGameID());
			gameModel.getAttackAction().putBasicPokemonOnBench(getCardOwner(), (PokemonCard) realCard);
		} else
			gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + "'s deck contains no Bellsprout!", "");

		// Shuffle deck:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getAttackAction().shufflePosition(ownDeck());
	}

	private void irongrip() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
