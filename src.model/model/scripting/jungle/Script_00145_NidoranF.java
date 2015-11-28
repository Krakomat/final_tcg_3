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

public class Script_00145_NidoranF extends PokemonCardScript {

	public Script_00145_NidoranF(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Fury Swipes", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Call for Family", att2Cost);
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
		if (attackName.equals("Fury Swipes"))
			this.furySwipes();
		else
			this.callforFamily();
	}

	private void furySwipes() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 3 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 10, true);
	}

	private void callforFamily() {
		ArrayList<Card> basicPokemon = gameModel.getBasicPokemonOnPosition(ownDeck());
		List<Card> bellsproutBasicPokemon = new ArrayList<>();
		for (Card c : basicPokemon)
			if (c instanceof PokemonCard && (c.getName().equals("Nidoran F") || c.getName().equals("Nidoran M")))
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
}
