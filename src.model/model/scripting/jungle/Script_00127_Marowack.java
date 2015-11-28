package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00127_Marowack extends PokemonCardScript {

	public Script_00127_Marowack(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Bonemerang", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Call for Friend", att2Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Call for Friend") && gameModel.getFullBenchPositions(this.getCardOwner().getColor()).size() == 5)
			return false;
		else
			return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Bonemerang"))
			this.bonemerang();
		else
			this.callforFriend();
	}

	private void bonemerang() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 2 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(2);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 30, true);
	}

	private void callforFriend() {
		ArrayList<Card> basicPokemon = gameModel.getBasicPokemonOnPosition(ownDeck());
		List<Card> rockBasicPokemon = new ArrayList<>();
		for (Card c : basicPokemon)
			if (c instanceof PokemonCard && ((PokemonCard) c).getElement() == Element.ROCK)
				rockBasicPokemon.add(c);

		if (rockBasicPokemon.size() > 0) {
			Card chosenCard = this.getCardOwner().playerChoosesCards(rockBasicPokemon, 1, true, "Choose a pokemon to put on your bench!").get(0);
			Card realCard = gameModel.getCard(chosenCard.getGameID());
			gameModel.getAttackAction().putBasicPokemonOnBench(getCardOwner(), (PokemonCard) realCard);
		} else
			gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + "'s deck contains no basic pokemon!", "");

		// Shuffle deck:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getAttackAction().shufflePosition(ownDeck());
	}
}
