package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00277_BrocksGeodude extends PokemonCardScript {

	public Script_00277_BrocksGeodude(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		this.addAttack("Call for Friend", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Hook Shot", att2Cost);
	}

	@Override
	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Call for Friend") && gameModel.getFullBenchPositions(this.getCardOwner().getColor()).size() == 5)
			return false;
		else
			return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Call for Friend"))
			this.CallforFriend();
		else
			this.HookShot();
	}

	private void CallforFriend() {
		ArrayList<Card> basicPokemon = gameModel.getBasicPokemonOnPosition(ownDeck());
		List<Card> brockBasicPokemon = new ArrayList<>();
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			for (Card c : basicPokemon)
				if (c instanceof PokemonCard && c.getCardType() == CardType.BASICPOKEMON && (c.getName().contains("Brock")))
					brockBasicPokemon.add(c);

			if (brockBasicPokemon.size() > 0) {
				Card chosenCard = this.getCardOwner().playerChoosesCards(brockBasicPokemon, 1, true, "Choose a pokemon to put on your bench!").get(0);
				Card realCard = gameModel.getCard(chosenCard.getGameID());
				gameModel.getAttackAction().putBasicPokemonOnBench(getCardOwner(), (PokemonCard) realCard);
			} else
				gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + "'s deck contains no Brock pokemon!", "");

			// Shuffle deck:
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
			gameModel.getAttackAction().shufflePosition(ownDeck());
		}
	}

	private void HookShot() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, false);
	}
}
